package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrderImageScene;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderImageSceneRepository extends PagingAndSortingRepository<PurchaseOrderImageScene, Long> {

    PurchaseOrderImageScene findFirstByName(String name);
}
