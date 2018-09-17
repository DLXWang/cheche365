<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .form-horizontal .form-group {
            margin-right: 0 !important;
        }

        #template_auto_div {
            height: 90%;
            overflow-y: auto;
            padding: 10px;
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

        .textinput {
            width: 400px;
            min-height: 120px;
            max-height: 300px;
            _height: 120px;
            margin-left: auto;
            margin-right: auto;
            padding: 3px;
            outline: 0;
            border: 1px solid #a0b3d6;
            font-size: 12px;
            word-wrap: break-word;
            overflow-x: hidden;
            overflow-y: auto;
            _overflow-y: visible;
        }

    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="1">模板号</option>
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
                <button id="toNew" class="btn btn-danger">新建主动发送短信</button>
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
        <a id="new_smsContent_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 id="conditions_title" class="text-center">新建主动发送短信</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <input id="smsContentView" name="smsContentView" type="hidden">
            <input id="smsTemplateId" name="smsTemplateId" type="hidden">
            <input id="parameter" name="parameter" type="hidden">
            <input id="id" name="id" type="hidden" value="0">

            <div class="diy-height">
                <div class="form-group">
                    <div class="col-sm-8 text-left">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">发送用户：</span>

                    <div class="col-sm-4 text-left">
                        <select class="form-control  text-input-150 text-height-29" id="sendUser" name="sendUser">
                            <option value="1" selected>用户群</option>
                            <option value="2">单一用户</option>
                        </select>
                    </div>
                    <div class="col-sm-3 text-left" id="filterUserListDiv">
                        <select class="form-control  text-input-150 text-height-29" id="filterUserList"
                                name="filterUserId">
                        </select>

                    </div>
                    <div class="col-sm-3 text-left" id="singleUserDiv" style="display:none;">
                        <input style="width:180px" class="form-control  text-input-180 text-height-29" id="singleUser"
                               name="mobile" maxlength="11" placeholder="请输入11位手机号码">
                    </div>
                    <div class="col-sm-2 text-left text-height-28" id="userCountDiv">
                        <span id="userCount" style="color:red; font-weight: bold;"></span> 人
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">短信模板：</span>

                    <div class="col-sm-8 text-left">
                        <input id="templateName" name="templateName"
                               class="form-control text-input-200 text-height-28 select-28" placeholder="点击选择短信模板"
                               readonly>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">短信内容：</span>

                    <div class="col-sm-8 text-left">
                        <div id="content" name="content" class="form-control textinput" style="resize: none;"
                             readonly></div>
                    </div>
                </div>
                <div id="variable">
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">发送时间：</span>
                    <label class="radio-inline col-sm-3 text-center" id="immediateDiv">
                        <input type="radio" value="0" id="userTime1" name="sendFlag" checked> 立即发送
                    </label>
                </div>
                <div class="form-group" id="timedDiv">
                    <span class="col-sm-3 text-height-28 text-right"></span>
                    <label class="radio-inline col-sm-3 text-center">
                        <input type="radio" value="1" id="userTime2" name="sendFlag"> 定时发送
                    </label>
                    <label class="radio-inline col-sm-4 text-left">
                        <input type="text" id="userTime" name="sendTime" class="form-control text-height-28 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});" readonly>
                    </label>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">备注：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control text-input-320" rows="5"
                                  style="resize: none;" placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>

                <div class="col-sm-8 text-left">
                    <p class="alert alert-danger text-input-280 error-msg none"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="errorText"></span></p>
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
<div id="preview_content_div" class="none">
    <div class="theme_poptit">
        <h4 id="preview_title" class="text-center">新建完成预览</h4>
    </div>
    <div class="new">
        <form id="preview_form" class="form-input form-horizontal">
            <div>
                <div class="form-group">
                    <div class="col-sm-8 text-left">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">发送用户：</span>

                    <div class="col-sm-8 text-left">
                        <span class="text-height-28 text-right" id="previewUser">用户筛选功能名字</span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">短信内容：</span>

                    <div id="smsContentViewDiv" class="col-sm-8 text-left">
                        <textarea id="previewContent" name="previewContent" class="form-control text-input-280" rows="8"
                                  style="resize: none;" maxlength="200" readonly></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group btn-finish">
                <div class="col-sm-12 text-center">
                    <input id="toSave" type="button" class="btn btn-danger text-input-150" value="确定">&nbsp;&nbsp;&nbsp;
                    <input id="toFix" type="button" class="btn btn-danger text-input-150" value="返回修改">
                </div>
            </div>
        </form>
    </div>
</div>
<div id="template_div" class="table-responsive">
    <div class="theme_poptit">
        <a id="ls_template_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
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
        <div id="template_page_div" class="customer-pagination">
            <ul id="template_pagination" class="pagination"></ul>
        </div>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sms/adhoc_message_management.js"></script>
