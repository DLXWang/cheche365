<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <style type="text/css">
        .setting-div {
            height: 63%;
            overflow-y:auto;
            padding: 10px;
        }
    </style>
</head>
<body>
<div id="setting_count_div" class="detail-together">
    <label>总记录数：</label>
    <span id="settingTotalCount" class="detail-all"></span>个
</div>
<div id="show_div" class="table-responsive">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="setting_list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">电销定时任务名称</th>
                <th class="text-center">创建时间</th>
                <th class="text-center">修改时间</th>
                <th class="text-center">操作人</th>
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
<div id="setting_content" class="none">
    <div class="theme_poptit">
        <a href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center title">过滤渠道详情</h4>
    </div>
    <div class="setting-div" style="height: 75%;">
        <table class="table table-bordered table-hover" id="setting_tab">
            <thead>
                <tr class="active">
                    <th class="text-center">序号</th>
                    <th class="text-center">渠道ID</th>
                    <th class="text-center">渠道说明</th>
                    <th class="text-center">渠道编码</th>
                    <th class="text-center">选择</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>

    <div class="form-group btn-finish">
        <span class="col-sm-5 text-height-28 text-right"></span>
        <div class="col-sm-7 text-left" style="padding-top:10px;">
            <input type="button" class="btn btn-danger text-input-100 toCreate" value="保存">
        </div>
    </div>
</div>
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.grid.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/task/exclude_channel_setting.js"></script>
</html>
