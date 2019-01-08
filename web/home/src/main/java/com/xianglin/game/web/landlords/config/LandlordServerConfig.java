package com.xianglin.game.web.landlords.config;

public class LandlordServerConfig {

    public static final int CODEC_WORK_THREADS = 4;

    public static final int TCP_SO_SNDBUF = 65536;

    public static final int TCP_SO_RCVBUF = 65536;

    public static final int HTTP_OBJECT_AGGREGATOR = 65536;

    public static final int SERVER_PORT = 8111;

    public static final int SERVER_ROOM_PORT = 8112;

    public static final String HALL_NAME_SPACE = "/hall";

    public static final String ROOM_NAME_SPACE = "/room";

    private int codecWorkThreads = LandlordServerConfig.CODEC_WORK_THREADS;

    private int tcpSoSndbuf = LandlordServerConfig.TCP_SO_SNDBUF;

    private int tcpSoRcvbuf = LandlordServerConfig.TCP_SO_RCVBUF;

    private int httpObjectAggregator = LandlordServerConfig.HTTP_OBJECT_AGGREGATOR;

    private int serverPort = LandlordServerConfig.SERVER_PORT;

    private String hallNamespace = LandlordServerConfig.HALL_NAME_SPACE;

    private String roomNamespace = LandlordServerConfig.ROOM_NAME_SPACE;

    public int getCodecWorkThreads() {
        return codecWorkThreads;
    }

    public void setCodecWorkThreads(int codecWorkThreads) {
        this.codecWorkThreads = codecWorkThreads;
    }

    public int getTcpSoSndbuf() {
        return tcpSoSndbuf;
    }

    public void setTcpSoSndbuf(int tcpSoSndbuf) {
        this.tcpSoSndbuf = tcpSoSndbuf;
    }

    public int getTcpSoRcvbuf() {
        return tcpSoRcvbuf;
    }

    public void setTcpSoRcvbuf(int tcpSoRcvbuf) {
        this.tcpSoRcvbuf = tcpSoRcvbuf;
    }

    public int getHttpObjectAggregator() {
        return httpObjectAggregator;
    }

    public void setHttpObjectAggregator(int httpObjectAggregator) {
        this.httpObjectAggregator = httpObjectAggregator;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getHallNamespace() {
        return hallNamespace;
    }

    public void setHallNamespace(String hallNamespace) {
        this.hallNamespace = hallNamespace;
    }

    public String getRoomNamespace() {
        return roomNamespace;
    }

    public void setRoomNamespace(String roomNamespace) {
        this.roomNamespace = roomNamespace;
    }
}
