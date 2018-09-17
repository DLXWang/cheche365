# 车车365（cheche）



## 子项目

*   core（核心服务：domain、repository以及service）

*   picc（PICC）

*   sinosig（SINOSIG）

*   wechat（微信）

*   admin（后台管理，5月30日之后已废弃）

*   test（测试组件）

*   common（公共组件）


## 前置条件

*   IDE

    强烈推荐使用 **JetBrains** 的 **[IntelliJ IDEA](http://www.jetbrains.com/idea)** ，否则，建议使用 **Spring** 的 **[GGTS](http://spring.io/tools/ggts/all)** 。


## 快速上手

*   从IDE中打开项目

    **IntelliJ IDEA**：选择 **Open** ，然后在 **File Chooser** 中选择`build.gradle`文件即可。

    ![选择Open](docs/images/idea-open-project.png)

    ![选择build.gradle](docs/images/idea-choose-gradle-file.png)


## Gradle任务

为了简化各个任务 **JVM选项** 的描述，我们将使用如下格式：

    <option name>       [option1|option2|...]       <default option>


*   `mysqlStart` 与 `mysqlStop`

    **描述**：启/停 **MySQL** 服务器。

    **JVM选项**：

        db.mysql.server.home        [/path/to/your/mysql]           ${project_home}/mysql
        db.mysql.user               [username]                      cheche
        db.mysql.password           [password]                      cheche


*   `appStart/appStartDebug`

    **描述**：在IDE中启动Web Server。

    **JVM选项**：

        spring.profiles.active              [dev|itg|pre_release|production]    dev
        gretty.inplace_mode                 [soft|hard]                         soft
        shell.ssh.port                      [52739|41628|30517|29406]           52739
        wechat.accesstoken.daemon           [enabled|disabled]                  disabled
        gretty.http.port                    [8246|9135|7135|8024]               8246
        gretty.service.port                 [9900|9800|9700|9600]               9900
        gretty.status.port                  [9901|9801|9701|9701]               9901
        temp.captcha.image.save.path        [/path/to/save/image]               /tmp
        external.program.imagemagick.home   [/path/to/imagemagick]              /usr/bin
        external.program.tesseract.home     [/path/to/tessearact]               /usr/bin
        phantomjs.binary.path               [/path/to/phantomjs]                /opt/deps/phantomjs/2.0/bin
        static_resource.unionrsa.url        [/path/to/unionrsa/html]            file:///opt/cheche365/prod/cheche/app/static_resources/web/unionrsa.html
        gretty.servlet.container            [jetty9/tomcat8]                    jetty9


*   `buildProduct`

    **描述**：构建产品，仅包含 **web** 一个web应用。

    **JVM选项**：

        spring.profiles.active              [dev|itg|production]            dev
        shell.ssh.port                      [52739|41628]                   52739
        wechat.accesstoken.daemon           [enabled|disabled]              disabled
        gretty.http.port                    [8246|9135]                     8246
        gretty.service.port                 [9900|9800]                     9900
        gretty.status.port                  [9901|9801]                     9901
        temp.captcha.image.save.path        [/path/to/save/image]           /tmp
        external.program.imagemagick.home   [/path/to/imagemagick]          /usr/bin
        external.program.tesseract.home     [/path/to/tessearact]           /usr/bin
        gretty.servlet.container            [jetty9/tomcat8]                jetty9


##  杂类

*    产品环境的启动参数：

        "-Dwechat.accesstoken.daemon=enable",
        "-Dwechat.conf.password=bcxjy",
        "-Xms1280m",
        "-Xmx1280m",
        "-Xmn512m",
        "-XX:+UseG1GC",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:+PrintFlagsFinal",
        "-XX:+PrintCommandLineFlags",
        "-XX:+PrintGC"

*   TODO
