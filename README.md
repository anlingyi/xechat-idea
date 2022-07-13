# XEChat-Idea

> Version 1.5.9-beta

> 基于Netty的IDEA即时聊天插件：让你能够在IDEA里实现聊天、下棋、斗地主！(理论上支持JetBrains全系列开发工具🙂)

- [目录](#xechat-idea)
    - [项目介绍](#项目介绍)
        - [项目结构](#项目结构)
        - [项目环境](#项目环境)
        - [项目开发](#项目开发)
    - [运行 & 部署](#运行-部署)
        - [服务端](#服务端)
            - [运行](#运行)
            - [部署](#部署)
        - [IDEA插件端](#idea插件端)
            - [修改IDEA版本](#修改idea版本)
            - [本地运行](#本地运行)
            - [插件部署](#插件部署)
    - [安装体验](#安装体验)
    - [Docker部署](#docker部署)
        - [镜像打包](#镜像打包)
        - [运行](#运行-1)
    - [学习交流](#学习交流)

## 项目介绍

主要功能：

* 即时聊天
* 游戏对战
* 待探索...

目前已实现：

* 五子棋（支持2人联机、人机对战，内置"人工制杖"）
* 斗地主（支持2~3人联机、人机对战）
* 阅读（作者 @[MINIPuffer](https://github.com/MINIPuffer) ，感谢PR😊）
* 天气查询（基于[和风天气](https://dev.qweather.com/)，作者 @[猎隼丶止戈](https://github.com/nn200433) ，感谢PR😊）

[了解更多...](https://xeblog.cn/?tag=xechat-idea)

![](https://oss.xeblog.cn/prod/33a4f79174f2470da66fc7e7f0a36fad.png)

![](https://oss.xeblog.cn/prod/87397d4da728467e912450f94e41b2ef.jpg)

![](https://oss.xeblog.cn/prod/40ddad661991451889acea177c7f5293.png)

### 项目结构

```
.
├── LICENSE
├── README.md
├── xechat-commons //公共模块
│   ├── pom.xml
│   └── src
├── xechat-plugin //IDEA插件端
│   ├── build.gradle
│   ├── gradle
│   ├── gradle.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── settings.gradle
│   └── src
└── xechat-server //服务端
    ├── pom.xml
    └── src
```

### 项目环境

**服务端 & 公共模块**

* JDK8
* Maven 3.6.x

**IDEA 插件端**

* JDK11
* Gradle 6.x

### 项目开发

* [实现一个自定义命令](https://xeblog.cn/articles/79)
* [实现一个自定义消息](https://xeblog.cn/articles/100)
* [实现一个联机对战游戏](https://xeblog.cn/articles/95)

## 运行 & 部署

> 提醒：公共模块需优先打包

```shell
# 进入公共模块根目录
cd xechat-commons
# 打包到本地仓库
mvn install
```

### 服务端

创建或调整日志目录 `src/main/resources/logback.xml`

```xml
<property name="ROOT_LOG_PATH" value="/var/log/xechat-server"/>
```

#### 运行

直接运行主方法 `XEChatServer.java`

#### 部署

```shell
# 进入服务端根目录
cd xechat-server
# 打包
mvn package
# 启动服务端
java -jar target/xechat-server-xxx.jar
```

启动参数：

* **设置端口**：`-p {端口号}`
* **设置敏感词文件**：`-swfile {文件路径}`
* **设置和风天气 api key**：`-weather {和风api key}`

参考示例：

```
java -jar target/xechat-server-xxx.jar -p 1024 -swfile /Users/anlingyi/local/test/words.txt -weather {和风天气api key}
```

[敏感词相关配置参考](https://xeblog.cn/articles/99)

[和风天气相关配置参考](https://xeblog.cn/articles/101)

### IDEA插件端

#### 修改IDEA版本

修改 `build.gradle` 配置文件，将 `IDEA` 版本号改为你想使用的版本（仅限开发调试阶段）

```
intellij {
    version '2021.2'
}
```

#### 本地运行

> Tasks > intellij > runIde

![image.png](https://oss.xeblog.cn/prod/cb07b490036d4755b06c4aa1bc1f8411.png)

#### 插件部署

> 提醒：修改服务端地址

进入到 `cn.xeblog.plugin.client.XEChatClient` ，修改以下变量值

```Java
    private static final String HOST = "localhost"; // 服务端IP
    private static final int PORT = 1024; // 服务端端口
```

**打包**

> Tasks > build > assemble

![image.png](https://oss.xeblog.cn/prod/ca9baea17f3748e59c0cef1f01bd0aa0.png)

打包完成后的文件
`build/distributions/xechat-plugin-xxx.zip`

**安装**

> IDEA > Preferences > Plugins

![image.png](https://oss.xeblog.cn/prod/9e07f0a7b3fb4c7bae0da2d8d1548388.png)

选择打包后的文件安装 `build/distributions/xechat-plugin-xxx.zip`

## 安装体验

添加插件库 `Plugins > 设置按钮 > Manage Plugin Repositories...`

```
http://plugins.xeblog.cn
```

![image.png](https://oss.xeblog.cn/prod/7381109b1fe04a3d9732238f267e53ed.png)

搜索 “xechat” 安装

![image.png](https://oss.xeblog.cn/prod/bb9ee5821ca84cca935f9ccab0040643.png)

如有条件，还请自行部署服务端。

## Docker部署

> 感谢 [@猎隼丶止戈](https://github.com/nn200433) 对此部分做的贡献 😊

### 镜像打包

```dockerfile
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
```

### 运行

> 不想自己打包可下载第三方镜像。[https://hub.docker.com/r/nn200433/xechat](https://hub.docker.com/r/nn200433/xechat)

```bash
# 下载镜像，一定要指定版本
docker pull nn200433/xechat:{Version}

# 运行
docker run -itd -p 1024:1024 --restart=always --name=xechat nn200433/xechat:{Version}
````

docker-compose.yml 方式：

```bash
version: '3'
services:
  xechat:
    image: nn200433/xechat:{version}
    container_name: xechat
    restart: always
    ports:
      - 1024:1025
    environment: 
      - BD_APP_ID=2019xxxxx289668
      - BD_APP_KEY=VIaBxxxxxExVw8S
      - WEATHER_KEY=0b5xxx250xx4d
    volumes: 
      - /home/xechat/logs:/var/log/xechat-server
      - /home/xechat/data/config/config.setting:/home/xechat/config.setting
```

*插件编译请参考上方文档*

## 学习交流

> 感谢 @鹿儿岛 提供的QQ交流群 😊

如果大家对这个项目感兴趣，欢迎加入我们的交流群🎉

* QQ群：754126966