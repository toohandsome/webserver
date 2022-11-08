package com.ruoyi.webserver.webssh.constant;
import com.ruoyi.webserver.webssh.pojo.HostData;

public class ConstantPool {

    public static final String USER_IP_PORT = "user_ip_port";
    /**
     * 用户连接的信息
     */
    public static HostData SSH_DATA = null;
    /**
     * 发送指令：连接
     */
    public static final String WEBSSH_OPERATE_CONNECT = "connect";
    /**
     * 发送指令：命令
     */
    public static final String WEBSSH_OPERATE_COMMAND = "command";

    public static final String WEBSSH_OPERATE_RESIZE = "resize";
}
