package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.alibaba.fastjson.JSONArray;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhotoService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.quote.QuotePhotoViewModel;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by xu.yelong on 2015/10/20.
 */
@RestController
@RequestMapping("/orderCenter/quote/photo")
public class QuotePhotoController {
    @Autowired
    private QuotePhotoService quotePhotoService;

    @Autowired
    private PurchaseOrderImageRepository purchaseOrderImageRepository;

    @Autowired
    private PurchaseOrderAuditingRepository purchaseOrderAuditingRepository;




    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VisitorPermission("or0501")
    public DataTablePageViewModel list(PublicQuery query) {
        Page<QuotePhoto> page = quotePhotoService.getQuotePhotoByPage(query);
        List<QuotePhotoViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(orderOperationInfo -> modelList.add(quotePhotoService.createViewModel(orderOperationInfo)));
        PageInfo pageInfo = quotePhotoService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    @RequestMapping(value = "channels", method = RequestMethod.GET)
    public List<Channel> getChannels() {
        List<Channel> channels = this.quotePhotoService.getChannels();
        return channels;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public QuotePhotoViewModel detail(@PathVariable Long id) {
        QuotePhoto photo = quotePhotoService.findById(id);
        return quotePhotoService.createViewModelWithSELF(photo);
    }

    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public QuotePhotoViewModel disable(@PathVariable Long id, Integer disable) {
        return quotePhotoService.createViewModel(quotePhotoService.setQuotePhotoDisable(id, disable));
    }

    @RequestMapping(value = "/visited/{id}", method = RequestMethod.PUT)
    public ModelAndViewResult visited(@PathVariable Long id, Integer visited) {
        return quotePhotoService.setQuotePhotoVistited(id, visited);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public QuotePhotoViewModel update(@Valid QuotePhotoViewModel viewModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return viewModel;
        }
        if (viewModel.getId() == null || viewModel.getId() < 1) {
            return viewModel;
        }
        return quotePhotoService.update(viewModel);
    }

    //查询已有车牌号
    @RequestMapping(value = "/licensePlateNo", method = RequestMethod.GET)
    public List<String> findLicensePlateNo(@RequestParam(value = "userId", required = true) Long userId) {
        if (userId == null) {
            return null;
        }
        return quotePhotoService.findLicensePlateNoByUser(userId);
    }

    //更换车牌号
    @RequestMapping(value = "/auto", method = RequestMethod.GET)
    public QuotePhotoViewModel changeAuto(@RequestParam(value = "licensePlateNo", required = true) String licensePlateNo,
                                          @RequestParam(value = "id", required = false) Long id,
                                          @RequestParam(value = "currentId", required = true) Long currentId) {
        if (licensePlateNo == null || currentId == null) {
            return null;
        }
        return quotePhotoService.createViewModel(quotePhotoService.changeAuto(licensePlateNo, id, currentId));
    }

    @RequestMapping(value = "/comment", method = RequestMethod.PUT)
    public ModelAndViewResult setComment(@RequestParam(value = "comment", required = true) String comment,
                                         @RequestParam(value = "id", required = false) Long id) {
        if (comment == null || id == null) {
            return null;
        }
        return quotePhotoService.setComment(id, comment);
    }

    @RequestMapping(value = "/hint", method = RequestMethod.GET)
    public JSONObject getHint(@RequestParam(value = "id", required = true) Long id) {
        String hint = purchaseOrderImageRepository.findHintById(id);
        if(hint == null||hint == ""){
            hint = "k";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("hint",hint);
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject;
    }

    @RequestMapping(value = "/setHint", method = RequestMethod.GET)
    public JSONObject setHint(@RequestParam(value = "hint", required = false) String hint,
                              @RequestParam(value = "id", required = false) Long id) {
        Map<String, String> map = new HashMap<String, String>();
        String flag;
        if (id == null) {
            flag ="0";
        }else {
            flag = "1";
            PurchaseOrderImage poi = purchaseOrderImageRepository.findOne(id);
            poi.setHint(hint);
            purchaseOrderImageRepository.save(poi);
        }
        map.put("result",flag);
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject;
    }

    @RequestMapping(value = "/setLog", method = RequestMethod.GET)
    public JSONObject setLog(
        @RequestParam(value = "id", required = false) Long id,
        @RequestParam(value = "hint", required = false) String hint,
        @RequestParam(value = "status", required = false) int Status
    ) {
        Map<String, String> map = new HashMap<String, String>();
        String flag="0";
        PurchaseOrderAuditing poa = new PurchaseOrderAuditing();
        PurchaseOrderImage poi = purchaseOrderImageRepository.findOne(id);
        poa.setCreateTime(new Date());
        poa.setPurchaseOrderImage(poi);
        poa.setStatus(Status);
        poa.setHint(hint);
        purchaseOrderAuditingRepository.save(poa);
        map.put("result",flag);
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject;
    }

}
