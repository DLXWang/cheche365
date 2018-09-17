package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.SysVersion;
import com.cheche365.cheche.core.repository.SysVersionRepository;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService;
import com.cheche365.cheche.core.service.SystemCountService;
import com.cheche365.cheche.core.service.SystemService;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.rest.model.AreaResult;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.cheche365.cheche.core.model.Channel.Enum.ANDROID_6;
import static com.cheche365.cheche.core.model.Channel.Enum.ANDROID_CHEBAOYI_222;

/**
 * Created by mahong on 2015/6/12.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/system")
@VersionedResource(from = "1.0")
public class SystemResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(SystemResource.class);

    @Autowired
    private SysVersionRepository sysVersionRepository;

    @Autowired
    private SystemCountService systemCount;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    @Qualifier("botpyCheckAccountService")
    private IThirdPartyCheckAccountService botpyCheckAccountService;

    @Autowired
    @Qualifier("cpicUKCheckAccountService")
    private IThirdPartyCheckAccountService cpicUKCheckAccountService;

    @Autowired
    @Qualifier("piccUKCheckAccountService")
    private IThirdPartyCheckAccountService piccUKCheckAccountService;


    private static Map<Long, String> ANDROID_PKG_PREFIX = new HashMap<Long, String>() {
        {
            put(ANDROID_6.getId(), "cheche");
            put(ANDROID_CHEBAOYI_222.getId(), "chebaoyi");
        }
    };

    private String androidPkgPath;
    @PostConstruct
    private void init() {
        androidPkgPath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getAndroidInstallPackage());
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<SysVersion>> getLatestVersionInfo(@RequestParam String currentVersion,
                                                                             @RequestParam Long channelId) {

        if (!SysVersion.checkVersionStyle(currentVersion)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "当前版本输入格式错误");
        }

        SysVersion sysVersion = sysVersionRepository.findLatestSysVersionByChannel(channelId);
        SysVersion requiredSysVersion = sysVersionRepository.findLatestRequiredSysVersion(channelId);

        if (sysVersion == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "未查到任何版本信息");
        }

        int result = SysVersion.compareVersion(sysVersion.getLatestVersion(), currentVersion);
        if (result > 0) {
            sysVersion.setNeedUpdate(true);
            if (requiredSysVersion != null && SysVersion.compareVersion(requiredSysVersion.getLatestVersion(), currentVersion) > 0) {
                sysVersion.setUpdateAdvice(requiredSysVersion.getUpdateAdvice());
            }
            setDownloadUrl(channelId, sysVersion);
        } else {
            sysVersion.setNeedUpdate(false);
            sysVersion.setUpdateAdvice(null);
            sysVersion.setReason(null);
        }

        sysVersion.setIsNeedUpdate(sysVersion.isNeedUpdate()); //安卓2.2.3->2.2.4完全强升后可删除
        RestResponseEnvelope envelope = new RestResponseEnvelope(sysVersion);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    private void setDownloadUrl(Long channelId, SysVersion sysVersion) {
        Boolean android = ANDROID_6.getId().equals(channelId) || ANDROID_CHEBAOYI_222.getId().equals(channelId);
        if (android) {
            String downloadUrl = androidPkgPath.replace("version", sysVersion.getLatestVersion());
            downloadUrl = downloadUrl.replace("prefix", ANDROID_PKG_PREFIX.get(channelId));
            if (!RuntimeUtil.isProductionEnv()) {
                downloadUrl = downloadUrl.replace("android", "android/testApk");
            }
            sysVersion.setDownloadUrl(resourceService.absoluteUrl("", downloadUrl));
        }
    }

    @RequestMapping(value = "/area", method = RequestMethod.GET)
    public AreaResult getSupportedArea(@RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "lastModified", required = false) Long lastModified) {
        AreaResult areaResult = new AreaResult();
        if (lastModified == null || (lastModified != null && lastModified == 0)) {
            Area bjRoot = Area.Enum.getValueByCode(110000L);
            bjRoot.setChildren(Area.Enum.BJAreas);

            areaResult.setAreas(Arrays.asList(new Area[]{bjRoot}));
            areaResult.setLastModified(System.currentTimeMillis());
            areaResult.setNeedUpdate(true);
        } else {
            areaResult.setAreas(null);
            areaResult.setLastModified(lastModified);
            areaResult.setNeedUpdate(false);
        }
        return areaResult;
    }


    /**
     * 最新版获取所有预约车险，投保车主，投保车主省了多少钱信息
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public HashMap<String, Object> getOrderCount() {

        HashMap<String, Object> mapCount = systemCount.getSystemCountMap();

        return mapCount;
    }
    /**
     * channelId 渠道id  可选参数值：车保易安卓，车险安卓
     * 安卓最新版app下载
     */
    @RequestMapping(value = "app/download", method = RequestMethod.GET)
    public ModelAndView downLoadApp(@RequestParam(value = "channelId", required = false, defaultValue = "222") Long channelId) {

        String appDownloadPath = systemService.getMaxVersionApp(ANDROID_PKG_PREFIX.get(channelId), androidPkgPath);
        logger.info("downLoad app channelId:{},appPath:{}", channelId, appDownloadPath);
        return new ModelAndView(new RedirectView(resourceService.absoluteUrl(resourceService.getResourceUrl(appDownloadPath))));
    }


    @RequestMapping(value = "/status/{organization}/account", method = RequestMethod.GET)
    public Object account(@PathVariable(value = "organization") String organization){
        if ("botpy".equals(organization)){
            return botpyCheckAccountService.getFailedAccounts();
        } else if ("cpicuk".equals(organization)){
            return cpicUKCheckAccountService.getFailedAccounts();
        } else if ("piccuk".equals(organization)){
            return piccUKCheckAccountService.getFailedAccounts();
        }
        return new String[]{};
    }

}
