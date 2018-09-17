package com.cheche365.cheche.mock.core

import com.cheche365.cheche.common.util.XmlUtils


/**
 * Created by zhengwei on 25/01/2018.
 */
class XmlTemplateParser extends TemplateParser {

    XmlTemplateParser() {
        super('model_template', 'xml')
    }

    @Override
    def parse(File file) {
        XmlUtils.xmlToMap(file.text)
    }
}
