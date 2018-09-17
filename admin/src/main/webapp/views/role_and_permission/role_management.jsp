<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .text-input-152 {
            width: 152px !important;
        }
        .permission-div {
            height: 63%;
            overflow-y:auto;
            padding: 10px;
        }
        .model-span {
            margin-bottom: 15px;
        }
        .model-span .span-select {
            padding: 0 21px;
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
            <span class="btn-group">
                  <button id="toNew" class="btn btn-danger" type="button">新建角色</button>
            </span>
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
                <th class="text-center">序号</th>
                <th class="text-center">角色名称</th>
                <th class="text-center">角色分组</th>
                <th class="text-center">权限范围</th>
                <th class="text-center">备注</th>
                <th class="text-center">状态</th>
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
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center title">新建角色</h4>
    </div>
    <div class="form-input-top">
        <form id="new_form" class="form-input form-horizontal" style="padding-left: 26px;">
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">角色名称：</span>
                <div class="col-sm-9 text-left">
                    <input id="name" name="name" type="text" class="form-control text-input-280 text-height-28" placeholder="请输入角色，最多十字" maxlength="10">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">角色分组：</span>
                <div class="col-sm-9 text-left">
                    <label class="radio-inline">
                        <input type="radio" id="roleType_1" name="roleType" value="INTERNAL_USER" checked> 内部角色
                    </label>
                    <label class="radio-inline">
                        <input type="radio" id="roleType_2" name="roleType" value="EXTERNAL_USER"> 外部角色
                    </label>
                </div>
            </div>
            <div class="form-group" id="roleLevelDiv">
                <span class="col-sm-3 text-height-28 text-right">角色类型：</span>
                <div class="col-sm-9 text-left">
                    <label class="radio-inline">
                        <input type="radio" id="level_0" name="level" value="0" checked> 普通角色
                    </label>
                    <label class="radio-inline">
                        <input type="radio" id="level_1" name="level" value="1"> 特殊角色
                    </label>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">权限范围：</span>
                <div class="col-sm-9 text-left">
                    <button type="button" id="role_permission_btn" class="btn btn-warning">查看权限范围详情</button>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">备注：</span>
                <div class="col-sm-9 text-left">
                    <textarea id="description" name="description" class="form-control text-input-280" rows="5" style="resize: none;" placeholder="请输入备注，最多二百字" maxlength="200"></textarea>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-3"></span>
                <div class="col-sm-9 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i class="glyphicon glyphicon-remove-sign"></i><span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div>
                <input type="hidden" id="permissions" name="permissions" value="">
                <input type="hidden" id="roleId" name="id" value="">
            </div>
            <div class="form-group btn-finish">
                <span class="col-sm-3 text-height-28 text-right"></span>
                <div class="col-sm-9 text-left">
                    <input type="button" class="btn btn-danger text-input-200 toCreate" value="保存">
                </div>
            </div>
        </form>
    </div>
</div>
<div id="role_permission_content" class="none">
    <div class="theme_poptit">
        <a href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center title">权限范围详情</h4>
    </div>
    <div class="form-input-top model-span">
        <span class="span-select text-height-28 text-right form-inline model-span">
            一级模块：
            <select id="level_1_select" class="form-control text-input-152 text-height-28 select-28">
            </select>
        </span>
        <span class="span-select text-height-28 text-right form-inline">
            二级模块：
            <select id="level_2_select" class="form-control text-input-152 text-height-28 select-28">
            </select>
        </span>
        <span class="span-select text-height-28 text-right form-inline">
            三级模块：
            <select id="level_3_select" class="form-control text-input-152 text-height-28 select-28">
            </select>
        </span>
    </div>
    <div class="form-input-top model-span none" style="margin-bottom: 0;padding-top: 0;" id="showSpecialPermissionBtnDiv">
        <span class="span-select text-height-28 text-right form-inline model-span">
            <a href="javascript:;" id="showSpecialPermissionBtn">查看所有特殊权限</a>
        </span>

    </div>
    <div class="permission-div">
        <table class="table table-bordered table-hover" id="permission_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">权限ID</th>
                <th class="text-center">一级模块</th>
                <th class="text-center">二级模块</th>
                <th class="text-center">三级模块</th>
                <th class="text-center">权限名称</th>
                <th class="text-center">选择</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <div class="form-group error-line">
        <span class="col-sm-4"></span>
        <div class="col-sm-8 text-left none">
            <p class="alert alert-danger text-input-280 error-msg"><i class="glyphicon glyphicon-remove-sign"></i> <span id="_errorText">错误提示</span></p>
        </div>
    </div>

    <div class="form-group btn-finish">
        <span class="col-sm-5 text-height-28 text-right"></span>
        <div class="col-sm-7 text-left">
            <input type="button" class="btn btn-danger text-input-100 toCreate" value="保存">
        </div>
    </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/role_and_permission/role_management.js"></script>
