package com.ppdai.platform.das.codegen.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {

    public static String download(HttpServletResponse response, File f, String zipFileName, String codeGenFilePath) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream buff = null;
        OutputStream myout = null;
        String path = codeGenFilePath + "/" + zipFileName;
        File file = new File(path);

        try {

            if (f.isFile()) {
                zipFile(f, zipFileName, codeGenFilePath);
            } else {
                new ZipFolder(f.getAbsolutePath(), codeGenFilePath).zipIt(zipFileName);
            }

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
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
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
            }
        }

        return StringUtils.EMPTY;
    }


    private static void zipFile(File fileToZip, String zipFileName, String codeGenFilePath) throws Exception {
        byte[] buffer = new byte[1024];

        FileInputStream in = null;
        ZipOutputStream zos = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File(codeGenFilePath, zipFileName));
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
