<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="1">模板号</option>
                <option value="4">手机号</option>
            </select>
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                    <span class="input-group-btn">
                        <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                    </span>
            </div>
            <select id="status" class="form-control text-input-150">
                <option value="">请选择发送状态</option>
                <option value="1">发送成功</option>
                <option value="2">发送失败</option>
                <option value="0">发送中</option>
            </select>
        </div>
    </div>
</div>
<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
        </table>
    </div>
</div>

<script type="text/javascript"
        src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sms/schedule_message_log.js"></script>
