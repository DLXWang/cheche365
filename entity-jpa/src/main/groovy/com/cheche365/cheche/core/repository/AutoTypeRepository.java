package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AutoType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AutoTypeRepository extends PagingAndSortingRepository<AutoType, Long> {

    AutoType findByModel(String model);

    AutoType findFirstByModelOrderByIdDesc(String model);

    AutoType findFirstByBrandAndFamilyAndGroupAndModelOrderByIdDesc(String brand, String family, String group, String model);

    @Query(value = "select distinct u.brand from auto_type u where u.brand like %?1% limit 20", nativeQuery = true)
    List<String> listBrandRange(String brand);

    @Query(value = "select distinct u.family from auto_type u where u.family like %?1% limit 20", nativeQuery = true)
    List<String> listFamilyRange(String family);

    @Query(value = "select distinct u.auto_group from auto_type u where u.auto_group like %?1% limit 20", nativeQuery = true)
    List<String> listGroupRange(String group);

    @Query(value = "select distinct u.model from auto_type u where u.model like %?1% limit 20", nativeQuery = true)
    List<String> listModelRange(String model);

    AutoType findFirstByCode(String code);

    @Query(value = "select at.* from auto a,auto_type at where a.auto_type = at.id and a.license_plate_no = ?1 GROUP BY a.create_time DESC LIMIT 1", nativeQuery = true)
    AutoType findByLicensePlateNo(String licensePlateNo);
}
