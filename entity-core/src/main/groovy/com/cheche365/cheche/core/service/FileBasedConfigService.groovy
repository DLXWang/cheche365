package com.cheche365.cheche.core.service

import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import javax.annotation.PostConstruct
import java.nio.file.FileSystems
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static java.nio.file.Paths.get as getPath
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY



/**
 * 基于文件系统实现的配置服务：<br>
 * 此服务在进程启动后扫描当前工作目录下的conf子目录，<br>
 * 对其中的指定后缀文件名加以解析（.properties等）
 */
@Slf4j
class FileBasedConfigService implements IConfigService {

    private Environment env

    private ConcurrentMap<String, Properties> propertySources


    FileBasedConfigService(Environment env) {
        this.env = env
    }

    @PostConstruct
    void postInit() {
        def confDirs = this.env.getProperty('conf.paths', 'conf')
        log.info '查找并加载配置路径下所有的指定类型的配置文件：{}', confDirs
        def confDirPaths = confDirs.tokenize(',').collect { confDir ->
            getPath confDir
        }
        def confDirFiles = confDirPaths*.toFile()

        def propertySources = confDirFiles*.listFiles({ file ->
            file.name.endsWith '.properties'
        } as FileFilter).flatten().findAll(Closure.IDENTITY).collectEntries { file ->
            [(file.canonicalFile.absolutePath): createProperties(file)]
        } as ConcurrentHashMap
        this.propertySources = propertySources

        log.info '启动文件监控线程'
        def l = log
        new Thread({
            def watchService = FileSystems.default.newWatchService()
            confDirPaths*.register watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE
            while (true) {
                try {
                    def wk = watchService.take()
                    l.info '捕获到文件事件'
                    wk.pollEvents().each { event ->
                        def fileName = event.context().toFile().name
                        if (fileName.endsWith('.properties')) {
                            def eventKind = event.kind()
                            if (ENTRY_CREATE == eventKind || ENTRY_MODIFY == eventKind) {
                                // 针对创建和修改事件：找到对应文件且其必须存在，然后执行事件特定方法
                                confDirFiles.findResult { confDirFile ->
                                    new File(confDirFile, fileName).with { file ->
                                        if (file.exists()) { // 如果文件存在
                                            file.canonicalFile.absolutePath
                                        }
                                    }
                                }?.with { filePath ->
                                    l.info '文件事件类型：{}，文件路径：{}', eventKind, filePath
                                    reloadPropertySource propertySources, filePath
                                }
                            } else if (ENTRY_DELETE == eventKind) {
                                // 针对删除事件：遍历所有文件，只要其不存在，就执行事件特定方法
                                confDirFiles.each { confDirFile ->
                                    new File(confDirFile, fileName).with { file ->
                                        if (!file.exists()) { // 如果文件不存在
                                            def filePath = file.canonicalFile.absolutePath
                                            l.info '文件事件类型：{}，文件路径：{}', eventKind, filePath
                                            removePropertySource propertySources, filePath
                                        }
                                    }
                                }
                            }
                        }
                    }
                    wk.reset()
                } catch(ex) {
                    l.warn '监听目录{}过程中出现非预期错误：', confDirPaths, ex
                }
            }
        }, 'filebased-config-service-filewatcher-thread').with {
            daemon = true
            it
        }.start()
    }

    @Override
    String getProperty(String name) {
        def nameWithPrefixes = name.tokenize '.'
        if (nameWithPrefixes.size() > 1) {
            // 带命名空间
            def namespace = nameWithPrefixes[-2]
            def compositedPropName = nameWithPrefixes[0..<-2, -1].join '.'
            propertySources.findResult { filePath, props ->
                if ((filePath =~ /^.*[\\|\/]$namespace\.\w+$/).matches()) {
                    props.getProperty compositedPropName
                }
            }
        } else {
            // 不带命名空间
            propertySources.values().findResult { props ->
                props.getProperty name
            }
        }
    }

    @Override
    String getProperty(String name, String defaultValue) {
        getProperty(name) ?: defaultValue
    }

    @Override
    Map<String, String> getAllProperties(String namespace) {
        if (namespace) {
            // 带命名空间
            propertySources.findResult { filePath, props ->
                if ((filePath =~ /^.*[\\|\/]$namespace\.\w+$/).matches()) {
                    props
                }
            }
        } else {
            // 不带命名空间，所有不同命名空间的属性合并到一起————为不同命名空间的属性名添加命名空间，然后合并到一起
            // 属性名会形如25000.110000.botpy.code、25000.110000.cpicuk.code等
            propertySources.collectEntries { filePath, props ->
                def propNamespace = (filePath =~ /^.*[\\|\/](\w+)\.\w+$/)[0][1]
                props.collectEntries { propName, propValue ->
                    def propNameSegments = propName.tokenize '.'
                    def propNameWithNamespace = (propNameSegments.size() > 1 ?
                        (propNameSegments[0..<-1] + propNamespace + propNameSegments[-1])
                        : ([propNamespace] + propNameSegments)).join '.'
                    [(propNameWithNamespace): propValue]
                }
            }
        }
    }

    private static void reloadPropertySource(ConcurrentMap propertySources, String path) {
        propertySources.put path, createProperties(path as File)
    }

    private static void removePropertySource(ConcurrentMap propertySources, String path) {
        propertySources.remove path
    }

    private static createProperties(file) {
        new Properties().with { props ->
            file.withReader { reader ->
                props.load reader
            }
            props
        }
    }

}
