package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class IdentityType implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @Column(columnDefinition = "VARCHAR(45)")
    String name
    @Column(columnDefinition = "VARCHAR(2000)")
    String description
    @ManyToOne
    @JoinColumn(name = "parent", foreignKey = @ForeignKey(name = "FK_IDENTITY_TYPE_REF_PARENT", foreignKeyDefinition = "FOREIGN KEY (`parent`) REFERENCES `parent_identity_type` (`id`)"))
    ParentIdentityType parent

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.getClass()) return false
        return this.id == o.id
    }

    @Override
    String toString() {
        return "IdentityType{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}'
    }

    @Override
    int hashCode() {
        id.hashCode()
    }

    static class Enum{
        //身份证
        public static IdentityType IDENTITYCARD
        //护照
        public static IdentityType PASSPORT
        //军官证
        public static IdentityType OFFICERARD
        //回乡证
        public static IdentityType  RETURN_PERMIT
        //临时身份证
        public static IdentityType TEMPORARY_ID
        //户口簿
        public static IdentityType RESIDENCE_BOOKLET
        //警官证
        public static IdentityType OFFICERS_CARD
        //台胞证
        public static IdentityType MTP
        //营业执照
        public static IdentityType BUSINESS_LICENSE
        //其它证件
        public static IdentityType OTHER_IDENTIFICATION
        //组织机构代码
        public static IdentityType ORGANIZATION_CODE
        //工商注册号码
        public static IdentityType BUSINESS_REGISTRATION_NUMBER
        //统一社会信用代码
        public static IdentityType UNIFIED_SOCIAL_CREDIT_CODE
        //港澳通行证
        public static IdentityType HONGKONG_MACAO_LAISSEZ_PASSER
        //台湾通行证
        public static IdentityType TAIWAN_LAISSEZ_PASSER

        public static List<IdentityType> QUOTE_TYPE
        public static List<IdentityType> IDENTITY_TYPES_TOA
        public static List<IdentityType> ALL

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
            if (applicationContext != null) {
                IdentityTypeRepository identityTypeRepository = applicationContext.getBean(IdentityTypeRepository.class)
                IDENTITYCARD = identityTypeRepository.findFirstByName("身份证")
                PASSPORT = identityTypeRepository.findFirstByName("护照")
                OFFICERARD = identityTypeRepository.findFirstByName("军官证")
                RETURN_PERMIT = identityTypeRepository.findFirstByName("回乡证")
                TEMPORARY_ID = identityTypeRepository.findFirstByName("临时身份证")
                RESIDENCE_BOOKLET = identityTypeRepository.findFirstByName("户口簿")
                OFFICERS_CARD = identityTypeRepository.findFirstByName("警官证")
                MTP = identityTypeRepository.findFirstByName("台胞证")
                BUSINESS_LICENSE = identityTypeRepository.findFirstByName("营业执照")
                OTHER_IDENTIFICATION = identityTypeRepository.findFirstByName("其它证件")
                ORGANIZATION_CODE = identityTypeRepository.findFirstByName("组织机构代码")
                BUSINESS_REGISTRATION_NUMBER = identityTypeRepository.findFirstByName("工商注册号码")
                UNIFIED_SOCIAL_CREDIT_CODE = identityTypeRepository.findFirstByName("统一社会信用代码")
                HONGKONG_MACAO_LAISSEZ_PASSER = identityTypeRepository.findFirstByName("港澳通行证")
                TAIWAN_LAISSEZ_PASSER = identityTypeRepository.findFirstByName("台湾通行证")
                QUOTE_TYPE = [IDENTITYCARD, ORGANIZATION_CODE, BUSINESS_REGISTRATION_NUMBER, UNIFIED_SOCIAL_CREDIT_CODE, PASSPORT, OFFICERARD, HONGKONG_MACAO_LAISSEZ_PASSER, TAIWAN_LAISSEZ_PASSER]
                IDENTITY_TYPES_TOA = [IDENTITYCARD, PASSPORT, OFFICERARD, ORGANIZATION_CODE, BUSINESS_LICENSE, UNIFIED_SOCIAL_CREDIT_CODE]
                ALL = identityTypeRepository.findAll()

            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Identity Type初始化失败")
            }
        }
    }

    static IdentityType toIdentityType(Long id) {
        Enum.ALL.find { it.id == id }
    }
}
