package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 2015/6/8.
 */
@Component
public class InsuranceCompanyResource extends BaseService<InsuranceCompany, InsuranceCompany> {

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository;



    public List<InsuranceCompany> listAll() {
        return super.getAll(insuranceCompanyRepository);
    }


    public List<InsuranceCompany> findQuotableCompanies() {
        Iterable<InsuranceCompany> iterable = InsuranceCompany.ocQuoteAndDisplayCompanies();
        Iterator<InsuranceCompany> iterator = iterable.iterator();
        List<InsuranceCompany> insuranceCompanyList = new ArrayList<>();
        while (iterator.hasNext()) {
            insuranceCompanyList.add(iterator.next());
        }
        return insuranceCompanyList;
    }

    public List<InsuranceCompany> findQuotableCompaniesByArea(Long areaId) {
        Area area = areaRepository.findById(areaId);
        if (area == null) {
            return null;
        }
        List<InsuranceCompany> insuranceCompanyList=quoteFlowConfigRepository.findInsuranceCompanyByArea(area);
        return insuranceCompanyList;
    }

}
