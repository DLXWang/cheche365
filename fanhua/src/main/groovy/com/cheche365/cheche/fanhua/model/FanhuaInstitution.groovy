package com.cheche365.cheche.fanhua.model

import com.cheche365.cheche.core.model.Area

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Created by zhangtc on 2017/12/15.
 */
@Entity
class FanhuaInstitution {

    @Id
    Long id
    String comname
    String citycode
    String cityname
    @ManyToOne
    Area area
}
