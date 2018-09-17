<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .upload-btn {
            padding-top: 0 !important;
        }

        .new {
            padding-top: 15px;
        }

        .error-line {
            height: 10px;
        }

        .error-msg {
            margin: 0 !important;
            padding: 2px !important;
        }

        .btn-finish {
            padding-top: 10px;
        }

        .remove-a {
            padding: 0 10px;
        }

        .form-horizontal .form-group {
            margin-right: 0 !important;
        }
    </style>
</head>

<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="searchSel" class="form-control text-input-150">
                <option>合作商名称</option>
            </select>

            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder=""/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
            <div class="btn-group">
                <button id="toNew" class="btn btn-danger">新建合作商</button>
            </div>
        </div>
    </div>
</div>

<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
        </table>
    </div>
</div>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 id="partner_title" class="text-center">新建合作商</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height">
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">合作商名称：</span>

                    <div class="col-sm-8 text-left">
                        <input id="name" name="name" type="text" class="form-control text-input-280 text-height-28"
                               placeholder="请输入合作商名称，最多二十位" maxlength="20">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">支持合作方式：</span>

                    <div class="col-sm-8 text-left" id="cooperationModes">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">合作商类型：</span>

                    <div class="col-sm-8 text-left">
                        <select id="partnerTypeSel" name="partnerType.id"
                                class="form-control text-input-200 text-height-28 select-28">
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">预计首次合作日期：</span>

                    <div class="col-sm-8 text-left">
                        <input id="cooperationTime" type="text" name="cooperationTime"
                               class="form-control text-height-28 text-input-200 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">合同：</span>

                    <div class="col-sm-8 text-left">
                        <button type="button" id="upload_btn_1"
                                class="btn btn-info text-height-28 text-input-200 upload-btn">上传文件(20M以内)
                        </button>
                        <div class="text-left form-inline none">
                            <span id="upload_btn_1_text" class="text-height-28 text-input-200" title="">&nbsp;</span><a
                            href="javascript:;" id="upload_btn_1_remove" class="remove-a" title="删除"><i
                            class='glyphicon glyphicon-remove'></i></a>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">技术文档：</span>

                    <div class="col-sm-8 text-left">
                        <button type="button" id="upload_btn_2"
                                class="btn btn-info text-height-28 text-input-200 upload-btn">上传文件(20M以内)
                        </button>
                        <div class="text-left form-inline none">
                            <span id="upload_btn_2_text" class="text-height-28 text-input-200" title="">&nbsp;</span><a
                            href="javascript:;" id="upload_btn_2_remove" class="remove-a" title="删除"><i
                            class='glyphicon glyphicon-remove'></i></a>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">备注：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control text-input-280" rows="5"
                                  style="resize: none;" placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>

                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <span class="col-sm-4 text-height-28 text-right"></span>

                <div class="col-sm-8 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200" value="保存">
                </div>
            </div>
            <div>
                <input type="hidden" id="partnerAttachmentId" name="partnerAttachment.id" value="0">
                <input type="hidden" id="partnerId" name="id" value="0">
                <input type="hidden" id="fileType" name="fileType" value="">
                <input type="hidden" id="contractUrlNew" name="partnerAttachment.contractUrl" value="">
                <input type="hidden" id="contractNameNew" name="partnerAttachment.contractName" value="">
                <input type="hidden" id="technicalDocumentUrlNew" name="partnerAttachment.technicalDocumentUrl"
                       value="">
                <input type="hidden" id="technicalDocumentNameNew" name="partnerAttachment.technicalDocumentName"
                       value="">
            </div>
        </form>
    </div>
</div>
<div id="upload_content" class="none">
    <input type="file" name="uploadify" id="uploadify">

    <div id="fileQueue" class="uploadify-queue" style="height: 100px;width: 200px;"></div>
    <p style="padding-left: 400px;">
        <a class="btn btn-primary" href=javascript:$("#uploadify").uploadify("upload","*");>上传</a>
        <%--<a style="margin-left: 10px;" class="btn btn-default" href=javascript:$("#uploadify").uploadify("cancel","*");>取消上传</a>--%>
        <a class="shutDown btn btn-default" style="margin-left: 20px;">关闭</a>
    </p>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/partner/partner_management.js"></script>

