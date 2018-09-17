package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.VehicleContact;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by taguangyao on 2015/12/29.
 */
@Repository
public interface VehicleContactRepository extends PagingAndSortingRepository<VehicleContact, Long> {

    List<VehicleContact> findByMobile(String mobile);
}
