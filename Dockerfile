FROM openjdk:8-jre-slim
MAINTAINER "安凌毅 https://xeblog.cn"

ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV PARAMS="-path /home/xechat/config/config.setting"
ENV TZ="Asia/Shanghai"

EXPOSE 1024 1025

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD xechat-server/target/xechat-server-*.jar /home/xechat/server.jar
ADD xechat-server/src/main/resources/config.setting /home/xechat/config/config.setting
COPY xechat-server/src/main/resources/db/ip2region.xdb /home/xechat/db/
COPY xechat-server/src/main/resources/db/keywords.txt /home/xechat/db/

WORKDIR /home/xechat/

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS server.jar $PARAMS"]