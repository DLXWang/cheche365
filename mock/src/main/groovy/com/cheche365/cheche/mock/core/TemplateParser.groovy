package com.cheche365.cheche.mock.core

abstract class TemplateParser {

    String prefix
    String suffix

    TemplateParser(String prefix, String suffix){
        this.prefix = prefix
        this.suffix = suffix
    }


    String getTemplateName(String fileName) {
        if(fileName && fileName.startsWith(prefix) && fileName.endsWith(suffix) && fileName.length()>(prefix.length()+suffix.length())){
            fileName.substring(prefix.length()+1,fileName.length()-suffix.length()-1)
        }
    }

    boolean support(String fileName){
        fileName.endsWith(suffix)
    }

    abstract parse(File file)
}
