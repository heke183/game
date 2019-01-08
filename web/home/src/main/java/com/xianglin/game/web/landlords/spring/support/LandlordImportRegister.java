package com.xianglin.game.web.landlords.spring.support;

import com.corundumstudio.socketio.Configuration;
import com.xianglin.game.web.landlords.LongPollingLandlordServer;
import com.xianglin.game.web.landlords.config.LandlordServerConfig;
import com.xianglin.game.web.landlords.event.EventHandler;
import com.xianglin.game.web.landlords.event.EventType;
import com.xianglin.game.web.landlords.model.NamespaceType;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

public class LandlordImportRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EventHandler.class));

        Set<BeanDefinition> definitions = scanner.findCandidateComponents("com.xianglin.game.web.landlords");

        for (BeanDefinition definition : definitions) {
            if (definition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) definition;
                Map<String, Object> annotationAttributes = annotatedBeanDefinition.getMetadata().getAnnotationAttributes(EventHandler.class.getName());
                AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
                String beanName = ((EventType)attributes.getEnum("eventType")).getType();
                beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
            }
        }


        BeanDefinition exceptionResolve = beanDefinitionRegistry.getBeanDefinition("exceptionResolve");

        // 大厅的sever 端口 8111
        RootBeanDefinition configurationBean = new RootBeanDefinition(Configuration.class);
        configurationBean.getPropertyValues().add("exceptionListener", exceptionResolve);

        GenericBeanDefinition serverBean = new GenericBeanDefinition();
        serverBean.setBeanClass(LongPollingLandlordServer.class);
        serverBean.setInitMethodName("start");

        serverBean.getConstructorArgumentValues().addIndexedArgumentValue(0, new LandlordServerConfig());
        serverBean.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference("configuration"));
        serverBean.getConstructorArgumentValues().addIndexedArgumentValue(2, NamespaceType.hall);

        beanDefinitionRegistry.registerBeanDefinition("configuration", configurationBean);
        beanDefinitionRegistry.registerBeanDefinition("landlordHallServer", serverBean);

        // 房间的server 端口 8222
        RootBeanDefinition configurationRoomBean = new RootBeanDefinition(Configuration.class);
        configurationRoomBean.getPropertyValues().add("exceptionListener", exceptionResolve);

        GenericBeanDefinition serverRoomBean = new GenericBeanDefinition();
        serverRoomBean.setBeanClass(LongPollingLandlordServer.class);
        serverRoomBean.setInitMethodName("start");

        LandlordServerConfig config = new LandlordServerConfig();
        config.setServerPort(LandlordServerConfig.SERVER_ROOM_PORT);
        serverRoomBean.getConstructorArgumentValues().addIndexedArgumentValue(0, config);
        serverRoomBean.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference("configurationRoom"));
        serverRoomBean.getConstructorArgumentValues().addIndexedArgumentValue(2, NamespaceType.room);

        beanDefinitionRegistry.registerBeanDefinition("configurationRoom", configurationBean);
        beanDefinitionRegistry.registerBeanDefinition("landlordRoomServer", serverRoomBean);

    }
}
