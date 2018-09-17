package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.AppointmentInsurance;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by zhengwei on 7/24/15.
 */

@Service
@Transactional
public class AppointmentInsuranceService {

    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;

    public AppointmentInsurance getById(Long id) {
        return appointmentInsuranceRepository.findOne(id);
    }

    public AppointmentInsurance getAppointment(Long id, User user){

        return this.appointmentInsuranceRepository.searchById(id, user.getId());

    }

    public AppointmentInsurance addAppointment(AppointmentInsurance appointmentInsurance, Channel channel){
        appointmentInsurance.setSourceChannel(channel);
        return this.appointmentInsuranceRepository.save(appointmentInsurance);
    }

    public Page<AppointmentInsurance> getAppointmentPage(User user, Pageable pageable){
        return this.appointmentInsuranceRepository.searchAppointmentsPageable(user, pageable);
    }

    public Long getAppointmentsCount() {
        return this.appointmentInsuranceRepository.count();
    }


}
