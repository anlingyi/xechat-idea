# XEChat-Idea

> Version 1.6.7-beta0

> 基于Netty的IDEA即时聊天插件：让你能够在IDEA里实现聊天、下棋、斗地主！(理论上支持JetBrains全系列开发工具🙂)

> 浏览器端：[XEChat-Web](https://github.com/anlingyi/xechat-web)

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
    - [Docker运行](#docker运行)
    - [公开你的鱼塘](#公开你的鱼塘)
    - [学习交流](#学习交流)

## 项目介绍

主要功能：

* 即时聊天
* 游戏对战
* 待探索...

目前已实现：

**游戏类**

* 五子棋（支持2人联机、人机对战，内置"人工制杖"）
* 斗地主（支持2~3人联机、人机对战）
* 不贪吃蛇
* 2048（作者 @[浓睡不消残酒](https://github.com/CodeNoobLH) ，感谢PR😊）
* 数独（作者 @[Speciallei](https://github.com/Specialleiliei) ，感谢PR😊）
* 推箱子（作者 @[Speciallei](https://github.com/Specialleiliei) ，感谢PR😊）
* 中国象棋（支持2人联机、人机对战，作者 @[15738383930](https://github.com/15738383930) ，感谢PR😊）
* 俄罗斯方块（作者 @[SherlockerSun](https://github.com/SherlockerSun) ，感谢PR😊）
* 扫雷（作者 @[SherlockerSun](https://github.com/SherlockerSun) ，感谢PR😊）

**工具类**

* 阅读（作者 @[MINIPuffer](https://github.com/MINIPuffer) ，感谢PR😊）
* 天气查询（作者 @[猎隼丶止戈](https://github.com/nn200433) ，感谢PR😊）
* 浏览器

[了解更多...](https://xeblog.cn/?tag=xechat-idea)

![](https://oss.xeblog.cn/prod/6382599251dc467380c3b1c1eff6bfa5.png)

![](https://oss.xeblog.cn/prod/87397d4da728467e912450f94e41b2ef.jpg)

![](https://oss.xeblog.cn/prod/40ddad661991451889acea177c7f5293.png)

![](https://oss.xeblog.cn/prod/3af16813518b4e4592ece13b0330be9b.png)

![](https://oss.xeblog.cn/prod/154404fccbdd466591df2fbeabdfeb74.png)

![](https://oss.xeblog.cn/prod/8e5bc4004afd48cf9fed4df18a66d070.png)

![](https://oss.xeblog.cn/prod/76dee7f5bb924dd59a5ffaaf333fc45c.png)

![](https://oss.xeblog.cn/prod/ad9a78cf91b94ec0b0a622a8de8f38d9.png)

![](https://oss.xeblog.cn/prod/8adc57b65371469ab6dec2d9e34cb38b.png)

![](https://oss.xeblog.cn/prod/d5706e73906f4377921231898c3ddceb.png)

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
* [WebSocket协议接入文档](https://xeblog.cn/articles/112)

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
* **设置和风天气**：`-weather {和风api key}`
* **设置百度翻译**：`-fyAppId {appId} -fyAppKey {appKey}`
* **设置ip2region文件**：`-ipfile {文件路径}`
* **设置管理员令牌**：`-token {令牌}`
* **指定外部配置文件**：`-path {文件路径}`
* **开放WS协议**：`-enableWS {true|false}`

具体的外部配置文件信息请看：`xechat-server/src/main/resources/config.setting`

参考示例：

```
java -jar target/xechat-server-xxx.jar -p 1024 -swfile /Users/anlingyi/local/test/words.txt -weather {和风天气api key}
```

[敏感词相关配置参考](https://xeblog.cn/articles/99)

[和风天气相关配置参考](https://xeblog.cn/articles/101)

[开放WS协议相关配置参考](https://xeblog.cn/articles/113)

### IDEA插件端

#### 修改IDEA版本

修改 `build.gradle` 配置文件，将 `IDEA` 版本号改为你想使用的版本（仅限开发调试阶段）

```
intellij {
    version '2021.3'
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

## Docker运行

> 感谢 [@猎隼丶止戈](https://github.com/nn200433) 对此部分做的贡献 😊

镜像地址：[https://hub.docker.com/r/anlingyi/xechat-server/tags](https://hub.docker.com/r/anlingyi/xechat-server/tags)

**docker-compose.yml**

```yml
version: '3'
services:
  xechat:
    image: anlingyi/xechat-server:1.6.7-beta
    container_name: xechat-server
    restart: always
    ports:
      - 1024:1024
      - 1025:1025
    volumes:
      - /home/xechat/logs:/var/log/xechat-server
      - /home/xechat/config/config.setting:/home/xechat/config/config.setting
      - /home/xechat/db:/home/xechat/db
```

## 公开你的鱼塘

如果你想公开你的鱼塘，请编辑项目中的 `server_list.json` 文件，添加上你的鱼塘信息，然后提交PR到这里，待我们审核通过后即可。

```json
    {
        "name": "xxx", //鱼塘名
        "ip": "127.0.0.1", //你的服务器IP或域名
        "port": 1024 //端口号
    }
```

## 学习交流

> 感谢 @鹿儿岛 提供的QQ交流群 😊

如果大家对这个项目感兴趣，欢迎加入我们的交流群🎉

* QQ群：754126966