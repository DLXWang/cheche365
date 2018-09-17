package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceProductDetail
import com.cheche365.cheche.core.model.abao.InsuranceProductDetailName
import com.cheche365.cheche.core.model.abao.InsuranceProductStatus
import com.cheche365.cheche.core.model.abao.InsuranceProductTag
import com.cheche365.cheche.core.model.abao.InsuranceProductType
import com.cheche365.cheche.core.model.abao.TagType
import com.cheche365.cheche.core.repository.InsuranceProductDetailNameRepository
import com.cheche365.cheche.core.repository.InsuranceProductDetailRepository
import com.cheche365.cheche.core.repository.InsuranceProductRepository
import com.cheche365.cheche.core.repository.InsuranceProductStatusRepository
import com.cheche365.cheche.core.repository.InsuranceProductTagRepository
import com.cheche365.cheche.core.repository.InsuranceProductTypeRepository
import com.cheche365.cheche.core.repository.TagTypeRepository
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.beans.PropertyDescriptor

/**
 * Created by wangjiahuan on 2016/11/22 0022.
 */
@Service
public class InsuranceProductService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String TAG_SPLIT_SYMBOL = "、";

    @Autowired
    private InsuranceProductRepository insuranceProductRepository;

    @Autowired
    private InsuranceProductTypeRepository productTypeRepository;

    @Autowired
    private InsuranceProductDetailNameRepository productDetailNameRepository;

    @Autowired
    private InsuranceProductStatusRepository productStatusRepository;

    @Autowired
    private TagTypeRepository tagTypeRepository;

    @Autowired
    private InsuranceProductDetailRepository productDetailRepository

    @Autowired
    private InsuranceProductTagRepository productTagRepository

    public List<InsuranceProduct> findAllByProductType(InsuranceProductType insuranceProductType) {
        return insuranceProductRepository.findAllByProductTypeAndStatus(insuranceProductType, InsuranceProductStatus.Enum.EFFECTIVE);
    }

    public List<InsuranceProduct> findAllByHotSale() {
        return insuranceProductRepository.findAllByHotSale();
    }

    public List<InsuranceProduct> findAllByTagType(Long id) {
        return insuranceProductRepository.findAllByTagType(id);
    }

    public Map<String, List<Map<String, String>>> getExcelDataFromDB() {
        Map<String, List<Map<String, String>>> excelData = new HashMap()
        productTypeRepository.findAll().each { productType ->
            List<Map<String, String>> excelList = new ArrayList<>()
            def titles = []
            InsuranceProduct.PROPERTIES.each {
                if (!["metaClass", "class", "insuranceProductDetails"].contains(it.getName())) {
                    titles += it.getName()
                }
            }
            productDetailNameRepository.findAllByProductType(productType).each { titles += it.detailName }
            Map titleRowMap = new HashMap<>()
            titles.each { titleRowMap.put(it, it) }
            excelList.add(titleRowMap)
            insuranceProductRepository.findAllByProductType(productType).each { insuranceProduct ->
                Map eachRowDataMap = new HashMap<>()
                InsuranceProduct.PROPERTIES.each {
                    if (!["metaClass", "class", "insuranceProductDetails", "status", "insuranceCompany", "productType", "insuranceProductTags"].contains(it.getName())) {
                        eachRowDataMap.put(it.getName(), it.getReadMethod().invoke(insuranceProduct));
                    }
                }
                eachRowDataMap.put("status", insuranceProduct.status.name);
                eachRowDataMap.put("insuranceCompany", insuranceProduct.insuranceCompany.name);
                eachRowDataMap.put("productType", insuranceProduct.productType.name);
                eachRowDataMap.put("insuranceProductTags", this.productTagsToString(insuranceProduct.insuranceProductTags));
                insuranceProduct.insuranceProductDetails.each {
                    eachRowDataMap.put(it.detailName.detailName, it.value);
                }
                excelList.add(eachRowDataMap);
            }
            excelData.put(productType.name, excelList);
        }
        excelData
    }

