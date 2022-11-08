package com.ruoyi.webserver.webssh.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.ruoyi.webserver.webssh.constant.ConstantPool;
import com.ruoyi.webserver.webssh.pojo.HostData;
import com.ruoyi.webserver.webssh.pojo.SSHConnectInfo;
import com.ruoyi.webserver.webssh.service.WebSSHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @Description: WebSSH业务逻辑实现
 * @Author: NoCortY
 * @Date: 2020/3/8
 */
@Service
@Slf4j
public class WebSSHServiceImpl implements WebSSHService {

    private Map<String, Object> sshMap = new ConcurrentHashMap<>();
    private ThreadFactory springThreadFactory = new CustomizableThreadFactory("webssh-pool-");

    private ExecutorService executorService = new ThreadPoolExecutor(5, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(), springThreadFactory, new ThreadPoolExecutor.DiscardPolicy());

    /**
     * @Description: 初始化连接
     * @Param: [session]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Override
    public void initConnection(WebSocketSession session) {
        JSch jSch = new JSch();
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo();
        sshConnectInfo.setJSch(jSch);
        sshConnectInfo.setWebSocketSession(session);
        String uuid = String.valueOf(session.getAttributes().get(ConstantPool.USER_IP_PORT));
        //将这个ssh连接信息放入map中
        sshMap.put(uuid, sshConnectInfo);
    }

    /**
     * @Description: 处理客户端发送的数据
     * @Param: [buffer, session]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Override
    public void recvHandle(String buffer, WebSocketSession session) {


        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_IP_PORT));
        if (buffer.contains("host")) {
            ObjectMapper objectMapper = new ObjectMapper();
            HostData webSSHData = null;
            try {
                webSSHData = objectMapper.readValue(buffer, HostData.class);
            } catch (IOException e) {
                log.error("Json转换异常,异常信息:{}", e.getMessage());
                return;
            }
            //找到刚才存储的ssh连接对象
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            //启动线程异步处理
            HostData finalWebSSHData = webSSHData;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean res = connectToSSH(sshConnectInfo, finalWebSSHData, session);
                        log.info("webssh连接状态" + res);
                    } catch (JSchException | IOException e) {
                        log.error("webssh连接异常,异常信息:{}", e.getMessage());
                        close(session);
                    }
                }
            });
        } else if (buffer.contains("resize")) {
            String[] split = buffer.split(",");

            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            if (sshConnectInfo != null) {
                ChannelShell channel = sshConnectInfo.getChannelShell();
                if (channel != null) {
                    channel.setPtySize(Integer.valueOf(split[1]), Integer.valueOf(split[2]), 0, 0);
                }

            } else {
                log.error("未找到当前用户连接: " + userId);
            }
        } else {
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            if (sshConnectInfo != null && sshConnectInfo.getChannelShell()!=null) {
                try {
                    transToSSH(sshConnectInfo.getChannelShell(), buffer);
                } catch (IOException e) {
                    log.error("webssh连接异常");
                    log.error("异常信息:{}", e.getMessage());
                    close(session);
                }
            } else {
                log.error("未找到当前用户连接: " + userId);
            }

        }
    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        log.info("sendMessage to web ,tname:" + Thread.currentThread().getName());
        session.sendMessage(new TextMessage(buffer));
    }

    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_IP_PORT));
        SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
        if (sshConnectInfo != null) {
            //断开连接
            if (sshConnectInfo.getChannelShell() != null) {
                sshConnectInfo.getChannelShell().disconnect();
            }
            //map中移除
            sshMap.remove(userId);
        }
    }

    @Override
    public Map<String, String> testConnect(HostData data) {
        String username = data.getUsername();
        String password = data.getPassword();
        String host = data.getHost();
        int port = data.getPort();

        // 创建JSch对象
        JSch jSch = new JSch();
        Session jSchSession = null;
        Map<String, String> map = new HashMap<>();
        map.put("res", "");
        map.put("msg", "");
        boolean res = false;

        try {
            // 根据主机账号、ip、端口获取一个Session对象
            jSchSession = jSch.getSession(username, host, port);

            // 存放主机密码
            jSchSession.setPassword(password);

            Properties config = new Properties();

            // 去掉首次连接确认
            config.put("StrictHostKeyChecking", "no");

            jSchSession.setConfig(config);

            // 超时连接时间为3秒
            jSchSession.setTimeout(3000);

            // 进行连接
            jSchSession.connect();

            // 获取连接结果
            res = jSchSession.isConnected();
            map.put("res", String.valueOf(res));
        } catch (JSchException e) {
            log.warn(e.getMessage());
            map.put("msg", e.getMessage());
        } finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
            if (res) {
                log.info("测试SSH连接: " + host + " 连接成功");
            } else {
                log.error("测试SSH连接: " + host + " 连接失败");
            }

            // 返回到前端的数据
            return map;
        }
    }

    /**
     * @Description: 使用jsch连接终端
     * @Param: [cloudSSH, webSSHData, webSocketSession]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    private boolean connectToSSH(SSHConnectInfo sshConnectInfo, HostData webSSHData, WebSocketSession webSocketSession) throws JSchException, IOException {
        Session session = null;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //获取jsch的会话
        session = sshConnectInfo.getJSch().getSession(webSSHData.getUsername(), webSSHData.getHost(), webSSHData.getPort());
        session.setConfig(config);
        //设置密码
        session.setPassword(webSSHData.getPassword());
        //连接  超时时间30s
        session.connect(30000);

        //开启shell通道
        ChannelShell channel = (ChannelShell) session.openChannel("shell");


        //通道连接 超时时间3s
        channel.connect(3000);
        channel.setPtySize(webSSHData.getCol(), webSSHData.getRow(), 0, 0);

        // 获取连接状态
        boolean res = channel.isConnected();

        //设置channel
        sshConnectInfo.setChannelShell(channel);

        //转发消息
        transToSSH(channel, "\r");

        //读取终端返回的信息流
        InputStream inputStream = channel.getInputStream();
        try {
            //循环读取
            byte[] buffer = new byte[1024];
            int i = 0;
            //线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            // 此方法不会调用,只有线程结束才会运行
            session.disconnect();
            channel.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return res;
    }

    /**
     * @Description: 将消息转发到终端
     * @Param: [channel, data]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    private void transToSSH(Channel channel, String command) throws IOException {
        log.info("trans commadn to SSH  ,tname:" + Thread.currentThread().getName());
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }
}
