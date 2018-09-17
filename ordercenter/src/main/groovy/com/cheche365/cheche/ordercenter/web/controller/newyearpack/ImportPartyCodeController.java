package com.cheche365.cheche.ordercenter.web.controller.newyearpack;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xu.yelong on 2016/1/27.
 */
@RestController
@RequestMapping("/orderCenter/newyearpack/upload")
public class ImportPartyCodeController {
    private Logger logger = LoggerFactory.getLogger(ImportPartyCodeController.class);
    private static Map<Long, List> codeMap = new ConcurrentHashMap();

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;


    /**
     * 兑换码导入只会读取excel的第一列数据
     *
     * @param name
     * @param codeType
     * @param codeGroup
     * @param effectiveDate
     * @param expireDate
     * @param file
     * @param response
     */

    @RequestMapping(value = "", method = RequestMethod.POST)
    @VisitorPermission("or0802")
    public void upload(@RequestParam(value = "name", required = false) String name,
                       @RequestParam(value = "codeType", required = false) Long codeType,
                       @RequestParam(value = "codeGroup") Long codeGroup,
                       @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
                       @RequestParam(value = "expireDate", required = false) String expireDate,
                       @RequestParam(value = "codeFile", required = false) MultipartFile file,
                       HttpServletResponse response) {

    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Transactional
    @VisitorPermission("or0802")
    public void save(HttpServletResponse response) {

    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public List getCodeType() {
        Map typeMap = getCodeTypeMap();
        return new ArrayList<>(typeMap.values());
    }

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public List getCodeGroup() {
        Map codeMap = getCodeGroupMap();
        return new ArrayList<>(codeMap.values());
    }


    private Map getCodeTypeMap() {
        return new HashMap<>();
    }

    private Map getCodeGroupMap() {
        return new HashMap<>();
    }

    /**
     * @param response
     * @param ajaxString
     */
    private void outPrint(HttpServletResponse response, String ajaxString) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            out = response.getWriter();
            out.write(ajaxString);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException("write return string has error", ex);
        } finally {
            if (out != null)
                out.close();
        }
    }
}
