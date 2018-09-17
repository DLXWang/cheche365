package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Feedback;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by liuzh on 2015/5/22.
 */
@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<Feedback,Long> {

}
