package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MessageVariable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageVariableRepository extends PagingAndSortingRepository<MessageVariable, Long> {
    MessageVariable findByCode(String code);

    Integer countByCode(String code);

}
