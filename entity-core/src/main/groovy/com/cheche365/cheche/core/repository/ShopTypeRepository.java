package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.agent.ShopType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopTypeRepository extends CrudRepository<ShopType,Long> {
}
