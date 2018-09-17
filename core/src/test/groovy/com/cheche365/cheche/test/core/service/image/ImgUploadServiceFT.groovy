package com.cheche365.cheche.test.core.service.image

import com.cheche365.cheche.core.service.image.QuotePhoneUploadService
import spock.lang.Specification

/**
 * Created by zhengwei on 4/2/17.
 */
class ImgUploadServiceFT extends Specification{

    def "file path生成测试"() {
        given:

        QuotePhoneUploadService service = new QuotePhoneUploadService(null, null)

        when:

        def result = service.filePath(
            [
                root: '/data/nfs/sr/production',
                mobile: '1311111111',
        ])

        println result

        then:
        result.absPath
        result.relPath
        result.absPath.toString().startsWith('/data/nfs/sr/production')
        result.relPath.toString().startsWith('1311111111')

    }
}
