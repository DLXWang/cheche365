<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <style type="text/css">
        .alert {
            margin: 0 !important;
            padding: 5px !important;
        }
    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-lg-8 form-inline">
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="姓名/邮箱/手机" maxlength="20"/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
            <span class="btn-group">
                  <button id="toNew" class="btn btn-danger" type="button">新建账号</button>
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
        <table class="table table-bordered table-hover" id="account_list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">ID</th>
                <th class="text-center">姓名</th>
                <th class="text-center">角色</th>
                <th class="text-center">邮箱</th>
                <th class="text-center">手机</th>
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
        <a id="new_account_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="addOrEdit" class="text-center"></h4>
    </div>
    <div class="new form-input-top">
        <form id="new_form" class="form-input form-horizontal">
            <input type="hidden" id="id" name="id">
            <input type="hidden" id="internalUserType" name="internalUserType">
            <input type="hidden" id="password" name="password" value="12345678">
            <div id="updateId" class="form-group">
                <span class="col-sm-4 text-height-28 text-right">邮箱：</span>
                <div class="col-sm-8 text-left">
                    <input type="text" id="email" name="email" class="form-control text-input-200 text-height-28">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-4 text-height-28 text-right">账号分组：</span>
                <div class="col-sm-8 text-left">
                    <label class="radio-inline">
                        <input type="radio" value="1" id="internalUserType1" name="inOrOutRole">内部角色
                    </label>
                    <label class="radio-inline">
                        <input type="radio" value="2" id="internalUserType2" name="inOrOutRole">外部角色
                    </label>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-4 text-height-28 text-right">角色：</span>
                <div class="col-sm-8">
                    <select id="roleSel" name="roleIds" class="form-control text-height-28 text-input-150 select-28" multiple="multiple">
                    </select>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-4 text-height-28 text-right">姓名：</span>
                <div class="col-sm-8 text-left">
                    <input id="name" name="name" type="text" class="form-control text-input-200 text-height-28" placeholder="请输入姓名，最多十字" maxlength="10">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-4 text-height-28 text-right">电话：</span>
                <div class="col-sm-8 text-left">
                    <input id="mobile" name="mobile" type="text" class="form-control text-input-200 text-height-28" placeholder="请输入电话" maxlength="20">
                </div>
            </div>
            <div class="form-group notice-p">
                <span class="col-sm-4"></span>
                <div class="col-sm-8 text-left">
                    <p id="initializePwd" class="alert alert-info text-input-200">注意：新建系统账号后初始密码为12345678</p>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>
                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-200 error-msg"><i class="glyphicon glyphicon-remove-sign"></i> <span id="edit_errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <div class="col-sm-12 text-center">
                    <input id="accountSave" type="button" class="btn btn-danger text-input-100" value="保存">
                </div>
            </div>
        </form>
    </div>
</div>

<div id="update_pwd_div" class="none">
    <div class="theme_poptit">
        <a id="update_pwd_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">重置密码</h4>
    </div>
    <div class="new form-input-top">
        <form id="update_pwd" class="form-input form-horizontal">
            <input type="hidden" id="newPwdId" name="id">
            <div class="form-group">
                <span class="col-sm-4 text-height-28 text-center">新密码：</span>
                <div class="col-sm-6 text-left input-append input-group">
                    <input id="newPwd" name="password" type="password" class="form-control text-height-28 text-input-120"  placeholder="" maxlength="12" style='display: inline-block;'>
                    <span id='show-hide-pwd' tabindex='100' title='显示/隐藏密码' class='add-on input-group-addon' style='cursor: pointer;'>
                        <i class='glyphicon icon-eye-open glyphicon-eye-open'></i>
                    </span>
                </div>
            </div>
            <div class='form-group' style='margin-left:50px;height: 52px;'>
                <span style='color: #bcbcbc'>
                    密码规则：<br>
                    1.密码长度6-12位<br>
                    2.须同时包含大写字母、小写字母、数字三种，允许输入下划线<br>
                </span>
            </div>
            <div class="form-group error-line">
                <div class="col-sm-3"></div>
                <div class="col-sm-8 none">
                    <p class="alert alert-danger text-input-300 error-msg"><i class="glyphicon glyphicon-remove-sign"></i> <span id="pwd_errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <div class="col-sm-12 text-center">
                    <input id="pwdSave" type="button" class="btn btn-danger text-input-100" value="保存">
                </div>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/account/account_management.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/account/outer_account_management.js"></script>
