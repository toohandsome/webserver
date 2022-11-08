package com.ruoyi.webserver.webssh.pojo;

import lombok.Data;

/**
 * @Description: webssh数据传输
 * @Author: NoCortY
 * @Date: 2020/3/8
 */
@Data
public class HostData {

    private String host;
    private Integer port = 22;
    private Integer col = 80;
    private Integer row = 24;

    private String username;
    private String password;

}
