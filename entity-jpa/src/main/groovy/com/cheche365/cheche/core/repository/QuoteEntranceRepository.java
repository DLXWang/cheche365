package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QuoteEntrance;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by chennan on 2015/12/21.
 */
@Repository
public interface QuoteEntranceRepository extends CrudRepository<QuoteEntrance,Long>,JpaSpecificationExecutor<QuoteEntrance> {
}
