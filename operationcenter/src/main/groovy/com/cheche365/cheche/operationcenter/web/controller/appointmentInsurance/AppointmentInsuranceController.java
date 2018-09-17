package com.cheche365.cheche.operationcenter.web.controller.appointmentInsurance;

import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.operationcenter.service.appointmentInsurance.AppointmentInsuranceService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.web.model.appointmentInsurance.AppointmentInsuranceViewModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by zhangshitao on 2015/10/31.
 */
@RestController
@RequestMapping("/operationcenter/appointmentinsurances")
public class AppointmentInsuranceController {

    @Autowired
    private AppointmentInsuranceService opcAppointmentInsuranceService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 根据条件查询地推用户
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @param keyType
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public PageViewModel<AppointmentInsuranceViewModel> search(
            @RequestParam(value = "currentPage",required = true) Integer currentPage,
            @RequestParam(value = "pageSize",required = true) Integer pageSize,
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "keyType",required = false) Integer keyType,
            @RequestParam(value = "datetime",required = false) String date) {
        if(currentPage == null || currentPage < 1 ){
            throw new FieldValidtorException("list appointmentinsurance, currentPage can not be null or less than 1");
        }

        if(pageSize == null || pageSize < 1 ){
            throw new FieldValidtorException("list appointmentinsurance, pageSize can not be null or less than 1");
        }

        return opcAppointmentInsuranceService.search(currentPage,pageSize,keyword,date,keyType);
    }

    /**
     * 更新地推用户
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ResultModel updatePush(@RequestParam(value = "name",required = true) String name,
                           @RequestParam(value = "autoNo",required = true) String licensePlateNo,
                           @RequestParam(value = "endDate",required = true) String expireBefore,
                           @RequestParam(value = "appointmentInsurance",required = true) String appointmentInsuranceId,
                           @RequestParam(value = "updateUserId",required = true) String userId,
                           @RequestParam(value = "comment",required = true) String comment){
        if(name == null||"".equals(name.trim())){
            throw new FieldValidtorException("list appointmentinsurance, name can not be null");
        }
        if(licensePlateNo == null||"".equals(licensePlateNo.trim())){
            throw new FieldValidtorException("list appointmentinsurance, licensePlateNo can not be null");
        }
        if(expireBefore == null||"".equals(expireBefore.trim())){
            throw new FieldValidtorException("list appointmentinsurance, expireBefore can not be null");
        }
        if(userId == null||"".equals(userId.trim())){
            throw new FieldValidtorException("list appointmentinsurance, userId can not be null");
        }
        if(appointmentInsuranceId == null||"".equals(appointmentInsuranceId.trim())){
            throw new FieldValidtorException("list appointmentinsurance, appointmentInsuranceId can not be null");
        }

        opcAppointmentInsuranceService.appointmentInsurance(appointmentInsuranceId, userId, name, licensePlateNo, expireBefore,comment);
        return new ResultModel();
    }

    /**
     * 更改处理状态
     */
    @RequestMapping(value = "/updatestatus",method = RequestMethod.POST)
    public ResultModel updateStatus(@RequestParam(value = "appointmentInsuranceId",required = true)String appointmentInsuranceId,
                             @RequestParam(value = "status",required = true)Integer status){
        if(appointmentInsuranceId == null || "".equals(appointmentInsuranceId.trim())){
            throw new FieldValidtorException("list appointmentInsuranceId, appointmentInsuranceId can not be null");
        }
        if(status == null){
            throw new FieldValidtorException("list status, status can not be null");
        }
        opcAppointmentInsuranceService.updateStatus(appointmentInsuranceId, status);
        return new ResultModel();
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/export")
    public void exportExcel(HttpServletResponse response){
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(("地推信息表.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = opcAppointmentInsuranceService.createExportExcel();
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export appointmentInsurance result has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export appointmentInsurance  result, close OutputStream has error", ex);
            }
        }
    }

    /**
     * 获取地推用户信息详情.
     * @param appointmentInsuranceId
     * @return
     */
    @RequestMapping(value = "/findOne",method = RequestMethod.POST)
    public AppointmentInsuranceViewModel findOne(@RequestParam(value = "appointmentInsuranceId",required = true) Long appointmentInsuranceId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get appointmentInsurance detail,id:" + appointmentInsuranceId);
        }
        if(appointmentInsuranceId == null || appointmentInsuranceId < 1){
            throw new FieldValidtorException("find appointmentInsurance detail, appointmentInsuranceId can not be null");
        }
        return opcAppointmentInsuranceService.findOne(appointmentInsuranceId);
    }
}
