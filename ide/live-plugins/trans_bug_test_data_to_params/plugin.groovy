import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

import static liveplugin.PluginUtil.registerAction
import static liveplugin.PluginUtil.show


firstFiveParamsOffsets = [
    0,  //license plate no
    3,  // vin no
    4,  // engine no
    1,  // owner
    2,  // id
]

private transform(text) {

    def (firstFive, others) = text.tokenize().indexed().split { kv->
        kv.key in (0..4)
    }.collect { group ->
        group.collect{
            it.value
        }
    }

    def firstFiveParams = firstFive.indexed().sort { itemWithIndex ->
        firstFiveParamsOffsets[itemWithIndex.key]
    }.values().collect{
        "'$it'"
    }.join(',\n\t').replace('\t', '    ')


    def enrollDate = others[0]

    def brandCode = others[1..-1].join(' ')

    """
[
    0,
    '',
    $firstFiveParams
    null,
    null,
    [
        quoteRecord: [
            auto: [
                autoType  : [
                    code: '$brandCode',
                    supplementInfo: [
                        commercialStartDate: '',
                        compulsoryStartDate: '',
                        autoModel          : ''
                    ]
                ],
                enrollDate: '$enrollDate'
            ]
        ]
    ]
],
    """
}

private updateClipboardAndNotify(text) {
    Toolkit.defaultToolkit.systemClipboard.setContents new StringSelection(text), null
    show "结果已更新至系统剪贴板，直接粘贴即可：$text"
}


registerAction("Bug测试数据转换", "ctrl alt shift B") { event ->
    def text = Toolkit.defaultToolkit.systemClipboard.getContents().getTransferData(DataFlavor.stringFlavor)
    updateClipboardAndNotify transform(text)
}

if (!isIdeStartup) {
    show "“Bug测试数据转换”插件已加载，拷贝Bug描述中要转换的车辆数据然后在IDE中按下“Ctrl_Alt_Shift_B”"
}

