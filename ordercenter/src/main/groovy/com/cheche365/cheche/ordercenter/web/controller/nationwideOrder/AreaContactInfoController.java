package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.AreaContactInfo;
import com.cheche365.cheche.core.service.AreaContactInfoService;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.AreaContactInfoManagerService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionManageService;
import com.cheche365.cheche.ordercenter.service.resource.AreaResource;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.AreaContactInfoViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 全国分站
 * Created by xu.yelong on 2015/11/12.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/areaContactInfo")
public class AreaContactInfoController {

    @Autowired
    private AreaContactInfoManagerService areaContactInfoManagerService;

    @Autowired
    private AreaContactInfoService areaContactInfoService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private InstitutionManageService institutionManageService;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private AreaResource areaResource;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    //@VisitorPermission("or070201")
    public PageViewModel<AreaContactInfoViewModel> findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                           @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "keyType", required = false) Integer keyType) {
        if (currentPage == null || currentPage < 1 || pageSize == null || pageSize < 1) {
            return null;
        }
        return createPageViewModel(areaContactInfoService.listQuotePhoto(currentPage, pageSize, keyword, keyType));
}

    @RequestMapping(value = "/allArea", method = RequestMethod.GET)
    public List<AreaViewData> findAreaByActive() {
        List<Area> areas = areaResource.listByCache();
        return createAreaViewData(areas);
    }

    @RequestMapping(value = "/area", method = RequestMethod.GET)
    public List<AreaViewData> findAreaByInstitutionAndEnable(){
        List<Area> areaList=institutionManageService.findAreaByInstitutionAndEnable(true);
        return createAreaViewData(areaList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AreaContactInfoViewModel detail(@PathVariable Long id) {
        return createViewModel(areaContactInfoService.findById(id));
    }




    @RequestMapping(value = "/update", method = RequestMethod.PUT)
//    @VisitorPermission("or07020102")
    public ResultModel update(@Valid AreaContactInfoViewModel viewModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return null;
        }

        if(areaExist(viewModel)){
            return new ResultModel(false,"该城市已存在");
        }

        return areaContactInfoManagerService.update(createAreaContactInfo(viewModel));
    }

    @RequestMapping(value = "/save", method = RequestMethod.PUT)
//    @VisitorPermission("or07020101")
    public ResultModel save(@Valid AreaContactInfoViewModel viewModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return null;
        }

        if(areaExist(viewModel)){
            return new ResultModel(false,"该城市已存在");
        }

        return areaContactInfoManagerService.update(createAreaContactInfo(viewModel));
    }

    private PageViewModel<AreaContactInfoViewModel> createPageViewModel(Page page) {
        PageViewModel model = new PageViewModel<AreaContactInfoViewModel>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<AreaContactInfoViewModel> pageViewDataList = new ArrayList<AreaContactInfoViewModel>();
        for (AreaContactInfo areaContactInfo : (List<AreaContactInfo>) page.getContent()) {
            AreaContactInfoViewModel viewData = createViewModel(areaContactInfo);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);
        return model;
    }


    private List<AreaViewData> createAreaViewData(List<Area> areas) {
        List<AreaViewData> areaViewDatas = new ArrayList<AreaViewData>();
        for (Area area : areas) {
            AreaViewData areaViewData = new AreaViewData();
            areaViewData.setId(area.getId());
            areaViewData.setName(area.getName());
            areaViewDatas.add(areaViewData);
        }
        return areaViewDatas;
    }


    private AreaContactInfoViewModel createViewModel(AreaContactInfo areaContactInfo) {
        AreaContactInfoViewModel model = new AreaContactInfoViewModel();
        String[] contains = new String[]{"id", "name", "email", "mobile", "qq", "street",
            "comment", "createTime", "updateTime"};
        BeanUtil.copyPropertiesContain(areaContactInfo, model, contains);
        model.setAreaName(areaContactInfo.getArea() != null ? areaContactInfo.getArea().getName() : null);
        model.setArea(areaContactInfo.getArea() != null ? areaContactInfo.getArea().getId() : null);
        model.setProvinceName(areaContactInfo.getProvince() != null ? areaContactInfo.getProvince().getName() : null);
        model.setProvince(areaContactInfo.getProvince() != null ? areaContactInfo.getProvince().getId() : null);
        model.setCityName(areaContactInfo.getCity() != null ? areaContactInfo.getCity().getName() : null);
        model.setCity(areaContactInfo.getCity() != null ? areaContactInfo.getCity().getId() : null);
        model.setDistrictName(areaContactInfo.getDistrict() != null ? areaContactInfo.getDistrict().getName() : null);
        model.setDistrict(areaContactInfo.getDistrict() != null ? areaContactInfo.getDistrict().getId() : null);
        model.setOperator(areaContactInfo.getOperator() != null ? areaContactInfo.getOperator().getName() : null);
        model.setUpdateTime(areaContactInfo.getUpdateTime() != null ? DateUtils.getDateString(areaContactInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN) : "");
        return model;
    }

    private AreaContactInfo createAreaContactInfo(AreaContactInfoViewModel viewModel) {
        if (viewModel == null) {
            return null;
        }
        AreaContactInfo areaContactInfo = new AreaContactInfo();
        areaContactInfo.setCreateTime(new Date());
        if (viewModel.getId() != null) {
            areaContactInfo = areaContactInfoService.findById(viewModel.getId());
        }
        areaContactInfo.setUpdateTime(new Date());
        areaContactInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        areaContactInfo.setComment(StringUtil.trim(viewModel.getComment()));
        areaContactInfo.setArea(createArea(viewModel.getArea(), areaContactInfo.getArea()));
        areaContactInfo.setProvince(createArea(viewModel.getProvince(), areaContactInfo.getProvince()));
        areaContactInfo.setCity(createArea(viewModel.getCity(), areaContactInfo.getCity()));
        areaContactInfo.setDistrict(createArea(viewModel.getDistrict(), areaContactInfo.getDistrict()));
        String[] contains = new String[]{"id", "name", "email", "mobile", "qq", "street"};
        BeanUtil.copyPropertiesContain(viewModel, areaContactInfo, contains);
        return areaContactInfo;
    }

    private Area createArea(Long id,Area area){
        if(id==null){
            return null;
        }
        if(area==null||!area.getId().equals(id)){
            return areaService.findById(id);
        }
        return area;
    }

    private boolean areaExist(AreaContactInfoViewModel viewModel){
        Area area=areaService.findById(viewModel.getArea());
        if(area==null){
            return false;
        }
        AreaContactInfo areaContactInfo=areaContactInfoService.findByArea(area);
        if(viewModel.getId()==null&&areaContactInfo!=null){
            return true;
        }else if(viewModel.getId()!=null&&!areaContactInfo.getId().equals(viewModel.getId())){
            return true;
        }
        return false;
    }

}
