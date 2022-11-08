package com.ruoyi.webserver.file.util;


import com.ruoyi.webserver.file.entity.ServerFile;

public class FileUtil {

    public static String getFullPath(ServerFile serverFile) {
        return serverFile.getDir() + java.io.File.separator + serverFile.getName();
    }

    public static String getDestFullPath(ServerFile serverFile) {
        return serverFile.getDestDir() + java.io.File.separator + serverFile.getDestName();
    }

    public static boolean isOptFile(ServerFile serverFile) {
        return serverFile.getOpTarget() == 0;
    }

    public static boolean isCopy(ServerFile serverFile) {
        return serverFile.getOpType() == 0;
    }

    public static boolean isCut(ServerFile serverFile) {
        return serverFile.getOpType() == 1;
    }

    public static boolean isDel(ServerFile serverFile) {
        return serverFile.getOpType() == 2;
    }

}
