package com.cheche365.cheche.core.util

import static org.apache.commons.beanutils.BeanUtils.cloneBean
import static org.apache.commons.beanutils.BeanUtils.copyProperty

/**
 * Created by houjinxin on 15/9/8.
 */
class ObjectUtil {
    /**
     * 深度拷贝对象的指定属性
     * @param source
     * @param target
     * @param propertyPathList 由源对象和目标对象path组成的List的结合，结构如下：
     * [['sourcePath1','targetPath1'],['sourcePath2','targetPath2'],...]
     * sourcePath与targetPath 分别是source和target的属性
     */
    public static void deeplyCopyProperties(source, target, propertyPathList) {

        propertyPathList.each { sourcePropertyPath, targetPropertyPath, clone = true ->
            GroovyShell shell = new GroovyShell(this.class.classLoader, new Binding([source: source, target : target]))
            // 保证父级path对应的属性不为null，如果是null，就用创建一个对应class的实例赋给父级
            def targetPropertyList = targetPropertyPath.tokenize('.')
            if(targetPropertyList.size() > 1) {
                targetPropertyList[0..-2].with { paths ->
                    paths.withIndex().collect { path, idx ->
                        paths[0..idx].join '.'
                    }
                }.each { parentPath ->
                    def targetParentPath = "target.$parentPath"
                    if (!shell.evaluate(targetParentPath)) {
                        def targetParentPathList = targetParentPath.tokenize '.'
                        def ancestorPath = targetParentPathList[0..-2].join '.'
                        def ancestorInstance = shell.evaluate ancestorPath
                        def value = ancestorInstance.metaPropertyValues.find { propValue ->
                            parentPath.tokenize('.')[-1] == propValue.name
                        }.type.newInstance()
                        shell.setVariable('value', value)
                        shell.evaluate "$targetParentPath = value"
                    }
                }
            }
            // 设置值，在为目标对象的属性赋值前要判断是否为null 如果为null不需要克隆对象
            def targetPropertyValue = shell.evaluate("source.$sourcePropertyPath")?.with { self ->
                clone ? cloneBean(self) : self
            }
            copyProperty target, targetPropertyPath, targetPropertyValue
        }

    }
}
