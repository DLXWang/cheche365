package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.util.CacheUtil
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by zhengwei on 4/3/15.
 */

@Service
@Transactional
class InsuranceCompanyService {

    private static final String CACHE_KEY = "com:cheche365:cheche:ordercenter:insureComp"
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    private InsuranceCompanyRepository companyRepository

    @Autowired
    private AreaRepository areaRepository

    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    List<InsuranceCompany> findCompaniesByAreaAndChannel(Long areaId, Channel channel) {
        Area area = getArea(areaId)
        quoteFlowConfigRepository.findByAreaAndChannel(area, channel?.parent).findAll {
            (channel.isOrderCenterChannel() ?
                it.insuranceCompany.ocQuote() :
                it.insuranceCompany.quote()
            ) && !it.insuranceCompany.disable()
        }.collect { it.insuranceCompany }.sort { it.rank }
    }

    Area getArea(Long id) {
        Area area = null
        if (null != id) {
            area = areaRepository.findOne(id)
        }

        if (area == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "Area信息不存在")
        }
        return area
    }

    InsuranceCompany findById(Long id) {
        return companyRepository.findOne(id)
    }

    List<InsuranceCompany> listByKeyWord(String keyWord) {
        List<InsuranceCompany> companyList = this.listByCache()
        List<InsuranceCompany> resultList = new ArrayList<>()
        if (!CollectionUtils.isEmpty(companyList)) {
            for(InsuranceCompany company:companyList){
                if (company.getName().contains(keyWord)) {
                    resultList.add(company)
                }
            }
        } else {
            initCache()
            listByKeyWord(keyWord)
        }
        return resultList;
    }

    private List<InsuranceCompany> listByCache(){
        String cacheAString= CacheUtil.getValue(this.stringRedisTemplate,CACHE_KEY)
        List<InsuranceCompany> companyList=new ArrayList<>()
        if(!StringUtils.isEmpty(cacheAString)){
            companyList = CacheUtil.doListJacksonDeserialize(cacheAString,InsuranceCompany.class)
            logger.debug("will get company list cache ,size :->{}",companyList.size())
        }
        return companyList;
    }

    private void initCache(){
        Iterable<InsuranceCompany> insureCompList = companyRepository.findAll()
        logger.debug("will cache company list size: -> {}",insureCompList.size())
        stringRedisTemplate.opsForValue().set(CACHE_KEY, CacheUtil.doJacksonSerialize(insureCompList))
    }
}
