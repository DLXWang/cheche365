# 本地开发环境构建脚本



## 前置条件

**注意，所有软件都不要安装到有空格或中文的目录中。**

*   Vagrant

    从[Vagrant](http://www.vagrantup.com/downloads.html)下载 **1.7.2** 或更高版本。

    将 **%VAGRANT_HOME%/bin** 加入到PATH中（Windows安装文件会自动添加）。

*   VirtualBox

    从[VirtualBox 4.3](https://www.virtualbox.org/wiki/Download_Old_Builds_4_3)下载4.3.28或版本。

    VirtualBox 5.0未经测试，有兴趣的同学可以试试。

    将 **%VIRTUALBOX_HOME%** 加入到PATH中。

*   MSYS2（仅限于Windows平台）

    从[MSYS2](http://msys2.github.io/)下载x64最新版本。

    按照官网首页的8步说明更新pacman，然后在执行下列命令安装rsync：

        pacman -S rsync



## 快速上手

*   ![启动MSYS2 Shell](../../../docs/images/msys2-shell-icon.png)

*   将所有含有tpl后缀的文件复制一份并去掉tpl后缀

*   打开终端，转到此目录下，执行如下命令

        vagrant up

*   待启动结束之后，可以用host上的redis-cli、mysql和浏览器等测试client上运行的server：

        redis-cli
        mysql -uroot -proot
        wget http://localhost/autotype/brand/0BEEDF7D1B5F27482D93D549F92CB77B_1.png


