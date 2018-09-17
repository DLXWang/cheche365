package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.GiftStatus;
import com.cheche365.cheche.core.repository.GiftStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by WF on 2015/7/16.
 */

@Service
@Transactional
public class GiftStatusService {


    @Autowired
    GiftStatusRepository giftStatusRepository;



    public Iterable<GiftStatus> getAll(){
        return this.giftStatusRepository.findAll();
    }

}
