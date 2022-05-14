# XEChat-Idea

> 基于Netty的IDEA即时聊天插件

## 项目介绍

主要功能：

* 即时聊天
* 游戏对战

[了解更多...](https://xeblog.cn/?tag=xechat-idea)

![](https://oss.xeblog.cn/prod/9502360a6dd04d1fa55f5d906e9404c5.png)

![](https://oss.xeblog.cn/prod/87397d4da728467e912450f94e41b2ef.jpg)

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
* IDEA 2021.2.x

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

### IDEA插件端

#### 修改IDEA版本

修改 `build.gradle` 配置文件，将 `IDEA` 版本号改为你本地正在使用的版本

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
