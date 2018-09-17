FROM openjdk:8u171-jre-alpine
WORKDIR /web
COPY ./web/build/libs/*web*.jar /web
RUN mkdir -p /web/conf
COPY ./*/conf/*.properties /web/conf/
CMD java \
    -Dspring.profiles.active=${PROFILES_ACTIVE} \
	-Dwechat.conf.password=bcxjy \
	-Dspring.config.name=application,sms-c,mongodb,ms-sc \
	-Dphantomjs.binary.path=/opt/deps/phantomjs/2.0/bin/phantomjs \
	-Dwechat.accesstoken.daemon=enabled \
	-Dbaoxian.mock_enabled=true \
	-Dpiccuk.username=C110100023 \
	-Dpiccuk.password=password1 \
	-Dccint.api_base_url="https://imgs1.intsig.net" \
	-Dccint.user=cheche365 \
	-Dccint.password=GR5F6ER677GUG \
	-Dcpic2b.remoteServer=http://1.119.6.70:4444/wd/hub \
	-Dcpic2b.username=w_C0210F \
	-Dcpic2b.password=Cpicc3333 \
	-Dorg.eclipse.jetty.annotations.maxWait=180 \
	-Duser.timezone=GMT+08 \
	-Xms1280m \
	-Xmx2048m \
	-XX:+HeapDumpOnOutOfMemoryError \
	-XX:+PrintFlagsFinal \
	-XX:+PrintCommandLineFlags \
	-XX:+PrintGC \
	-Dtemp.captcha.image.save.path=/tmp \
	-Dexternal.program.imagemagick.home=/usr/bin \
	-Dexternal.program.tesseract.home=/usr/bin \
	-Dserver.port=${SERVER_PORT} \
	-Dredis_host=${HOST_IP} \
    -Dredis_port=${REDIS_PORT}  \
    -Dspring.data.mongodb.host=${HOST_IP} \
    -Dspring.data.mongodb.port=${MONGO_PORT} \
    -Dspring.data.mongodb.database=${MONGO_DATABASE} \
    -Ddev.ds_url="jdbc:mysql://${HOST_IP}:${DB_DEST_PORT}/cheche?characterEncoding=utf8" \
	-jar *web*.jar
