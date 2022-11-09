package com.ruoyi.webserver.file.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mysgk
 */
@Data
public class ServerFile {

    private String name;
    private String dir;


    private List<String> fileList = new ArrayList<>();
    private List<String> dirList = new ArrayList<>();


    private String destName;
    private String destDir;


    private boolean isBatchOpt = false;

    /**
     * 0 文件
     * 1 目录
     */
    private int opTarget;

    /**
     * 0 复制
     * 1 剪切
     * 2 删除
     * 3 压缩
     * 4 解压
     */
    private int opType;
}
