package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.scheduletask.model.ExcelAttachmentConfig;
import com.cheche365.cheche.scheduletask.model.AttachmentData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by guoweifu on 2015/12/24.
 */
public interface IAttachmentService {

    /**
     * 创建附件
     * @param attachmentDataList 附件数据list
     * @param excelAttachmentConfig 附件
     * @return 返回文件名称和路径map
     * @throws IOException
     */
    public Map<String, String> createSimpleAttachment(List<? extends AttachmentData> attachmentDataList,Map<String, Object> paramMap, ExcelAttachmentConfig excelAttachmentConfig) throws IOException;


    /**
     * 创建附件
     * @param excelAttachmentConfig excel附件配置信息
     * @param paramMap 附件参数
     * @param dataSetMaps 附件数据集 Map<"order",orderList>
     * @return
     * @throws IOException
     */
    public Map<String, String> createAttachment(ExcelAttachmentConfig excelAttachmentConfig,Map<String, Object> paramMap,Map<String,? extends List<? extends AttachmentData>> dataSetMaps) throws IOException;

}
