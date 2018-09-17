package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.abao.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangjiahuan on 2016/12/23 0023.
 */
@Repository
public interface IndustryRepository  extends JpaRepository<Industry, Long> {

    @Query(value = "select * from industry a RIGHT JOIN industry_type b on a.industry_type=b.id where  b.id=?1", nativeQuery = true)
    List<Industry> findByIndustryType(Long id);
}
