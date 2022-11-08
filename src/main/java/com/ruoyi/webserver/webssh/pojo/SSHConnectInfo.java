package com.ruoyi.webserver.webssh.pojo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
* @Description: ssh连接信息
* @Author: NoCortY
* @Date: 2020/3/8
*/
@Data
public class SSHConnectInfo {

    private ChannelShell channelShell;
    private WebSocketSession webSocketSession;
    private JSch jSch;
    private String channelType;

}
