package com.cheche365.cheche.core.repository;


import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PurchaseOrderAuditingRepository  extends PagingAndSortingRepository<PurchaseOrderAuditing, Long>{

}
