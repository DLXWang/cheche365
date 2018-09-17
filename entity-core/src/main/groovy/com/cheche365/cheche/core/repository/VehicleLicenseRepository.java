package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.VehicleLicense;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleLicenseRepository extends PagingAndSortingRepository<VehicleLicense, Long> {

    VehicleLicense findFirstByLicensePlateNoAndOwner(String licensePlatNo, String owner);

    VehicleLicense findFirstByLicensePlateNoOrderByIdDesc(String licensePlatNo);
}
