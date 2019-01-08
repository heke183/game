package com.xianglin.game.web.landlords;

import com.xianglin.game.web.landlords.config.LandlordServerConfig;
import com.xianglin.game.web.landlords.event.EventType;
import com.xianglin.game.web.landlords.handler.CloseWebSocketFrameHandler;
import com.xianglin.game.web.landlords.handler.TextWebSocketFrameHandler;
import com.xianglin.game.web.landlords.model.NamespaceType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultLandlordsServer implements LandlordsServer {

    private static final Logger logger = LoggerFactory.getLogger(LandlordsServer.class);

    private final LandlordServerConfig serverConfig;

    private final EventLoopGroup acceptor = new NioEventLoopGroup();
    private final EventLoopGroup worker = new NioEventLoopGroup();

    public DefaultLandlordsServer() {
        this(new LandlordServerConfig());
    }

    public DefaultLandlordsServer(LandlordServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public <T> void addEventListener(EventType eventType, T t) {

    }

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                serverConfig.getCodecWorkThreads(),
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ServerCodecThread#" + this.threadIndex.incrementAndGet());
                    }
                });

        serverBootstrap
                .group(acceptor, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getTcpSoSndbuf())
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getTcpSoRcvbuf())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(
                                defaultEventExecutorGroup
                                , new HttpServerCodec()
                                , new ChunkedWriteHandler()
                                , new HttpObjectAggregator(serverConfig.getHttpObjectAggregator())
                                , new WebSocketServerProtocolHandler("/ws")
                                , new TextWebSocketFrameHandler()
                                , new CloseWebSocketFrameHandler());
                    }
                });
        serverBootstrap.bind(serverConfig.getServerPort());
        logger.info("LandlordsServer start ...");
    }

    @Override
    public <T> T getOriginServer() {
        return null;
    }

    @Override
    public void shutdown() {
        acceptor.shutdownGracefully();
        worker.shutdownGracefully();
    }

    @Override
    public NamespaceType nameSpaceType() {
        return null;
    }
}
