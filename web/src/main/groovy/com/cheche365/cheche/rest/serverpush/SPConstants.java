package com.cheche365.cheche.rest.serverpush;

import java.nio.charset.StandardCharsets;

/**
 * Created by zhengwei on 12/30/16.
 */
public class SPConstants {

    public static final String UTF_8 = StandardCharsets.UTF_8.name();

    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_MESSAGE_ID = "id";

    public static final String TOPIC_PATH = "/sp/subscribe";
    public static final String CROSS_JVM_BROADCASTER_ID = TOPIC_PATH;

}
