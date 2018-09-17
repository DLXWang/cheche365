package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.abao.IndustryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangjiahuan on 2016/12/23 0023.
 */
@Repository
public interface IndustryTypeRepository extends JpaRepository<IndustryType, Long> {

    @Query(value = "select * from industry_type where parent is null", nativeQuery = true)
    List<IndustryType> findByIndustry();

    @Query(value = "select * from industry_type where parent=?1", nativeQuery = true)
    List<IndustryType> findByParent(Long parent);
}
