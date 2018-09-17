package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.UploadFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface UploadFileRepository extends PagingAndSortingRepository<UploadFile, Long>, JpaSpecificationExecutor<UploadFile> {

    Iterable<UploadFile> findAllBySourceTypeAndSourceIdAndStatus(Integer sourceType, Long sourceId, Integer status);
}