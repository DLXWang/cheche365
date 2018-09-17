package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.Relationship
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/12/30.
 */
@Repository
interface RelationshipRepository extends PagingAndSortingRepository<Relationship, Long> {
    Relationship findFirstByName(String name)
}
