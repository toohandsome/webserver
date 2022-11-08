package com.ruoyi.webserver.file.service.impl;

import cn.hutool.core.util.RuntimeUtil;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.webserver.file.entity.ServerFile;
import com.ruoyi.webserver.file.service.FileService;
import com.ruoyi.webserver.file.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mysgk
 */
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public AjaxResult getUserHome() {
        String property = System.getProperty("user.home");
        if (!StringUtils.hasText(property)) {
            property = "/root";
        }
        String ret = "";
        try {
            ret = RuntimeUtil.execForStr("ls -l -a" + property);
        } catch (Exception e) {
            ret = RuntimeUtil.execForStr("ls -l" + property);
        }
        return AjaxResult.success(ret);
    }

    @Override
    public AjaxResult jumpTo(ServerFile serverFile) {
        String ret = "";
        try {
            ret = RuntimeUtil.execForStr("ls -l -a" + serverFile.getDir());
        } catch (Exception e) {
            ret = RuntimeUtil.execForStr("ls -l" + serverFile.getDir());
        }
        return AjaxResult.success(ret);
    }

    @Override
    public AjaxResult upload(MultipartFile multipartFile, String path) {
        try {
            String fileName = FileUploadUtils.extractFilename(multipartFile);
            String absPath = FileUploadUtils.getAbsoluteFile(path, fileName).getAbsolutePath();
            multipartFile.transferTo(Paths.get(absPath));
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Override
    public AjaxResult newFile(ServerFile serverFile) {
        File realFile = new File(FileUtil.getFullPath(serverFile));
        try {
            if (realFile.getParentFile().mkdirs()) {
                realFile.createNewFile();
                return AjaxResult.success();
            }
            return AjaxResult.error("创建目录" + realFile.getParentFile() + "失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Override
    public AjaxResult newDir(ServerFile serverFile) {
        File realFile = new File(serverFile.getDir());
        try {
            realFile.mkdirs();
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Override
    public AjaxResult copyOrCut(ServerFile serverFile) {
        String cmd = "";

        if (serverFile.isBatchOpt()) {
            List<String> pathList = new ArrayList<>();
            pathList.addAll(serverFile.getFileList());
            pathList.addAll(serverFile.getDirList());
            String cmdPrefix = "";
            if (FileUtil.isCopy(serverFile)) {
                cmdPrefix = "cp ";
            } else if (FileUtil.isCut(serverFile)) {
                cmdPrefix = "rm -rf ";
            }

            for (String path : pathList) {
                cmd += cmdPrefix + path + " " + serverFile.getDestDir() + " \n";
            }
        } else {
            // 复制文件
            if (FileUtil.isCopy(serverFile)) {
                cmd += "cp -r " + (FileUtil.isOptFile(serverFile) ? FileUtil.getFullPath(serverFile) : serverFile.getDir()) + " " + serverFile.getDestDir() + " \n";
            }
            // 剪切文件
            else if (FileUtil.isCut(serverFile)) {
                cmd += "mv -r " + (FileUtil.isOptFile(serverFile) ? FileUtil.getFullPath(serverFile) : serverFile.getDir()) + " " + serverFile.getDestDir() + " \n";
            } else {
                return AjaxResult.error("类型错误");
            }
        }

        log.info("cmd:  " + cmd);
        try {
            RuntimeUtil.execForStr(cmd);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Override
    public AjaxResult delete(ServerFile serverFile) {
        String cmd = "";
        if (serverFile.isBatchOpt()) {
            List<String> pathList = new ArrayList<>();
            pathList.addAll(serverFile.getFileList());
            pathList.addAll(serverFile.getDirList());
            for (String path : pathList) {
                cmd += "rm -rf " + path + " \n";
            }
        } else {
            cmd += "rm -rf " + (FileUtil.isOptFile(serverFile) ? FileUtil.getFullPath(serverFile) : serverFile.getDir());
        }

        log.info("cmd:  " + cmd);
        try {
            RuntimeUtil.execForStr(cmd);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Override
    public AjaxResult compress(ServerFile serverFile) {
        return null;
    }

    @Override
    public AjaxResult decompression(ServerFile serverFile) {
        return null;
    }
}
