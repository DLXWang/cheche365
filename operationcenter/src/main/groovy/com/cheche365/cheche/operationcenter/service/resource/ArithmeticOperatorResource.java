package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.ArithmeticOperator;
import com.cheche365.cheche.core.repository.ArithmeticOperatorRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.ArithmeticOperatorViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class ArithmeticOperatorResource extends BaseService<ArithmeticOperator, ArithmeticOperator> {

    @Autowired
    private ArithmeticOperatorRepository arithmeticOperatorRepository;

    public List<ArithmeticOperator> listAll() {
        return super.getAll(arithmeticOperatorRepository);
    }

    public List<ArithmeticOperatorViewModel> createViewData(List<ArithmeticOperator> arithmeticOperatorList) {
        if (arithmeticOperatorList == null)
            return null;

        List<ArithmeticOperatorViewModel> viewDataList = new ArrayList<>();
        arithmeticOperatorList.forEach(arithmeticOperator -> {
            ArithmeticOperatorViewModel viewData = new ArithmeticOperatorViewModel();
            viewData.setId(arithmeticOperator.getId());
            viewData.setName(arithmeticOperator.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
