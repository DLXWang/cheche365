<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head >
    <meta charset="UTF-8">
</head>

<form id="refresh_form" method="post">
    <div id="top_div" class="top-search">
        <div class="row">
            <div class="col-sm-12 form-inline">
                <span>合作商：</span>
                <select id="partner" class="form-control text-input-150"
                        style="margin-left:50px;margin-right:300px;width: 200px">
                    <option value="">请选择</option>
                </select>
                <span>第三方渠道名称：</span>
                <select id="channelName" class="form-control text-input-150"
                        style="margin-left:15px;margin-right:15px;width: 200px">
                    <option value="">请选择</option>
                </select>
                <p></p>
                <span>渠道英文名称：</span>
                <select id="channelEngSel" class="form-control text-input-150"
                        style="margin-left:7px;margin-right:357px;width: 200px">
                    <option value="">请选择</option>
                </select>
                <p></p>
                <span>报价方式：</span>
                <select id="quoteWay" class="form-control text-input-150"
                        style="margin-left:36px;margin-right:370px;width: 200px">
                    <option value="">请选择</option>
                    <option value="1">比价</option>
                    <option value="2">直投</option>
</select>
<span>状态：</span>
<select id="status" class="form-control text-input-150"
        style="margin-left:15px;margin-right:15px;width: 200px">
    <option value="">请选择</option>
    <option value="1">上线</option>
    <option value="0">下线</option>
</select>
<button id="searchBtn" class="btn btn-danger" type="button" style="margin-left:15px;margin-right:15px;">查询</button>
<button id="cancelBtn" class="btn btn-danger" type="button" style="margin-left: 20px;">重置</button>
<p></p>
                <button id="create_btn" class="btn btn-danger" type="button" style="margin-left: 5px;margin-top: 20px;"
                        onclick="window.open('/views/thirdParty/toC_manager_add.html')"> + 新建</button>
</div>
</div>
</div>

<div class="col-sm-12">
    <table class="table table-bordered table-hover" id="partner_list"></table>
</div>
</form>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/thirdParty/toC_manager_list.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.select.js"></script>

