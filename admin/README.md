## 使用Dockerfile生成镜像前，需要执行以下操作
* 1.cmd下更改目录：cmd命令行将初始目录更改为apps项目下
* 2.使用gradle来对当前项目进行打包：cmd命令行执行 gradlew.bat admin:build -x test -x check
* 3.待打包完成后再执行Dockerfile文件进行image的创建
