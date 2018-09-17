<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .form-horizontal .form-group {
            margin-right: 0 !important;
        }
        #template_auto_div {
            height: 90%;
            overflow-y:auto;
            padding: 10px;
        }
    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select  id="keyType" class="form-control text-input-150">
                <option value="1">模板号</option>
                <option value="0">模板名</option>
                <option value="2">短信内容</option>
                <option value="3">备注</option>
            </select>
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                    <span class="input-group-btn">
                        <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                    </span>
            </div>
            <span class="btn-group">
                <button id="toNew" class="btn btn-danger">新建条件触发短信</button>
            </span>
        </div>
    </div>
</div>

<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
        </table>
        <div class="customer-pagination">
            <ul class="pagination"></ul>
        </div>
    </div>
</div>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="ls_new_sms_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="conditions_title" class="text-center">新建条件触发短信</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <input id="id" name="id" type="hidden">
            <input id="templateId" name="smsTemplateId" type="hidden">
            <input id="conditionId" name="conditionId" type="hidden">
            <div class="diy-height">
                <div class="form-group">
                    <div class="col-sm-8 text-left">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">短信模板：</span>
                    <div class="col-sm-8 text-left">
                        <input id="templateSel" name="smsTemplate" class="form-control text-input-200 text-height-28" placeholder="点击选择短信模板" readonly>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">漫道模板号：</span>
                    <div class="col-sm-8 text-left">
                        <span id="mdTemplateNo" type="text" name="mdTemplateNo" class="text-height-28 text-input-200" >
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">盈信通模板号：</span>
                    <div class="col-sm-8 text-left">
                        <span id="yxtTemplateNo" name="yxtTemplateNo" class="text-input-200 text-height-28 select-28">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">短信内容：</span>
                    <div class="col-sm-8 text-left">
                        <textarea id="content" name="content" class="form-control text-input-280" rows="5" style="resize: none;" maxlength="200" disabled="true"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">触发条件：</span>
                    <div class="col-sm-8 text-left">
                        <select id="conditionsSel" name="scheduleCondition" class="form-control text-input-200 text-height-28 select-28">
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">备注：</span>
                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control text-input-280" rows="5" style="resize: none;" placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>
                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <span class="col-sm-4 text-height-28 text-right"></span>
                <div class="col-sm-8 text-left">
                    <input id="toCreate" type="button" class="btn btn-danger text-input-200" value="保存">
                </div>
            </div>
        </form>
    </div>
</div>
<div id="template_div" class="table-responsive">
    <div class="theme_poptit">
        <a id="ls_template_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center"><b>请选择模板</b></h4>
    </div>
    <div id="template_auto_div" class="col-sm-12">
        <table class="table table-bordered table-hover" id="template_list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">模板号</th>
                <th class="text-center">模板名</th>
                <th class="text-center">短信内容</th>
                <th class="text-center"></th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sms/schedule_message_management.js"></script>
