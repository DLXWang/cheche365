FROM openjdk:8u171-jre-alpine
WORKDIR /scheduletask
COPY ./scheduletask/build/libs/*scheduletask*.jar /scheduletask
CMD java \
    -Dspring.profiles.active=${SPRING_PROFILE} \
    -Dserver.port=${ST_PORT} \
    -Dspring.data.mongodb.host=${MONGO_HOST} \
    -Dspring.data.mongodb.port=${MONGO_PORT} \
    -Dspring.data.mongodb.database=${MONGO_DATABASE} \
    -Dmail.ssl=true \
    -Dwechat.conf.password=bcxjy \
    -Dspring.config.name=application,sms-c,mongodb \
    -Duser.timezone=GMT+08 \
    -Xms512m \
    -Xmx1024m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:+PrintFlagsFinal \
    -XX:+PrintCommandLineFlags \
    -XX:+PrintGC \
    -jar *scheduletask*.jar
