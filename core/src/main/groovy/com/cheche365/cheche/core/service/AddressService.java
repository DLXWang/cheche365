package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Address;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AddressRepository;
import com.cheche365.cheche.core.repository.AreaRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单地址服务
 * <p>
 * Created by gaochengchun on 2015/5/29.
 */
@Service
@Transactional
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AreaRepository areaRepository;

    public Address getOne(Long addressId) {
        return addressRepository.findOne(addressId);
    }

    public Address getOne(Long addressId, User user) {
        return addressRepository.findByIdAndApplicant(addressId, user);
    }

    //全设置为非默认
    private void setNoDefault(User user) {
        List<Address> addressList = addressRepository.findDefaultAddressByApplicant(user);
        for (Address address : addressList) {
            address.setDefaultAddress(false);
            addressRepository.save(address);
        }
    }

    public Address save(Address address, User user) {
        if (!address.isDefaultAddress()) {
            return save(address);
        } else {
            //修改原来的地址对象的是否默认属性
            setNoDefault(user);
            address.setDefaultAddress(true);
            return save(address);
        }
    }

    public void correctBadAddress(Address address) {
        if (address == null) {
            return;
        }

        address.setMobile(StringUtils.replaceAll(address.getMobile(), " ", ""));

        if (StringUtils.isNumeric(address.getCity())) {// 直辖市传值错误的问题
            boolean isMunicipalityArea = Area.isMunicipalityArea(Long.parseLong(address.getCity()));
            boolean badDistrictParam = StringUtils.isBlank(address.getDistrict()) || StringUtils.equals(address.getDistrict(), "0");
            if (isMunicipalityArea && badDistrictParam) {
                address.setDistrict(address.getCity());
                address.setCity(address.getProvince());
                address.setProvince(null);
            }
        }


    }

    public Address checkExist(final Address address) {
        if (address.getId() != null) {
            return address;
        }
        List<Address> list = addressRepository.findAll(new Specification<Address>() {
            @Override
            public Predicate toPredicate(Root<Address> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (address.getApplicant() != null) {
                    predicates.add(cb.equal(root.get("applicant"), address.getApplicant()));
                }
                if (StringUtils.isNoneBlank(address.getCity())) {
                    predicates.add(cb.equal(root.get("city"), address.getCity()));
                }
                if (StringUtils.isNotBlank(address.getDistrict())) {
                    predicates.add(cb.equal(root.get("district"), address.getDistrict()));
                }
                if (StringUtils.isNotBlank(address.getName())) {
                    predicates.add(cb.equal(root.get("name"), address.getName()));
                }
                if (StringUtils.isNotBlank(address.getProvince())) {
                    predicates.add(cb.equal(root.get("province"), address.getProvince()));
                }
                if (StringUtils.isNotBlank(address.getStreet())) {
                    predicates.add(cb.equal(root.get("street"), address.getStreet()));
                }
                if (StringUtils.isNotBlank(address.getMobile())) {
                    predicates.add(cb.equal(root.get("mobile"), address.getMobile()));
                }
                predicates.add(cb.equal(root.get("disable"), Boolean.FALSE));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        });
        return list.isEmpty() ? address : list.get(0);
    }


    public Address save(Address address) {
        validAddress(address);
        if (address.getId() == null || address.getId().longValue() <= 0) {
            address.setCreateTime(new Date());
            //判断是否已经存在记录
            address = checkExist(address);
        }
        address.setUpdateTime(new Date());
        return addressRepository.save(address);
    }

    public void validAddress(Address address) {
        if (!"0".equals(address.getCity()) && StringUtils.isNotBlank(address.getCity())
            && (NumberUtils.isDigits(address.getCity()) && (Integer.parseInt(address.getCity()) < 110000 || Integer.parseInt(address.getCity()) >= 900000))) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "暂不支持海外城市");
        }
        if (!"0".equals(address.getProvince()) && StringUtils.isNotBlank(address.getProvince())
            && (NumberUtils.isDigits(address.getProvince()) && (Integer.parseInt(address.getProvince()) < 110000 || Integer.parseInt(address.getProvince()) >= 900000))) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "暂不支持海外省份");
        }
        if (!"0".equals(address.getDistrict()) && StringUtils.isNotBlank(address.getDistrict())
            && (NumberUtils.isDigits(address.getDistrict()) && (Integer.parseInt(address.getDistrict()) < 110000 || Integer.parseInt(address.getDistrict()) >= 900000))) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "暂不支持海外市区");
        }
    }


    public Page<Address> getAddressList(User user, Pageable pageable) {
        return addressRepository.searchAddressListPageable(user, pageable);
    }

    public Page<Address> getAddressList(User user, String areaPrefix, Pageable pageable) {
        return addressRepository.searchAddressMatchingAreaId(user, areaPrefix, pageable);
    }

    public String getAddress(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getDeliveryAddress() != null) {
            Address deliveryAddress = purchaseOrder.getDeliveryAddress();
            StringBuffer address = new StringBuffer("");
            // 直辖市/省份
            if (StringUtils.isNotEmpty(deliveryAddress.getProvince())) {
                try {
                    address.append(getAreaName(Long.parseLong(deliveryAddress.getProvince())));
                } catch (NumberFormatException ex) {
                    address.append(deliveryAddress.getProvince());
                }
            }
            // 直辖市/市
            if (StringUtils.isNotEmpty(deliveryAddress.getCity())) {
                try {
                    address.append(getAreaName(Long.parseLong(deliveryAddress.getCity())));
                } catch (NumberFormatException ex) {
                    address.append(deliveryAddress.getCity());
                }
            }
            // 区/县
            if (StringUtils.isNotEmpty(deliveryAddress.getDistrict())) {
                try {
                    address.append(getAreaName(Long.parseLong(deliveryAddress.getDistrict())));
                } catch (NumberFormatException ex) {
                    address.append(deliveryAddress.getDistrict());
                }
            }
            // 街道
            address.append(deliveryAddress.getStreet());
            return address.toString();
        }
        return null;
    }

    private String getAreaName(Long areaId) {
        try {
            return areaRepository.findOne(areaId).getName();
        } catch (Exception ex) {
            return "";
        }
    }

}
