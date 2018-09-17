package com.cheche365.cheche.core.service



/**
 * 第三方上传服务
 * 注意，不仅仅针对上传图片，而是针对上传任何内容
 */
interface IThirdPartyUploadingService {

    /**
     * 上传
     * @param contents 待上传内容列表
     * @param additionalParameters 附加参数
     * @return
     */
    def upload(List contents, Map additionalParameters)

}
