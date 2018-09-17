package com.cheche365.cheche.operationcenter.web.controller.marketingRule;

import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.model.GiftTypeUseType;
import com.cheche365.cheche.operationcenter.service.marketingRule.GiftTypeService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.web.model.marketing.GiftRequestParams;
import com.cheche365.cheche.operationcenter.web.model.marketing.GiftTypeViewModel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yinJianBin on 2017/4/21.
 */
@RestController
@RequestMapping("/operationcenter/marketingRule/giftType")
public class GiftTypeController {

    private Logger logger = LoggerFactory.getLogger(GiftTypeController.class);

    @Autowired
    private GiftTypeService giftTypeService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public DataTablesPageViewModel<GiftTypeViewModel> list(GiftRequestParams params) {
        logger.info("根据条件查询giftType列表,条件参数{}", params.toString());
        DataTablesPageViewModel<GiftTypeViewModel> viewModel = new DataTablesPageViewModel<>();
        Page<GiftType> giftTypePage = giftTypeService.list(params);
        List<GiftTypeViewModel> viewModelList = new ArrayList<>(giftTypePage.getContent().size());
        giftTypePage.forEach(giftType -> {
            viewModelList.add(GiftTypeViewModel.createViewModel(giftType));
        });

        viewModel.setAaData(viewModelList);
        viewModel.setiTotalRecords(giftTypePage.getTotalElements());
        viewModel.setiTotalDisplayRecords(giftTypePage.getTotalElements());
        viewModel.setDraw(params.getDraw());

        return viewModel;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultModel add(GiftRequestParams params) {
        List<GiftType> giftTypeList = giftTypeService.uniqueCheck(params.getGiftName(), params.getGiftType(), params.getUseType());
        if (CollectionUtils.isNotEmpty(giftTypeList)) {
            return new ResultModel(false, "该礼物已经存在！");
        }
        giftTypeService.add(params);
        return new ResultModel(true, "保存成功！");
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public List<String> getByName(String paramWord, Integer pageSize) {
        List<String> names = giftTypeService.searchByName(paramWord, pageSize == null ? 10 : pageSize);
        return names;
    }

    @RequestMapping(value = "/giftTypeCategorys", method = RequestMethod.GET)
    public Map<Integer, String> getGiftTypeCategory() {
        return GiftTypeViewModel.getCategoryMappingShowed();
    }

    @RequestMapping(value = "/useTypes", method = RequestMethod.GET)
    public List<GiftTypeUseType> getUseTypes() {
        return Arrays.asList(GiftTypeUseType.Enum.GIVENAFTERORDER_3);//前台新增的时候只展示 下单后赠送
    }

    @RequestMapping(value = "/{id}/{disableStatus}", method = RequestMethod.POST)
    public ResultModel updateDisable(@PathVariable Long id, @PathVariable Integer disableStatus) {
        giftTypeService.updateDisable(id, disableStatus);
        return new ResultModel();
    }

}

