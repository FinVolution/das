package com.ppdai.das.console.controller;

import com.ppdai.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.das.console.common.codeGen.generator.java.context.JavaCodeGenContext;
import com.ppdai.das.console.common.codeGen.generator.java.generator.JavaDasGenerator;
import com.ppdai.das.console.common.codeGen.resource.ProgressResource;
import com.ppdai.das.console.common.user.UserContext;
import com.ppdai.das.console.common.utils.DateUtil;
import com.ppdai.das.console.common.utils.FileUtils;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.codeGen.Progress;
import com.ppdai.das.console.dto.entry.codeGen.W2uiElement;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.GenerateCodeModel;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.CodeService;
import com.ppdai.das.console.constant.Consts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/code")
public class CodeController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private Consts consts;

    @Autowired
    private CodeService codeService;


    @RequestMapping(value = "/count")
    public ServiceResult countProject(@RequestParam(value = "projectId", defaultValue = "0") Long projectId) throws SQLException {
        Boolean bool = codeService.isTaskCountByProjectId(projectId);
        return ServiceResult.success(bool);
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public ServiceResult generateProject(@RequestBody GenerateCodeModel generateCodeRequest, HttpServletRequest request) {
        Long project_id = generateCodeRequest.getProjectId();
        Progress progress = null;
        LoginUser user = UserContext.getUser(request);
        try {
            progress = ProgressResource.getProgress(user.getUserNo(), project_id, StringUtils.EMPTY);

            String code = CodeGenConsts.JAVA;
            JavaDasGenerator generator = new JavaDasGenerator();
            JavaCodeGenContext context = generator.createContext(project_id, true, progress);
            context.setGeneratePath(consts.codeConsoleilePath);

            log.info(String.format("Begin to generate java task for project %s", project_id));
            generateLanguageProject(generator, context);
            log.info(String.format("Java task for project %s generated.", project_id));

            return ServiceResult.success(code);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            return ServiceResult.fail("表结构发生变化，请删除变更表再新建表实体！");
        } catch (Throwable e) {
            e.printStackTrace();
            return ServiceResult.fail(e.getMessage());
        } finally {
            progress.setStatus(ProgressResource.FINISH);
        }
    }

    @RequestMapping(value = "/files")
    public ServiceResult<List<W2uiElement>> getFiles(@RequestParam(value = "projectId", defaultValue = "") String projectId,
                                                     @RequestParam(value = "name", defaultValue = "") String name) {

        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(name)) {
            return ServiceResult.fail("projectId 或 name 为空!!!");
        }
        List<W2uiElement> files = new ArrayList<>();

        File currentProjectDir = new File(new File(consts.codeConsoleilePath, projectId), CodeGenConsts.JAVA);
        if (currentProjectDir.exists()) {
            File currentFile;
            if (StringUtils.isBlank(name)) {
                currentFile = currentProjectDir;
            } else {
                currentFile = new File(currentProjectDir, name);
            }
            for (File f : currentFile.listFiles()) {
                W2uiElement element = new W2uiElement();
                if (null == name || name.isEmpty()) {
                    element.setId(String.format("%s_%d", projectId, files.size()));
                } else {
                    element.setId(String.format("%s_%s_%d", projectId, name.replace("\\", ""), files.size()));
                }
                if (null == name || name.isEmpty()) {
                    element.setData(f.getName());
                } else {
                    element.setData(name + File.separator + f.getName());
                }
                element.setText(f.getName());
                element.setChildren(f.isDirectory());
                if (element.isChildren()) {
                    element.setType("folder");
                } else {
                    element.setType("file");
                }
                files.add(element);
            }
        }
        return ServiceResult.success(files);
    }

    @RequestMapping(value = "/content")
    public ServiceResult<String> getFileContent(@RequestParam(value = "projectId", defaultValue = "") String projectId,
                                                @RequestParam(value = "name", defaultValue = "") String name) throws Exception {
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(name)) {
            return ServiceResult.fail("projectId 或 name 为空!!!");
        }
        File f = new File(new File(new File(consts.codeConsoleilePath, projectId), CodeGenConsts.JAVA), name);
        StringBuilder sb = new StringBuilder();
        if (f.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
                throw e;
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ServiceResult.success(sb.toString());
    }

    @RequestMapping("/download")
    public String download(@RequestParam(value = "projectId") Long projectId, HttpServletResponse response) throws Exception {
        File file = new File(new File(consts.codeConsoleilePath, String.valueOf(projectId)), CodeGenConsts.JAVA);
        Project project = projectDao.getProjectByID(projectId);
        String date = DateUtil.getCurrentTime();
        final String zipFileName = project.getName() + "-" + date + ".zip";
        return FileUtils.download(response, file, zipFileName, consts.codeConsoleilePath);
    }

    @RequestMapping(value = "/clearFiles")
    public ServiceResult clearFiles(@RequestParam(value = "projectId") Integer projectId) {
        try {
            String path = consts.codeConsoleilePath;
            File dir = new File(String.format("%s/%s", path, projectId));
            if (dir.exists()) {
                try {
                    org.apache.commons.io.FileUtils.forceDelete(dir);
                } catch (IOException e) {
                }
            }
            return ServiceResult.success();
        } catch (Throwable e) {
            return ServiceResult.fail(e.getMessage());
        }
    }


    private void generateLanguageProject(JavaDasGenerator generator, JavaCodeGenContext context) throws Exception {
        if (generator == null || context == null) {
            return;
        }
        generator.prepareDirectory(context);
        generator.prepareData(context);
        generator.generateCode(context);
    }
}
