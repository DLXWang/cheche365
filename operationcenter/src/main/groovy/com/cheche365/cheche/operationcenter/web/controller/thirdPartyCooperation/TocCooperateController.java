package com.cheche365.cheche.operationcenter.web.controller.thirdPartyCooperation;


import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.repository.ApiPartnerRepository;
import com.cheche365.cheche.core.service.ChannelService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.PartnerQuery;
import com.cheche365.cheche.operationcenter.service.partner.PartnerService;
import com.cheche365.cheche.operationcenter.service.thirdPartyCooperation.CooperateCommonService;
import com.cheche365.cheche.operationcenter.service.thirdPartyCooperation.CooperateService;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.ChannelManagerViewModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.TocDetailsViewModel;
import groovy.ui.SystemOutputInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;

/**
 * Created by liulu on 2018/4/17.
 * ToC合作管理Controller
 */
@RestController
@RequestMapping("/operationcenter/thirdParty/tocCooperate")
public class TocCooperateController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CooperateService cooperateService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ApiPartnerRepository apiPartnerRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CooperateCommonService commonService;
    private static final String TEMPLATE_FILE_NAME = "运营中心第三方合作ToC渠道配置参数模板.xlsx";

    /**
     * toC详情修改
     *
     * @return
     */
    @RequestMapping(value = "/updateDetails", method = RequestMethod.POST)
    public ResultModel update(@Valid PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOC);
        return commonService.updateChannel(query);
    }

    /**
     * toC详情页查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findDetailsInfo", method = RequestMethod.GET)
    public TocDetailsViewModel findDetailsInfo(@RequestParam(value = "id", required = true) Long id) {
        return cooperateService.findDetailsInfo(id);
    }
//
//    /**
//     * 下载渠道参数配置
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
//    public ResultModel getTemplateUrl(@RequestParam(value = "id", required = true) Long id) {
//        Channel channel = Channel.toChannel(id);
//        String templatePath = channelService.generatedConfigFile(channel);
//        String url = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME);
//        return new ResultModel(true, url);
//    }

    /**
     * toc 列表
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/partnerList", method = RequestMethod.GET)
    public DataTablePageViewModel<ChannelManagerViewModel> partnerList(PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOC);
        Page<ChannelManagerViewModel> channelList = commonService.findChannelBySpecAndPaginate(query);
        return new DataTablePageViewModel<>(channelList.getTotalElements(), channelList.getTotalElements(), query.getDraw(), channelList.getContent());

    }

    /**
     * 查询渠道名称填充下拉框
     *
     * @return
     */
    @RequestMapping(value = "/channelNameList", method = RequestMethod.GET)
    public List<Channel> channelNameList() {
        List<Channel> channels = channelService.selectAll();
        return channels;
    }

    /**
     * 查询所有合作商填充下拉框
     *updateDetails
     * @return
     */
    @RequestMapping(value = "/partnerNameList", method = RequestMethod.GET)
    public List<Partner> partnerNameList() {
        List<Partner> partners = partnerService.selectAll();
        return partners;
    }

    /**
     * 查询所有渠道英文名称填充下拉框
     *
     * @return
     */
    @RequestMapping(value = "/channelCodeList", method = RequestMethod.GET)
    public List<ApiPartner> channelCodeList() {
        List<ApiPartner> apiPartners = apiPartnerRepository.findAll();
        return apiPartners;
    }

    /**
     * 上下线
     *
     * @return
     */
    @RequestMapping(value = "/chgAble", method = RequestMethod.GET)
    public ResultModel chgAble(@RequestParam(value = "id", required = true) Long id,
                               @RequestParam(value = "status", required = true) Boolean status) {
        PartnerQuery query = new PartnerQuery();
        query.setChannel(id.toString());
        query.setStatus(status?"1":"0");
        Boolean result =  commonService.chgAble(query);
        if(result){
            return new ResultModel(true,"成功");
        }else{
            return new ResultModel(false,"失败");
        }
    }


    /**
     * 添加
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultModel add(@Valid PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOC);
        return commonService.createChannel(query);
    }
//
//    /**
//     * 添加
//     *
//     * @return
//     */
//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public ResultModel add(@Valid PartnerQuery query) {
//        query.setPartnerType(PartnerQuery.PartnerType.TOC);
//        return commonService.createChannel(query);
//    }

    /**
     * url
     *
     * @return
     */
    @RequestMapping(value = "/updateUrl", method = RequestMethod.GET)
    public ResultModel updateUrl(@RequestParam(value = "id", required = true) Long id, HttpServletResponse response) {
        Channel channel = Channel.toChannel(id);
        String path = channelService.generatedConfigFile(channel);
        downloadFile(path, "配置文件.txt",response);
        return new ResultModel(true,"成功");
    }

    private void downloadFile(String path, String fileName, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(path);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((fileName).getBytes(), "iso-8859-1"));
            response.setHeader("Content-Length",String.valueOf(file.length()));

            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(response.getOutputStream());

            byte[] data = new byte[1024];
            int len;
            while (-1 != (len = in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } catch (Exception ex) {
            logger.error("download file" + path + fileName + " has error", ex);
        } finally {
            try {
                if (in != null)
                    in.close();

                if(out != null)
                    out.close();
            } catch (IOException e) {
                logger.error("close stream has error", e);
            }
        }
    }




}
