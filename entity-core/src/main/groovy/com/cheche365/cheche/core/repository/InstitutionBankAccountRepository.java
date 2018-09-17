package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Institution;
import com.cheche365.cheche.core.model.InstitutionBankAccount;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionBankAccountRepository extends PagingAndSortingRepository<InstitutionBankAccount, Long> {
    List<InstitutionBankAccount> findByInstitution(Institution institution);
}
