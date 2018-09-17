import groovy.json.StringEscapeUtils

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

import static liveplugin.PluginUtil.*



private transform(text) {
    text.readLines().collect { line ->
        def (k, v) = StringEscapeUtils.unescapeJava(line).tokenize()
        "\n'$k' : '${!v ?: ''}'"
    }.join(',')
}

private updateClipboardAndNotify(text) {
    Toolkit.defaultToolkit.systemClipboard.setContents new StringSelection(text), null
    show "结果已更新至系统剪贴板，直接粘贴即可：$text"
}



registerAction("Fiddler请求参数转为Map", "ctrl alt shift M") { event ->
    def text = Toolkit.defaultToolkit.systemClipboard.getContents().getTransferData(DataFlavor.stringFlavor)
    updateClipboardAndNotify transform(text)
}

if (!isIdeStartup) {
    show "“Fiddler请求参数转为Map”插件已加载，拷贝Fiddler中要转换的参数然后在IDE中按下“Ctrl_Alt_Shift_M”"
}