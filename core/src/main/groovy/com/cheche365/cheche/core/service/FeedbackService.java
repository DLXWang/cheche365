package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Feedback;
import com.cheche365.cheche.core.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by liuzh on 2015/5/22.
 */
@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public void addFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    public Page<Feedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }
}
