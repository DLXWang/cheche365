<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <style type="text/css">
        .role-div {
            height: 70%;
            overflow-y:auto;
            padding: 10px;
        }
    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-lg-8 form-inline">
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="名称"/>
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
        <table class="table table-bordered table-hover" id="permission_list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">权限ID</th>
                <th class="text-center">一级模块</th>
                <th class="text-center">二级模块</th>
                <th class="text-center">三级模块</th>
                <th class="text-center">权限名称</th>
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
<div id="allot_permission" class="none" style="height:90%; overflow-x: auto; overflow-y: hidden;">
    <div class="theme_poptit">
        <a id="detail_perssion_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center" id="allot_id"></h4>
    </div>
    <div class="role-div">
        <table class="table table-bordered table-hover" id="allot_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">角色分组</th>
                <th class="text-center">角色名称</th>
                <th class="text-center">选择</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <div class="form-group btn-finish">
        <div class="col-sm-12 text-center">
            <input id="allot_save" type="submit" class="btn btn-danger" value="保存">
        </div>
    </div>
    <div class="form-group" style="height:40px">
    </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/role_and_permission/permission_management.js"></script>
