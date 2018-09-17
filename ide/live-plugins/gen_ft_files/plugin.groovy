import java.awt.*
import java.awt.datatransfer.DataFlavor

import static liveplugin.PluginUtil.registerAction
import static liveplugin.PluginUtil.show


tmpDir = System.properties.'java.io.tmpdir' as File


private void transform(text) {
    def testCaseConfs = evaluate text

    testCaseConfs.collect { companyCode, areaId, areaEngName, areaChsName ->
        def capitalizedCompanyCode = companyCode.capitalize()

        [
            "$capitalizedCompanyCode${areaId}${areaEngName}FT",
            """package com.cheche365.cheche.$companyCode

/**
 * $areaChsName
 */
class $capitalizedCompanyCode${areaId}${areaEngName}FT extends A${capitalizedCompanyCode}FT {

    @Override
    protected final getAreaProperties() {
        [id: ${areaId}L, name: '$areaChsName']
    }

}

"""
        ]
    }.each { clazzName, clazzContent ->
        ([tmpDir, "${clazzName}.groovy"] as File).withWriter { w ->
            w << clazzContent
        }
    }
}

private writeFileAndNotify(text) {
    transform text
    show "文件已写至${tmpDir.path}"
}


registerAction("生成FT文件", "ctrl alt shift F") { event ->
    def text = Toolkit.defaultToolkit.systemClipboard.getContents().getTransferData(DataFlavor.stringFlavor)
    writeFileAndNotify text
}

if (!isIdeStartup) {
    show """“生成FT文件”插件已加载，拷贝下列格式表达式：
[
\t['picc', 110000L, 'beijing', '北京'],
]
然后在IDE中按下“Ctrl_Alt_Shift_F”"""
}

