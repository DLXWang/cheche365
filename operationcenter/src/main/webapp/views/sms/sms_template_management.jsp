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

        .diy-height {
            height: 600px;
        }
    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="1">模板号</option>
                <option value="0">模板名</option>
                <option value="2">短信内容</option>
            </select>

            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                    <span class="input-group-btn">
                        <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                    </span>
            </div>
            <span class="btn-group">
                <button id="toNew" class="btn btn-danger">新建短信模板</button>
            </span>
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
        <h4 id="template_title" class="text-center">新建短信模板</h4>
    </div>
    <div class="new" style="margin-bottom: 2px">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height" style="height:390px">
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">模板名：</span>

                    <div class="col-sm-8 text-left">
                        <input id="name" name="name" type="text" class="form-control text-height-28"
                               placeholder="请输入模板名，最多三十位" maxlength="30">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">漫道模板号：</span>

                    <div class="col-sm-8 text-left">
                        <input id="zucpCode" name="zucpCode" type="text" class="form-control text-height-28"
                               placeholder="请输入模板号，最多三十位" maxlength="30">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">盈信通模板号：</span>

                    <div class="col-sm-8 text-left">
                        <input id="yxtCode" name="yxtCode" type="text" class="form-control text-height-28"
                               placeholder="请输入模板号，最多三十位" maxlength="30">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">变量列表：</span>

                    <div class="col-sm-8 text-left">
                        <select name="from" id="variable_multiselect" class="form-control" size="5" multiple="multiple">

                        </select>
                    </div>

                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right"></span>

                    <div class="col-sm-8 text-left">
                        <button id="add_variable" type="button" class="btn btn-danger">插入变量</button>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">短信内容：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="content" name="content" class="form-control " rows="5" style="resize: none;"
                                  placeholder="请输入短信内容，最多一千位" maxlength="1000"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">备注：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control " rows="5" style="resize: none;"
                                  placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-3"></span>

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
                <input type="hidden" id="smsTemplateId" name="id" value="0">
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sms/sms_template_management.js"></script>
