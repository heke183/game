package com.xianglin.game.web.landlords;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.web.landlords.config.LandlordServerConfig;
import com.xianglin.game.web.landlords.event.EventType;
import com.xianglin.game.web.landlords.model.NamespaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * socket.io support
 *
 * @author yefei
 */
public class LongPollingLandlordServer implements LandlordsServer {

    private static final Logger logger = LoggerFactory.getLogger(LongPollingLandlordServer.class);

    private final LandlordServerConfig serverConfig;

    private final SocketIOServer server;

    private final NamespaceType nameSpaceType;

    public LongPollingLandlordServer() {
        this(new LandlordServerConfig(), new Configuration(), NamespaceType.hall);
    }

    public LongPollingLandlordServer(LandlordServerConfig serverConfig, Configuration config, NamespaceType nameSpaceType) {
        this.serverConfig = serverConfig;
        config.setPort(serverConfig.getServerPort());
        this.server = new SocketIOServer(config);
        this.nameSpaceType = nameSpaceType;
    }

    @Override
    public <T> void addEventListener(EventType eventType, T t) {
        // TODO
        Class clazz = Object.class;
        for (Type genericInterface : t.getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericInterface;
                if (type.getRawType() == DataListener.class) {
                    clazz = (Class) type.getActualTypeArguments()[0];
                }
            }
        }
        String[] namespaces = eventType.getNamespace();
        if (namespaces != null && namespaces.length > 0) {
            for (String namespace : namespaces) {
                if (namespace.equals(nameSpaceType.getNamespace())) {
                    SocketIONamespace socketIONamespace = server.getNamespace(namespace);
                    socketIONamespace.addEventListener(eventType.getType(), clazz, (DataListener) t);
                }
            }
        } else {
            server.addEventListener(eventType.getType(), clazz, (DataListener) t);
        }
    }

    @Override
    public SocketIOServer getOriginServer() {
        return server;
    }

    @Override
    public void start() {
        server.addNamespace(nameSpaceType.getNamespace());
        server.start();

        logger.info("------- namespace: {} server start!", nameSpaceType.getNamespace());
    }

    @Override
    public void shutdown() {
        server.stop();
    }

    @Override
    public NamespaceType nameSpaceType() {
        return nameSpaceType;
    }
}
