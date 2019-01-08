package com.xianglin.game.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * session信息工具类
 *
 * @author wanglei
 * @date
 */
public class AppSessionContext {

    private static final Logger logger = LoggerFactory.getLogger(AppSessionContext.class);

    /**
     * 保存sessionId信息
     */
    public static final String SESSION_ID = "sessionId";

    public static final String SESSION_PRIFIX = "GLOBAL-";

    public static final String SESSION_ATTR_PRIFIX = "sessionAttr:";

    /**
     * ThreadLocal
     */
    private static ThreadLocal<Map<String, String>> context = new ThreadLocal<Map<String, String>>() {
        @Override
        public Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    /**
     * 初始化session
     *
     * @param map
     */
    public static void putSessionInfo(Map<String, String> map) {
        context.set(map);
    }

    /**
     * 清理session
     */
    public static void removeSession() {
        if (context.get() != null) {
            context.remove();
        }
    }

    /**
     * 取登陆用户partyId
     *
     * @param defaultValue
     * @return
     */
    public static Optional<Long> ofPartyId() {
        String partyId = context.get().get(SESSION_ATTR_PRIFIX + "partyId");
        return Optional.ofNullable(StringUtils.isEmpty(partyId) ? null : Long.valueOf(partyId));
    }

    /**
     * 取客户登录名（一般为手机号）
     *
     * @param defaultValue
     * @return
     */
    public static Optional<String> ofLoginName() {
        return Optional.ofNullable(context.get().get(SESSION_ATTR_PRIFIX + "loginName"));
    }

    /**
     * 取客户端版本号
     *
     * @param defaultValue
     * @return
     */
    public static Optional<String> ofClientVersion() {
        return Optional.ofNullable(context.get().get(SESSION_ATTR_PRIFIX + "clientVersion"));
    }

    /**
     * 取系统类型（ANDROID IOS）
     *
     * @param defaultValue
     * @return
     */
    public static Optional<String> ofSystemType() {
        return Optional.ofNullable(context.get().get(SESSION_ATTR_PRIFIX + "systemType"));
    }


    /**
     * 取sessionId
     *
     * @param defaultValue
     * @return
     */
    public static Optional<String> ofSessionId() {
        return Optional.ofNullable(context.get().get(SESSION_ID + "clientVersion"));
    }

}

