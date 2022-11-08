package com.ruoyi.webserver.file.service;


import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.webserver.file.entity.ServerFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: WebSSH的业务逻辑
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
@Service
public interface FileService {


    AjaxResult getUserHome();

    AjaxResult jumpTo(ServerFile serverFile);

    AjaxResult upload(MultipartFile multipartFile, String path);

    AjaxResult newFile(ServerFile serverFile);

    AjaxResult newDir(ServerFile serverFile);

    AjaxResult copyOrCut(ServerFile serverFile);

    AjaxResult delete(ServerFile serverFile);

    AjaxResult compress(ServerFile serverFile);

    AjaxResult decompression(ServerFile serverFile);
}
