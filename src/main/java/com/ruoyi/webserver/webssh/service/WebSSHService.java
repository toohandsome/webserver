package com.ruoyi.webserver.webssh.service;


import com.ruoyi.webserver.webssh.pojo.HostData;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: WebSSH的业务逻辑
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
@Service
public interface WebSSHService {
    /**
     * @Description: 初始化ssh连接
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    void initConnection(WebSocketSession session);

    /**
     * @Description: 处理客户段发的数据
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    void recvHandle(String buffer, WebSocketSession session);

    /**
     * @Description: 数据写回前端 for websocket
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    void sendMessage(WebSocketSession session, byte[] buffer) throws IOException;

    /**
     * @Description: 关闭连接
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    void close(WebSocketSession session);

    /**
     * @Description: 测试连接
     * @Param: 主机信息
     * @return: 是否能够连接成功
     * @Author: fuchengjie
     * @Date: 2022-8-15 19:00:05
     */
    Map<String, String> testConnect(HostData data);


}
