package com.ruoyi.webserver.file.controller;

import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.webserver.file.entity.ServerFile;
import com.ruoyi.webserver.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static com.ruoyi.webserver.file.util.FileUtil.getFullPath;

@Controller
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    FileService fileService;

    @GetMapping("/getUserHome")
    public AjaxResult getUserHome() {
        return fileService.getUserHome();
    }

    @PostMapping("/jumpTo")
    public AjaxResult jumpTo(@RequestBody ServerFile serverFile) {
        return fileService.jumpTo(serverFile);
    }

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("path") String path) {
        return fileService.upload(multipartFile, path);
    }

    @PostMapping("/download")
    public void download(@RequestBody ServerFile serverFile, HttpServletResponse response) {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String realFileName = System.currentTimeMillis() + "_" + serverFile.getName();
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(getFullPath(serverFile), response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    @PostMapping("/newFile")
    public AjaxResult newFile(@RequestBody ServerFile serverFile) {
        return fileService.newFile(serverFile);
    }

    @PostMapping("/newDir")
    public AjaxResult newDir(@RequestBody ServerFile serverFile) {
        return fileService.newDir(serverFile);
    }

    @PostMapping("/copyOrCut")
    public AjaxResult copyOrCut(@RequestBody ServerFile serverFile) {
        return fileService.copyOrCut(serverFile);
    }

    @PostMapping("/delete")
    public AjaxResult delete(@RequestBody ServerFile serverFile) {
        return fileService.delete(serverFile);
    }

    @PostMapping("/compress")
    public AjaxResult compress(@RequestBody ServerFile serverFile) {
        return fileService.compress(serverFile);
    }

    @PostMapping("/decompression")
    public AjaxResult decompression(@RequestBody ServerFile serverFile) {
        return fileService.decompression(serverFile);
    }
}
