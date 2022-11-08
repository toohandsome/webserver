package com.ruoyi.webserver.webssh.websocket;

import com.ruoyi.webserver.webssh.constant.ConstantPool;
import com.ruoyi.webserver.webssh.service.WebSSHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;


/**
 * @Description: WebSSH的WebSocket处理器
 * @Author: NoCortY
 * @Date: 2020/3/8
 */
@Component
@Slf4j
public class WebSSHWebSocketHandler implements WebSocketHandler {
    @Autowired
    private WebSSHService webSSHService;


    /**
     * @Description: 用户连接上WebSocket的回调
     * @Param: [webSocketSession]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession)  {
        log.info("用户:{},连接WebSSH", webSocketSession.getAttributes().get(ConstantPool.USER_IP_PORT));
        //调用初始化连接
        webSSHService.initConnection(webSocketSession);
    }

    /**
     * @Description: 收到消息的回调
     * @Param: [webSocketSession, webSocketMessage]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            log.info("用户:{},发送命令:{}", webSocketSession.getAttributes().get(ConstantPool.USER_IP_PORT), webSocketMessage.toString());
            //调用service接收消息
            webSSHService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
        } else if (webSocketMessage instanceof BinaryMessage) {
            log.warn("PongMessage");
        } else if (webSocketMessage instanceof PongMessage) {
            log.warn("PongMessage");
        } else {
            log.warn("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    /**
     * @Description: 出现错误的回调
     * @Param: [webSocketSession, throwable]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("handleTransportError: " + throwable.getMessage());
    }

    /**
     * @Description: 连接关闭的回调
     * @Param: [webSocketSession, closeStatus]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus)   {
        log.info("用户:{}断开webssh连接", String.valueOf(webSocketSession.getAttributes().get(ConstantPool.USER_IP_PORT)));
        webSSHService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
