package com.cheche365.cheche.core.service.image

import java.nio.file.Path

/**
 * Created by zhengwei on 4/2/17.
 * 统一图片文件对象
 */
class UnifyImageFile {

    String name
    String type //文件类型，由于user_img和purchase_order_image表设计差别较大，没有统一的对象表示文件类型，所以用string表示
    byte[] content
    Path absolutePath  //绝对路径，不带文件名
    Path relativePath  //相对路径，不带文件名
    Object persistObj //持久化对象，不同应用对应的表可能不一致

    //带着文件名的相对路径
    def relativeFilePath(){
        relativePath.resolve(name).toString()
    }

    //带着文件名的绝对路径
    def absoluteFilePath(){
        absolutePath.resolve(name).toString()
    }

    def toURI(){
        absolutePath.resolve(name).toFile().toURI()
    }

}