//    @Transactional
    Boolean persistExcelData(Map<String, List<Map<String, String>>> excelData) {
        if (!excelData) {
            return true
        }

        excelData.keySet().each { productTypeName ->
            InsuranceProductType productType = productTypeRepository.findFirstByName(productTypeName)
            if (!productType) {
                throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "数据库中未找到对应的产品大类型:" + productTypeName);
            }
            def detailTitles = excelData.get(productTypeName)[0].keySet() // excel第一行标题
            InsuranceProduct.PROPERTIES.each { detailTitles -= it.getName() }
            Map<String, InsuranceProductDetailName> productDetailNameMap = new HashMap<>()
            List<InsuranceProductDetailName> productDetailNames = productDetailNameRepository.findAll()
            detailTitles.each { detailTitle ->
                InsuranceProductDetailName productDetailName = productDetailNameMap.get(detailTitle) ? productDetailNameMap.get(detailTitle) : productDetailNames.find { it.detailName == detailTitle }
                if (!productDetailName) {
                    productDetailName = new InsuranceProductDetailName()
                    productDetailName.detailName = detailTitle
                    productDetailName.description = detailTitle
                    productDetailNameRepository.save(productDetailName)
                }
                if (!productDetailNameMap.get(detailTitle)) {
                    productDetailNameMap.put(detailTitle, productDetailName)
                }
            }

            List<InsuranceProduct> insuranceProducts = []
            List<InsuranceProductDetail> insuranceProductDetails = []
            List<InsuranceProductTag> insuranceProductTags = []
            List<InsuranceCompany> insuranceCompanies = InsuranceCompany.nonAutoCompanies()
            List<InsuranceProductStatus> productStatusList = productStatusRepository.findAll()
            List<TagType> tagTypeList = tagTypeRepository.findAll()
            Map<String, TagType> tagTypeMap = new HashMap<>()
            excelData.get(productTypeName).each { eachRow ->
                InsuranceProduct insuranceProduct = new InsuranceProduct()
                setProperties(eachRow, insuranceProduct)
                insuranceProduct.status = productStatusList.find { it.name == eachRow.get("status") }
                if (!insuranceProduct.status) {
                    throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "数据库中未找到对应的产品状态:" + eachRow.get("status"));
                }
                insuranceProduct.insuranceCompany = insuranceCompanies.find { it.name == eachRow.get("insuranceCompany") }
                if (!insuranceProduct.insuranceCompany) {
                    throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "数据库中未找到对应的保险公司:" + eachRow.get("insuranceCompany"));
                }
                insuranceProduct.productType = productType
                if (insuranceProduct.id && !(insuranceProductRepository.findOne(insuranceProduct.id))) {
                    insuranceProduct.id = null
                }
                insuranceProduct = insuranceProductRepository.save(insuranceProduct)
                insuranceProduct.insuranceProductTags = null
                insuranceProduct.insuranceProductDetails = null
                processTagsAndDetails(eachRow, insuranceProduct, tagTypeList, tagTypeMap, productDetailNameMap, insuranceProductDetails, insuranceProductTags)
                insuranceProducts.add(insuranceProduct)
            }
            deleteTagsAndDetails(insuranceProducts.findAll { it.id != null })
            productDetailRepository.save(insuranceProductDetails)
            productTagRepository.save(insuranceProductTags)
        }
        return true
    }

    private void deleteTagsAndDetails(ArrayList<InsuranceProduct> insuranceProducts) {
        if (insuranceProducts) {
            productDetailRepository.deleteByInsuranceProduct(insuranceProducts)
            productTagRepository.deleteByInsuranceProduct(insuranceProducts)
        }
    }

    private PropertyDescriptor[] setProperties(Map<String, String> eachRow, InsuranceProduct insuranceProduct) {
        InsuranceProduct.PROPERTIES.each {
            if (!StringUtils.isBlank(eachRow.get(it.getName())) && !["metaClass", "class", "insuranceProductDetails", "status", "insuranceCompany", "productType", "insuranceProductTags"].contains(it.getName())) {
                if (it.getPropertyType() == Boolean.class) {
                    it.getWriteMethod().invoke(insuranceProduct, Boolean.valueOf(eachRow.get(it.getName())));
                } else if (it.getPropertyType() == Long.class) {
                    it.getWriteMethod().invoke(insuranceProduct, Long.valueOf(eachRow.get(it.getName())));
                } else {
                    it.getWriteMethod().invoke(insuranceProduct, eachRow.get(it.getName()));
                }
            }
        }
    }

    private String[] processTagsAndDetails(Map<String, String> eachRow, InsuranceProduct insuranceProduct, List<TagType> tagTypeList,
                                           Map<String, TagType> tagTypeMap, Map<String, InsuranceProductDetailName> productDetailNameMap,
                                           List<InsuranceProductDetail> insuranceProductDetails, List<InsuranceProductTag> insuranceProductTags) {
        eachRow.get("insuranceProductTags").split(TAG_SPLIT_SYMBOL).each { tagTypeName ->
            TagType tagType = tagTypeMap.get(tagTypeName) ? tagTypeMap.get(tagTypeName) : tagTypeList.find { it.name == tagTypeName }
            if (!tagType) {
                tagType = new TagType()
                tagType.name = tagTypeName
                tagType.description = tagTypeName
                tagTypeRepository.save(tagType)
            }
            if (!tagTypeMap.get(tagTypeName)) {
                tagTypeMap.put(tagTypeName, tagType)
            }
        }
        processProductDetailsAndProductTags(insuranceProduct, eachRow, productDetailNameMap, insuranceProductDetails, tagTypeMap, insuranceProductTags)
    }

    private processProductDetailsAndProductTags(InsuranceProduct insuranceProduct, Map<String, String> eachRow,
                                                Map<String, InsuranceProductDetailName> productDetailNameMap, List<InsuranceProductDetail> insuranceProductDetails,
                                                Map<String, TagType> tagTypeMap, List<InsuranceProductTag> insuranceProductTags) {
        productDetailNameMap.values().each { detailName ->
            if (eachRow.get(detailName.detailName)) {
                InsuranceProductDetail productDetail = new InsuranceProductDetail()
                productDetail.insuranceProduct = insuranceProduct
                productDetail.detailName = detailName
                productDetail.value = eachRow.get(productDetail.detailName.detailName)
                productDetail.display = InsuranceProductDetail.Enum.allowDisplayDetail(productDetail)
                insuranceProductDetails.add(productDetail)
            }
        }
        tagTypeMap.values().each { tagType ->
            if (eachRow.get("insuranceProductTags").contains(tagType.name)) {
                InsuranceProductTag productTag = new InsuranceProductTag()
                productTag.insuranceProduct = insuranceProduct
                productTag.tagType = tagType
                insuranceProductTags.add(productTag)
            }
        }
    }

    private String productTagsToString(List<InsuranceProductTag> insuranceProductTags) {
        if (!insuranceProductTags) {
            return null
        }
        def productTags = ""
        insuranceProductTags.each { productTags = productTags + it.tagType.name + TAG_SPLIT_SYMBOL }
        productTags.substring(0, productTags.length())
    }
}
