FROM openjdk:8-jre-slim
MAINTAINER "安凌毅 https://xeblog.cn"

ENV JAVA_OPTS=""
ENV PARAMS="-p 1024"
ENV TZ="Asia/Shanghai"

EXPOSE 1024

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD server.jar /home/xechat/server.jar

WORKDIR /home/xechat/

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS server.jar $PARAMS"]