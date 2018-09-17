package com.cheche365.cheche.core.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Author:   shanxf
 * Date:     2018/7/30 11:47
 */
@Service
class SystemService {


    @Autowired
    private IResourceService resourceService

    private String androidPath

    @PostConstruct
    private void init() {
        androidPath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getAndroid())
    }

    String getMaxVersionApp(String prefix, String androidPkgPath) {
        def list = []
        new File(androidPath).eachFileMatch(~/${prefix}_v.*\.apk/) { File file ->
            def nameStr = file.name.split('\\.')
            list << ['1': nameStr[0].replace("${prefix}_v", ''), '2': nameStr[1], '3': nameStr[2]]
        }

        def maxVersion = findMax(list, 1)
        return androidPkgPath.replace('prefix', prefix).replace('version', maxVersion['1'] + '.' + maxVersion['2'] + '.' + maxVersion['3'])
    }

    def findMax(List list, type) {
        def maxValue = (list*."$type").max()
        if (maxValue) {
            findMax(list.findAll { it."$type" == maxValue }, ++type)
        } else {
            return list.get(0)
        }
    }
}
