<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <span class="btn-group">
                <button id="toNew" class="btn btn-danger">新建任务</button>
            </span>
        </div>
    </div>
</div>
<div id="count_div" class="detail-together">
    <label>总共：</label>
    <span id="totalCount" class="detail-all"></span>条
</div>
<div id="show_div" class="table-responsive">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">活动名称</th>
                <th class="text-center">活动代码</th>
                <th class="text-center">缓存key</th>
                <th class="text-center">电销来源</th>
                <th class="text-center">渠道</th>
                <th class="text-center">状态</th>
                <%--<th class="text-center">优先级</th>--%>
                <th class="text-center">操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
        <div class="customer-pagination">
            <ul class="pagination"></ul>
        </div>
    </div>
</div>
<div style="width: 494px; height: 654px; top: 40%;" id="popover_normal_input" class="theme_popover none">
    <div class="theme_poptit">
        <a id="ls_new_sms_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="conditions_title" class="text-center">新建任务</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <input id="id" name="id" type="hidden">
            <div class="">
                <div class="form-group">
                    <div class="col-sm-8 text-left">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">活动Code：</span>
                    <div class="col-sm-8 text-left">
                        <input type="text" name="markeingCode" id="marketing" class="form-control text-input-200 text-height-30">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">电销来源：</span>
                    <div class="col-sm-8 text-left">
                        <select name="source" id="source" class="form-control text-input-200 text-height-30"><option value="">请选择来源</option></select>
                    </div>
                </div>
                <%--<div class="form-group">--%>
                    <%--<span class="col-sm-4 text-height-28 text-right">来源类型：</span>--%>
                    <%--<div class="col-sm-8 text-left">--%>
                        <%--<select name="sourceType" id="sourceType" class="form-control text-input-200 text-height-30">><option value="">请选择来源类型</option></select>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="form-group">--%>
                    <%--<span class="col-sm-4 text-height-28 text-right">优先级：</span>--%>
                    <%--<div class="col-sm-8 text-left">--%>
                        <%--<input type="text" name="priority" id="priority" class="form-control text-input-200 text-height-30">--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">缓存key：</span>
                    <div class="col-sm-8 text-left">
                        <input type="text" name="cacheKey" id="cacheKey" class="form-control text-input-300 text-height-30">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">渠道：</span>
                    <div class="col-sm-8 text-left">
                        <select name="channel" id="channel" class="form-control text-input-200 text-height-30"><option value="">请选择渠道</option></select>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>
                <div class="col-sm-8 text-left ">
                    <p class="alert alert-danger text-input-280 error-msg none"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
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
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.grid.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/task/task_import_marketing_success_data.js"></script>
</html>
