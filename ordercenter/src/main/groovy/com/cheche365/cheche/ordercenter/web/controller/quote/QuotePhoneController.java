package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.QuotePhone;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhoneService;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhotoService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.quote.QuotePhoneViewModel;
import com.cheche365.cheche.ordercenter.web.model.quote.QuoteViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/10/15.
 */
@RestController
@RequestMapping("/orderCenter/quote/phone")
public class QuotePhoneController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuotePhoneService quotePhoneService;

    @Autowired
    private QuotePhotoService quotePhotoService;

    @RequestMapping(value = "/records",method = RequestMethod.GET)
    public QuoteViewModel findExistentQuoteAutos(@RequestParam(value = "licensePlateNo",required = true) String licensePlateNo) {
        if (logger.isDebugEnabled()) {
            logger.debug("find already exists phone or photo quote auto by licensePlateNo -> {}", licensePlateNo);
        }

        QuoteViewModel viewModel = new QuoteViewModel();
        List<QuotePhone> phoneList = quotePhoneService.findExistentQuotesByLicensePlateNo(licensePlateNo);
        viewModel.setPhones(quotePhoneService.createPhoneQuoteViewModel(phoneList));
        viewModel.setPhotos(quotePhotoService.listByLicensePlateNo(licensePlateNo));
        return viewModel;
    }

    @RequestMapping(value = "/auto",method = RequestMethod.GET)
    public QuotePhoneViewModel findExistentAuto(@RequestParam(value = "licensePlateNo",required = true) String licensePlateNo) {
        if (logger.isDebugEnabled()) {
            logger.debug("find auto if already exists by licensePlateNo -> {}", licensePlateNo);
        }

        return quotePhoneService.findExistentAutoByLicensePlateNo(licensePlateNo);
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public QuotePhoneViewModel savePhoneAuto(@Valid QuotePhoneViewModel quotePhoneViewModel, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("save new phone quote auto, licensePlateNo -> {}", quotePhoneViewModel.getLicensePlateNo());
        }
        if (bindingResult.hasErrors()) {
            logger.warn("some required params has missed.");
        }
        return quotePhoneService.createViewData(quotePhoneService.savePhoneQuoteAuto(quotePhoneViewModel));
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("or0502")
    public DataTablePageViewModel list(PublicQuery query) {
        Page<QuotePhone> page= quotePhoneService.getPhoneQuoteByPage(query);
        List<QuotePhoneViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(viewData -> modelList.add(quotePhoneService.createViewData(viewData)));
        PageInfo pageInfo = quotePhoneService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),query.getDraw(),modelList);
    }

    @RequestMapping(value = "/visited/{id}",method = RequestMethod.PUT)
    public ResultModel changeVisited(@PathVariable Long id,
                       @RequestParam(value = "visited",required = true) Integer visited){
        if (logger.isDebugEnabled()) {
            logger.debug("updateStatus phone visited1, id:{}, visited:{}", id, visited);
        }
        return quotePhoneService.changeVisited(id, visited);
    }

    @RequestMapping(value = "/{quotePhoneId}",method = RequestMethod.GET)
    public QuotePhoneViewModel findById(@PathVariable Long quotePhoneId){
        return quotePhoneService.findById(quotePhoneId);
    }

    @RequestMapping(value = "/comment",method = RequestMethod.PUT)
    public ModelAndViewResult setComment(@RequestParam(value = "comment",required = true) String comment,
                                         @RequestParam(value = "id",required = false) Long id){
        if(comment==null||id==null){
            return null;
        }
        return quotePhoneService.setComment(id, comment);
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public List<Channel> getChannels() {
        List<Channel> channels = this.quotePhoneService.getChannels();
        return channels;
    }
}
