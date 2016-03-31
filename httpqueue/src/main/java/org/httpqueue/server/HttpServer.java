package org.httpqueue.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.httpqueue.util.PropertiesStr;

import java.util.Properties;

public class HttpServer {

    private static Logger log = Logger.getLogger(HttpServer.class);

    public void startinbound(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpServerInboundHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public void startoutbound(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpServerOutboundHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
        PropertiesStr propertiesStr=new PropertiesStr();
        propertiesStr.initProperties();
        Thread inbound=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpServer serverIn = new HttpServer();
                log.info("HttpQueue Server listening on "+PropertiesStr.inBound+" ...");
                try {
                    serverIn.startinbound(PropertiesStr.inBound);
                } catch (Exception e) {
                    log.error("Inbound Server crash!!!",e);
                    System.exit(1);
                }
            }
        });
        Thread outbound=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpServer serverOut = new HttpServer();
                log.info("HttpQueue Server listening on "+PropertiesStr.outBound+" ...");
                try {
                    serverOut.startoutbound(PropertiesStr.outBound);
                } catch (Exception e) {
                    log.error("Outbound Server crash!!!",e);
                    System.exit(1);
                }
            }
        });
        inbound.start();
        outbound.start();
    }
}