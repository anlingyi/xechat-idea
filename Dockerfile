FROM openjdk:8-jre-slim
MAINTAINER "安凌毅 https://xeblog.cn"

ENV JAVA_OPTS=""
ENV TZ="Asia/Shanghai"

EXPOSE 1024

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD xechat-server/target/xechat-server-*.jar /home/xechat/server.jar
ADD xechat-server/target/config.setting /home/xechat/config.setting

WORKDIR /home/xechat/

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS server.jar -path /home/xechat/config.setting"]