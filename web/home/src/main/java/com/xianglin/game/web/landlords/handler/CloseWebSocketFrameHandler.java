package com.xianglin.game.web.landlords.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseWebSocketFrameHandler extends SimpleChannelInboundHandler<CloseWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(CloseWebSocketFrameHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CloseWebSocketFrame closeWebSocketFrame) throws Exception {
        logger.debug("close socket: {}", closeWebSocketFrame.toString());
    }
}