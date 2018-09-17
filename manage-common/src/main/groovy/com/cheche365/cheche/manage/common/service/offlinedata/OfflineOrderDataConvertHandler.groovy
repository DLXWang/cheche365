package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.FileUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.util.NumberToTextConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by yinJianBin on 2017/9/26.
 */
@Slf4j
@Service
class OfflineOrderDataConvertHandler {

    @Autowired
    ResourceService resourceService

    static assertNotBlank(String object, Integer rowNum, String columnName, StringBuilder stringBuilder) {
        def errorMessage = columnName + "不能为空! "
        if (StringUtils.isBlank(object)) {
            stringBuilder.append errorMessage
        }
    }

    static assertNull(Object object, String errorMessage, StringBuilder stringBuilder) {
        if (object != null) {
            stringBuilder.append errorMessage
        }
    }


    static assertNotNull(Object object, Integer rowNum, String columnName, StringBuilder stringBuilder) {
        def errorMessage = columnName + "不能为空! "
        if (object == null) {
            stringBuilder.append errorMessage
        }
    }

    static Double toDouble(def object, Integer rowNum, String columnName, StringBuilder sb) {
        try {
            if (object == null) {
                return 0.0d;
            }
            return Double.valueOf(object);
        } catch (ignored) {
            def errorMessage = columnName + "必须为数值类型! "
            sb.append(errorMessage)
            return null
        }
    }

    static def writeFile(String path, Collection<String> dataModels) {
        StringBuilder sb = new StringBuilder()
        dataModels.each { sb.append(it) }
        FileUtil.appendFile(path, sb.toString().bytes)
    }

    def getResourceAbsoluteUrl(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return StringUtils.EMPTY;
        }
        String offlineInsurancePath = resourceService.getProperties().getOfflineInsurance();
        int index = filePath.indexOf(offlineInsurancePath)
        if ((index) > -1) {
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(offlineInsurancePath), filePath.substring(index + offlineInsurancePath.length(), filePath.length()));
        }
        return StringUtils.EMPTY;
    }


    static String getCellValue(Cell cell) {
        String ret;
        if (cell == null) {
            return ""
        }
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case CellType.BLANK:
                ret = "";
                break;
            case CellType.BOOLEAN:
                ret = String.valueOf(cell.getBooleanCellValue());
                break;
            case CellType.ERROR:
                ret = null;
                break;
            case CellType.FORMULA:
                throw new FileUploadException("表文件不能包含公式！")
            case CellType.NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date theDate = cell.getDateCellValue();
                    ret = DateUtils.getDateString(theDate, DateUtils.DATE_LONGTIME24_PATTERN);
                } else {
                    double numericCellValue = cell.getNumericCellValue();
                    if (numericCellValue % 1 != 0) {
                        ret = DoubleUtils.displayStringWithDecimal(numericCellValue, 2);
                    } else {
                        ret = NumberToTextConverter.toText(numericCellValue);
                    }
                }
                break;
            case CellType.STRING:
                ret = cell.getRichStringCellValue().getString();
                break;
            default:
                ret = null;
        }

        return StringUtils.trimToEmpty(ret); //有必要自行trim
    }


}
