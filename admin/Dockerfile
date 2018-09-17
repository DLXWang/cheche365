FROM openjdk:8u171-jre-alpine
WORKDIR /admin
COPY ./admin/build/libs/*admin*.jar /admin
RUN mkdir -p /admin/static
RUN mkdir -p /admin/conf
COPY ./admin/src/main/webapp /admin/static
COPY ./*/conf/*.properties /admin/conf/
CMD java \
    -Dspring.profiles.active=${PROFILES_ACTIVE} \
    -Ddiscovery.hostname=${DISCOVERY_HOSTNAME} \
    -Dredis_host=${HOST_IP} \
    -Dredis_port=${REDIS_PORT}  \
    -Dspring.data.mongodb.host=${HOST_IP} \
    -Dspring.data.mongodb.port=${MONGO_PORT} \
    -Dspring.data.mongodb.database=${MONGO_DATABASE} \
    -Ddev.ds_url="jdbc:mysql://${HOST_IP}:${DB_DEST_PORT}/cheche?characterEncoding=utf8" \
    -Dserver_port=${SERVER_PORT} \
    -Dspring.config.name=application,sms-c,mongodb \
    -Dwechat.conf.password=bcxjy \
    -Duser.timezone=GMT+08 \
    -Dorg.eclipse.jetty.annotations.maxWait=120 \
    -Xms512m \
    -Xmx512m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:+PrintFlagsFinal \
    -XX:+PrintCommandLineFlags \
    -XX:+PrintGC \
    -jar *admin*.jar




