package com.cheche365.cheche.mock.core

import java.nio.file.Paths

class DataLoader {

    List<TemplateParser> nameParser
    def templateMap = [:]

    def loadJsonData(String pathName, List<TemplateParser> nameParser){
        this.nameParser = nameParser
        File file = Paths.get(pathName).toFile()
        readDirectory(new File(pathName))
        templateMap
    }

    def readDirectory(File file) {
        if(file.isDirectory()){
            file.eachFile {
                if(it.isFile()){
                    def parser = findParser(it.getName())
                    if(parser){
                        templateMap.put(parser.getTemplateName(it.name), parser.parse(it))
                    }
                }else if(it.isDirectory()){
                    readDirectory(it)
                }
            }
        }
    }

    TemplateParser findParser(String fileName){
        nameParser.find {it.support(fileName)}
    }
}
