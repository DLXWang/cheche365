package com.cheche365.cheche.botpy.flow


class AutoModelMappings {

    static final _AUTO_MODEL_DEFAULT = { context, modelList, result ->
        def autoModel = null
        if (!modelList) {
            return autoModel
        }
        def quoteComment = result.quotations.first().comment
        def m1 = quoteComment =~ /[\s\S]*(平台返回的车型为)[\s\S]*/
        if (m1.matches()) {
            def comments = quoteComment.split('\\s+')
            def autoModelKey = comments[comments.size() - 1]
            autoModel = modelList.find { it ->
                context.getVehicleOption(context, it).text.contains(autoModelKey)
            }
        }
        autoModel
    }

    static final _AUTO_MODEL_MAPPINGS = [
        default: _AUTO_MODEL_DEFAULT
    ]
}
