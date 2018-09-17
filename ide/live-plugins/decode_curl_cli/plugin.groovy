import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection


import static liveplugin.PluginUtil.registerAction
import static liveplugin.PluginUtil.show
import static java.net.URLDecoder.decode

private transform(text) {
    decode text, 'UTF-8'
}

private updateClipboardAndNotify(text) {
    Toolkit.defaultToolkit.systemClipboard.setContents new StringSelection(text), null
    show "结果已更新至系统剪贴板，直接粘贴即可：$text"
}


registerAction("解码cURL命令行", "ctrl alt shift U") { event ->
    def text = Toolkit.defaultToolkit.systemClipboard.getContents().getTransferData(DataFlavor.stringFlavor)
    updateClipboardAndNotify transform(text)
}

if (!isIdeStartup) {
    show "“解码cURL命令行”插件已加载，在Chrome中生成cURL命令行然后在IDE中按下“Ctrl_Alt_Shift_U”"
}

