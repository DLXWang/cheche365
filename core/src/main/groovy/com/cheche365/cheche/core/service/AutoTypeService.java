package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.AutoType;
import com.cheche365.cheche.core.repository.AutoTypeRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 车型库查询服务
 * TODO: 目前仅仅针对阳光车型API，所以仅仅注入一个externalService，今后有大地等接口时，这里就要变成List<IExternalAutoTypeService>
 * @author liqiang
 */
@Service
@Transactional
public class AutoTypeService implements IAutoTypeService {

    @Autowired
    private AutoTypeRepository autoTypeRepository;

    @Override
    public AutoType saveAutoType(AutoType autoType) {
        if (autoType == null) {
            return null;
        }

        if (autoType.getCode() == null || autoType.getCode().isEmpty()) {
            return this.autoTypeRepository.save(autoType);
        }

        AutoType autoTypeExisted = autoTypeRepository.findFirstByCode(autoType.getCode());
        if (autoTypeExisted == null) {
            return this.autoTypeRepository.save(autoType);
        }

        BeanUtil.copyPropertiesIgnore(autoType, autoTypeExisted, "id");
        return this.autoTypeRepository.save(autoTypeExisted);
    }


}
