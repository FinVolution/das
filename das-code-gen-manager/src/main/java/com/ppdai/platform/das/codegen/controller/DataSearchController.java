package com.ppdai.platform.das.codegen.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.local.ClientDasDelegate;
import com.ppdai.das.core.enums.DatabaseCategory;
import com.ppdai.platform.das.codegen.api.DataSearchConfiguration;
import com.ppdai.platform.das.codegen.common.utils.JavaIOUtils;
import com.ppdai.platform.das.codegen.common.utils.JsonUtil;
import com.ppdai.platform.das.codegen.common.utils.SQLUtils;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dao.DataSearchLogDao;
import com.ppdai.platform.das.codegen.dao.ProjectDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.dataSearch.DataSearchRequest;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.DataSearchLogView;
import com.ppdai.platform.das.codegen.enums.DataSearchTypeEnum;
import com.ppdai.platform.das.codegen.service.DataSearchService;
import com.ppdai.platform.das.codegen.service.DatabaseSetEntryService;
import com.ppdai.platform.das.codegen.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ppdai.platform.das.codegen.dto.model.ServiceResult.ERROR;

@Slf4j
@RestController
@RequestMapping(value = "/dataSearch")
public class DataSearchController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DataSearchLogDao dataSearchLogDao;

    @Autowired
    private DataSearchService dataSearchService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DatabaseSetEntryService databaseSetEntryService;

    @Autowired
    private DataSearchConfiguration dataSearchConfiguration;

    @Autowired
    private Consts consts;

    static final String NEW_LINE = System.getProperty("line.separator");

    static final Joiner COMMA_JOIN = Joiner.on(",");

    @RequestMapping(value = "/select")
    public ServiceResult select(@RequestBody DataSearchRequest dataSearchRequest, HttpServletRequest request, @CurrentUser LoginUser user) throws Exception {
        if (!permissionService.isManagerById(user.getId())) {
            return ServiceResult.fail("此接口仅供管理员使用！！");
        }
        ServiceResult sr = query(dataSearchRequest, Functions.identity());
        if (sr.getCode() == ServiceResult.SUCCESS) {
            dataSearchService.addLog(request, dataSearchRequest, user, DataSearchTypeEnum.SELECT.getType(), true, "结果条数:" + ((List) sr.getMsg()).size());
        } else {
            Map map = (ImmutableMap) sr.getMsg();
            dataSearchService.addLog(request, dataSearchRequest, user, DataSearchTypeEnum.SELECT.getType(), false, JsonUtil.toJSONString(map.get("exception")));
        }
        return sr;
    }

    @RequestMapping(value = "/searchlog")
    public ServiceResult searchlog(@RequestParam(value = "limit", defaultValue = "100") Integer limit) throws SQLException {
        return ServiceResult.success(dataSearchLogDao.findDataSearchLogList(limit));
    }

    /**
     * 1、翻页查询LOG
     */
    @RequestMapping(value = "/log/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DataSearchLogView>> list(@RequestBody Paging<DataSearchLogView> paging, @CurrentUser LoginUser user) throws SQLException {
        if (!permissionService.isManagerById(user.getId())) {
            return ServiceResult.fail("此接口仅供管理员使用！！");
        }
        return ServiceResult.success(dataSearchService.findLogPageList(paging));
    }

    private ServiceResult query(DataSearchRequest dataSearchRequest, Function<List<Map>, List<Map>> handler) throws Exception {
        ExecutorService es = null;
        List<String> shardings = null;
        String appId = "";

        try {
            shardings = databaseSetEntryService.getNamesByEntryIds(dataSearchRequest.getDbSetEntryIds());
            List<Project> projects = projectDao.getProjectBydbsetId((long)dataSearchRequest.getDbSetId());
            Preconditions.checkArgument(!projects.isEmpty(), "Cannot find app id for logic db: " + dataSearchRequest.getDbSetId());
            appId = Iterables.getFirst(projects, null).getApp_id() + "";

            DatabaseCategory category = setUp(appId, dataSearchRequest.getDbsetName());

            List<Future<List<Map>>> futures = new ArrayList<>();
            String appId2pass = appId;
            String finalSQL = SQLUtils.checkSql(dataSearchRequest.getSql(), category);

            es = Executors.newCachedThreadPool();
            CompletionService<List<Map>> cService = new ExecutorCompletionService<>(es);

            for (String sh : shardings.isEmpty() ? Lists.newArrayList("") : shardings) {
                for(String tableShardId : dataSearchRequest.getTableShardIds().isEmpty() ? Lists.newArrayList("") : dataSearchRequest.getTableShardIds()) {
                    String sqlWithTableShard = SQLUtils.withTableShard(finalSQL, tableShardId, category);
                    Future f = cService.submit(() -> doQuery(appId2pass, dataSearchRequest.getDbsetName(), sqlWithTableShard, sh));
                    futures.add(f);
                }
            }

            List list = new ArrayList();
            for (Future<List<Map>> f : futures) {
                List<Map> shardResult = f.get();
                List<Map> applied = handler.apply(shardResult);
                list.addAll(applied);
            }

            return ServiceResult.success(list);
        } catch (Exception e) {
            return ServiceResult.fail(
                    ImmutableMap.builder()
                            .put("request", dataSearchRequest.toString())
                            .put("exception", e.toString())
                            .put("appId", appId)
                            .put("shard", Objects.toString(shardings))
                            .put("StackTrace", Arrays.toString(e.getStackTrace())).build()
            );
        } finally {
            if (es != null && !es.isShutdown()) {
                es.shutdown();
            }
            cleanUp(appId);
        }
    }

    @RequestMapping(value = "/download/{dbsetName}/{dbSetId}")
    public String download(@PathVariable String dbsetName,
                           @PathVariable Integer dbSetId,
                           @RequestParam("dbSetEntryIds") String dbSetEntryIds,
                           @RequestParam("sql") String sql,
                           @RequestParam("limit") Integer limit,
                           HttpServletRequest request, HttpServletResponse response, @CurrentUser LoginUser user) throws Exception {
        if (!permissionService.isManagerById(user.getId())) {
            return new Gson().toJson(ServiceResult.fail("此接口仅供管理员使用！！"));
        }
        Path pathToDelete = null;
        FileInputStream fis = null;
        BufferedInputStream buff = null;
        OutputStream myout = null;
        try {
            List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(dbSetEntryIds);
            List<Integer> dbSetEntryIdList = list.stream().map(Integer::valueOf).collect(Collectors.toList());
            DataSearchRequest dataSearchRequest = DataSearchRequest.builder()
                    .dbSetId(dbSetId)
                    .dbsetName(dbsetName)
                    .dbSetEntryIds(dbSetEntryIdList)
                    .sql(sql)
                    .limit(limit)
                    .build();

            ServiceResult serviceResult = query(dataSearchRequest, Functions.identity());

            if (serviceResult.getCode() == ServiceResult.SUCCESS) {
                dataSearchService.addLog(request, dataSearchRequest, user, DataSearchTypeEnum.DOWNLOAD.getType(), true, "limit:" + ((List) serviceResult.getMsg()).size());
            } else {
                Map map = (ImmutableMap) serviceResult.getMsg();
                dataSearchService.addLog(request, dataSearchRequest, user, DataSearchTypeEnum.DOWNLOAD.getType(), false, JsonUtil.toJSONString(map.get("exception")));
            }

            if (serviceResult.getCode() == ERROR) {
                return new Gson().toJson(serviceResult);
            }

            Files.createDirectories(Paths.get(consts.dbaToolFilePath));
            final Path path = Paths.get(consts.dbaToolFilePath, dbsetName + ".csv");
            path.toFile().createNewFile(); // if file already exists will do nothing
            pathToDelete = path;

            List<Map<String, String>> result = (List<Map<String, String>>) serviceResult.getMsg();
            List<String> colNames = new ArrayList<>();
            result.forEach(row -> {
                        //Write column name
                        if (colNames.isEmpty()) {
                            colNames.addAll(row.keySet());
                            try {
                                List<String> colNamesWithQuote = colNames.stream().map(this::withQuote).collect(Collectors.toList());
                                String titles = COMMA_JOIN.join(colNamesWithQuote) + NEW_LINE;
                                Files.write(path, titles.getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }

                        List<String> rowData = colNames
                                .stream()
                                .map(colName -> withQuote(Objects.toString(row.get(colName), "")))
                                .collect(Collectors.toList());
                        String data = COMMA_JOIN.join(rowData) + NEW_LINE;
                        try {
                            Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
            );

            File f = path.toFile();
            final String zipFileName = dataSearchRequest.getDbsetName() + "-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";

            if (f.isFile()) {
                zipFile(f, zipFileName);
            }

            File file = Paths.get(consts.dbaToolFilePath, zipFileName).toFile();
            if (!file.exists()) {
                response.sendError(404, "File not found!");
                return StringUtils.EMPTY;
            } else {
                response.setContentType("application/zip;charset=utf-8");
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes(Charsets.UTF_8), "UTF-8"));
            }
            // response.reset();
            fis = new FileInputStream(file);
            buff = new BufferedInputStream(fis);
            byte[] b = new byte[1024];
            long k = 0;
            myout = response.getOutputStream();

            while (k < file.length()) {
                int j = buff.read(b, 0, 1024);
                k += j;
                myout.write(b, 0, j);
            }
            myout.flush();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pathToDelete != null) {
                    pathToDelete.toFile().delete();
                }

                if (fis != null) {
                    fis.close();
                }
                if (buff != null) {
                    buff.close();
                }
                if (myout != null) {
                    myout.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return StringUtils.EMPTY;
    }

    String withQuote(String s) {
        return "\"" + s + "\"";
    }

    private List doQuery(String appId, String logicDbName, String sql, String shard) {
        SqlBuilder sqlBuilder = new SqlBuilder().appendTemplate(sql).intoMap();
        Hints hints = sqlBuilder.hints();
        if (!shard.isEmpty()) {
            hints.inShard(shard);
        }

        ClientDasDelegate dasDelegate = new ClientDasDelegate(appId, logicDbName, "CustomerDasClientVersion");
        try {
            return dasDelegate.query(sqlBuilder);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    DatabaseCategory setUp(String appId, String dbName) throws Exception {
       return dataSearchConfiguration.setUp(appId, dbName);
    }

    void cleanUp(String appId) throws Exception {
        dataSearchConfiguration.cleanUp(appId);
    }

    private void zipFile(File fileToZip, String zipFileName) throws Exception {
        byte[] buffer = new byte[1024];

        FileInputStream in = null;
        ZipOutputStream zos = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File(consts.dbaToolFilePath, zipFileName));
            zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(fileToZip.getName());
            zos.putNextEntry(ze);
            in = new FileInputStream(fileToZip);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            JavaIOUtils.closeInputStream(in);
            JavaIOUtils.closeOutputStream(zos);
        }
    }

}
