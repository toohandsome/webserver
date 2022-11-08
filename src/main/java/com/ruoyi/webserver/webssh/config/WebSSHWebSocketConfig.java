package com.ruoyi.webserver.webssh.config;

import com.ruoyi.webserver.webssh.constant.ConstantPool;

import com.ruoyi.webserver.webssh.websocket.WebSSHWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Map;

/**
 * @Description: websocket配置
 * @Author: NoCortY
 * @Date: 2020/3/8
 */
@Configuration
@EnableWebSocket
public class WebSSHWebSocketConfig implements WebSocketConfigurer {
    @Resource
    WebSSHWebSocketHandler webSSHWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "/webssh")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

                        if (serverHttpRequest instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
                            InetAddress remoteAddress = request.getRemoteAddress().getAddress();
                            String hostAddress = remoteAddress.getHostAddress() + ":" + request.getRemoteAddress().getPort();
                            attributes.put(ConstantPool.USER_IP_PORT, hostAddress);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .setAllowedOrigins("*");
    }
}
