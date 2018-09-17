<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .new {
            padding-top: 15px;
        }
        .form-horizontal .form-group {
            margin-right: 0 !important;
        }
    </style>
</head>

<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-4 form-inline">
            <select id="searchSel" class="form-control text-input-150">
                <option>手机号</option>
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
        <table class="table table-bordered table-hover" id="list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">用户ID</th>
                <th class="text-center">用户手机号</th>
                <th class="text-center">邀请码</th>
                <th class="text-center">创建邀请码时间</th>
                <th class="text-center">总邀请人数</th>
                <th class="text-center">邀请用户</th>
                <th class="text-center">收货信息</th>
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

<%--邀请用户列表--%>
<div id="invited_list_div" class="table-responsive none">
    <div class="theme_poptit">
        <a id="detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="detail_title" class="text-center">邀请用户列表</h4>
    </div>
    <div>
        <div class="col-sm-12">
            <table class="table table-bordered table-hover" id="invited_list_tab">
                <thead>
                <tr class="active">
                    <th class="text-center">序号</th>
                    <th class="text-center">注册时间</th>
                    <th class="text-center">使用邀请码时间</th>
                    <th class="text-center">被邀请用户ID</th>
                    <th class="text-center">被邀请用户手机号</th>
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
</div>

<%--收货信息详情--%>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="partner_title" class="text-center">收货信息详情</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height">

                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">姓名：</span>
                    <div class="col-sm-8 text-left">
                        <span id="userName" type="text" name="userName" class="text-height-28 text-input-200" ></span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">电话：</span>
                    <div class="col-sm-8 text-left">
                        <span id="phoneNo" type="text" name="phoneNo" class="text-height-28 text-input-200" ></span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">地址：</span>
                    <div class="col-sm-8 text-left">
                        <span id="address" type="text" name="address" class="text-height-28 text-input-200" ></span>
                    </div>
                </div>
            </div>
            <div>
                <input type="hidden" id="inviteCodeId" name="id" value="0">
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/invitecode/invitecode_management.js"></script>
