<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-lg-8 form-inline">
            <select  id="keyType" class="form-control text-input-150">
                <option value="1">手机号</option>
                <option value="2">车牌号</option>
            </select>
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder=""/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
        </div>
    </div>
</div>
<div id="count_div" class="detail-together">
    <label>总记录数：</label>
    <span id="totalCount" class="detail-all"></span>个
</div>
<div id="show_div" class="table-responsive">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="user_list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">用户ID</th>
                <th class="text-center">手机号</th>
                <th class="text-center">三方绑定</th>
                <th class="text-center">注册时间</th>
                <th class="text-center">注册渠道</th>
                <th class="text-center">注册IP</th>
                <th class="text-center">最后登录</th>
                <th class="text-center">车辆信息</th>
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
<div id="detail_content" class="none">
    <jsp:include page="../auto/auto_detail.jsp"/>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/user/user_management.js"></script>

