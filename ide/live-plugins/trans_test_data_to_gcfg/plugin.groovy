import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

import static liveplugin.PluginUtil.*

firstFiveParamsOffsets = [
        0,  //license plate no
        3,  // vin no
        4,  // engine no
        1,  // owner
        2,  // id
]

private transform(text) {

    text.tokenize('\r\n').indexed().collect { lineIdx, line ->
        def (firstFive, others) = line.tokenize(',').with { items ->
            [3, 4].each { offset ->
                if (17 != items[offset].size()) {
                    items[offset] = null
                }
            }
            def oldSize = items.size()
            (items - null).with { newItems ->
                if (oldSize == newItems.size()) {
                    newItems.remove 3
                }
                newItems
            }
        }.indexed().split { kv ->
            kv.key in (0..4)
        }.collect { group ->
            group.collect {
                it.value
            }
        }
        def firstFiveParams = firstFive.indexed().sort { itemWithIndex ->
            firstFiveParamsOffsets[itemWithIndex.key]
        }.values().collect {
            "'$it'"
        }.join(',\n\t').replace('\t', '    ')


        def enrollDate = others[0].with {
            if (!it.contains('-')) {
                "${it[0..3]}-${it[4..5]}-${it[6..-1]}"
            }
        }

        def brandCode = others[1..-1].join(' ')

        """
[
    ${lineIdx * 10},
    '',
    $firstFiveParams,
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
],"""
    }.join '\n'

}




private updateClipboardAndNotify(text) {
    Toolkit.defaultToolkit.systemClipboard.setContents new StringSelection(text), null
    show "结果已更新至系统剪贴板，直接粘贴即可：$text"
}



registerAction("测试数据参数转为List<Map>", "ctrl alt shift B") { event ->
    def text = Toolkit.defaultToolkit.systemClipboard.getContents().getTransferData(DataFlavor.stringFlavor)
    updateClipboardAndNotify transform(text)
}

if (!isIdeStartup) {
    show "“测试数据参数转为List<Map>”插件已加载，拷贝要转换的参数然后在IDE中按下“Ctrl_Alt_Shift_B”"
}
