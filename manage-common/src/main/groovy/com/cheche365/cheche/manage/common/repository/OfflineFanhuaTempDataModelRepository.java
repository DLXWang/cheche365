package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.OfflineFanhuaTempDataModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/02/11.
 */
public interface OfflineFanhuaTempDataModelRepository extends PagingAndSortingRepository<OfflineFanhuaTempDataModel, Long>, JpaSpecificationExecutor<OfflineFanhuaTempDataModel> {
    
}
