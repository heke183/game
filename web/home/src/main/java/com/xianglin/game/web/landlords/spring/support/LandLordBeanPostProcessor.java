package com.xianglin.game.web.landlords.spring.support;

import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.xianglin.game.web.landlords.LongPollingLandlordServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LandLordBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(LandLordBeanPostProcessor.class);

    private final List<Class<? extends Annotation>> annotations =
                    Arrays.asList(OnConnect.class, OnDisconnect.class, OnEvent.class);

    @Resource(name = "landlordHallServer")
    private LongPollingLandlordServer landlordHallServer;

    @Resource(name = "landlordRoomServer")
    private LongPollingLandlordServer landlordRoomServer;

    private Class originalBeanClass;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (originalBeanClass != null) {
            landlordHallServer.getOriginServer().addListeners(bean, originalBeanClass);
            landlordRoomServer.getOriginServer().addListeners(bean, originalBeanClass);
            log.info("{} bean listeners added", beanName);
            originalBeanClass = null;
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final AtomicBoolean add = new AtomicBoolean();
        ReflectionUtils.doWithMethods(bean.getClass(),
                method -> add.set(true),
                method -> {
                    for (Class<? extends Annotation> annotationClass : annotations) {
                        if (method.isAnnotationPresent(annotationClass)) {
                            return true;
                        }
                    }
                    return false;
                });

        if (add.get()) {
            originalBeanClass = bean.getClass();
        }
        return bean;
    }

}