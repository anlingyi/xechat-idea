FROM openjdk:8-jre-slim
MAINTAINER "安凌毅 https://xeblog.cn"

ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV PARAMS="-path /home/xechat/config.setting"
ENV SW_FILE="/home/xechat/db/keywords.txt"
ENV IP2REGION_PATH="/home/xechat/db/ip2region.xdb"
ENV TZ="Asia/Shanghai"

EXPOSE 1024

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD xechat-server/target/xechat-server-*.jar /home/xechat/server.jar
ADD xechat-server/target/classes/config.setting /home/xechat/config/config.setting
COPY xechat-server/target/classes/db/* /home/xechat/db/

WORKDIR /home/xechat/

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS server.jar $PARAMS"]