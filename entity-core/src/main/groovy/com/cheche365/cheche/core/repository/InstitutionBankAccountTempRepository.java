package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InstitutionBankAccountTemp;
import com.cheche365.cheche.core.model.InstitutionTemp;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionBankAccountTempRepository extends PagingAndSortingRepository<InstitutionBankAccountTemp, Long> {
    List<InstitutionBankAccountTemp> findByInstitutionTemp(InstitutionTemp institutionTemp);
}
