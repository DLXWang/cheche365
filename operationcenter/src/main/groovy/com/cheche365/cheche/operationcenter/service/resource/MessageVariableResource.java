package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.MessageVariable;
import com.cheche365.cheche.core.repository.MessageVariableRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.sms.MessageVariableViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class MessageVariableResource extends BaseService<MessageVariable, MessageVariable> {

    @Autowired
    private MessageVariableRepository messageVariableRepository;

    public List<MessageVariable> listAll() {
        return super.getAll(messageVariableRepository);
    }

    public List<MessageVariableViewModel> createViewData(List<MessageVariable> messageVariableList) {
        if (messageVariableList == null)
            return null;

        List<MessageVariableViewModel> viewDataList = new ArrayList<>();
        messageVariableList.forEach(messageVariable -> {
            MessageVariableViewModel viewData = new MessageVariableViewModel();
            viewData.setId(messageVariable.getId());
            viewData.setCode(messageVariable.getCode());
            viewData.setName(messageVariable.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
