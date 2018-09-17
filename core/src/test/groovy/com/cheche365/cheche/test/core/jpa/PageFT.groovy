package com.cheche365.cheche.test.core.jpa

import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.repository.GiftRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

/**
 * Created by zhengwei on 5/31/17.
 */
class PageFT extends Specification {

    def "jpa分页测试"(){
        given:

        def giftsInDB = [
            new Gift(id : 1l),
            new Gift(id : 2l),
            new Gift(id : 3l)
        ]

        def giftRepo = Stub(GiftRepository){
            findAll(_) >> new PageImpl(
                giftsInDB,
                new PageRequest(0 ,2),
                giftsInDB.size()
            )
        }

        when:
        PageImpl<Gift> originalGifts = giftRepo.findAll(new PageRequest(0, 2))
        def extendedGifts = []
        extendedGifts.addAll(originalGifts.content)
        extendedGifts.addAll([
            new Gift(id : 4l),
            new Gift(id : 5l),
            new Gift(id : 6l),
            new Gift(id : 7l),
        ])

        def result = new PageImpl<Gift>(extendedGifts, new PageRequest(0,2), extendedGifts.size())
        println result

        then:
        originalGifts.totalPages == 2
        result.totalPages == 4
        result.content.size() == 7
    }
}
