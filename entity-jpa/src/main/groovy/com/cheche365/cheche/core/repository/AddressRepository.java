package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Address;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    @Query(value = "select u.* from address u where  u.id=?1 and u.applicant=?2 and u.disable=0 ", nativeQuery = true)
    Address findByIdAndApplicant(Long Id, User applicant);

    @Query(value = "select u.* from address u where u.applicant=?1 and u.disable=0 order by u.default_address desc,u.update_time desc", nativeQuery = true)
    List<Address> findByApplicant(User applicant);

    @Query(value = "select u.* from address u where u.applicant=?1 and u.disable=0 and u.default_address=1", nativeQuery = true)
    List<Address> findDefaultAddressByApplicant(User applicant);

    @Query(value = "from Address u where u.applicant=?1 and u.disable=0 and u.city is not null and u.district is not null order by u.defaultAddress desc,u.updateTime desc ")
    Page<Address> searchAddressListPageable(User user, Pageable pageable);

    @Query(value = "from Address u where u.applicant=?1 and u.disable=0 and u.city is not null and u.district is not null and (u.city like ?2 or u.district like ?2 )order by u.defaultAddress desc,u.updateTime desc ")
    Page<Address> searchAddressMatchingAreaId(User user, String areaPrefix, Pageable pageable);


    Address findByName(String name);

}
