<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>车车管理系统</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="Robots" content="none">
    <link href="<%=request.getContextPath()%>/css/layout.css" rel="stylesheet" media="screen">
</head>
<body>
    <div class="layout_div">
        <div id="head-nav" class="navbar navbar-inverse navbar-fixed-top">
            <div class="container-fluid">
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown user-dropdown">
                        <a id="user_dropdown" href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-user"></i>&nbsp; <span id="login_user_name"></span>&nbsp; <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="javascript:;" id="modify_password"><i class="glyphicon glyphicon-briefcase"></i> 修改密码</a></li>
                            <li role="separator" class="divider"></li>
                            <li><a href="javascript:;" onclick="index.logout();"><i class="glyphicon glyphicon-off"></i> 退出系统</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div class="position-div">
            <div class="row">
                <div class="col-sm-12">
                    <ol class="breadcrumb none">
                        当前所在位置：
                        <li id="firText"></li>
                        <li id="secText"></li>
                        <li id="thirdText"></li>
                    </ol>
                </div>
            </div>
        </div>
        <div class="left_div">
            <nav class="side_nav navbar navbar-fixed-top navbar-inverse" role="navigation">
                <div class="navbar-header">
                    <a class="navbar-brand" href="http://www.cheche365.com" target="_blank" style="padding: 15px 100px;"></a>
                </div>
                <div class="collapse navbar-collapse" id="nav-top" style="padding-top: 51px;">
                    <ul class="nav navbar-nav" id="accordion" data-toggle="buttons">
                        <li id="users_menu" class="panel"  style="display: none;" >
                            <a href="#users_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-user"></i> &nbsp;<span>用户管理</span><b class="caret"></b></a>
                            <ul id="users_ul" class="collapse list-unstyled sednav">
                                <li id="users_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/user/user_management.jsp"><i class="glyphicon glyphicon-user"></i>&nbsp;<span>用户管理</span></a></li>
                            </ul>
                        </li>
                        <li id="cars_menu" class="panel" style="display: none;">
                            <a href="#cars_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-cloud"></i> &nbsp;<span>车辆管理</span><b class="caret"></b></a>
                            <ul id="cars_ul" class="collapse list-unstyled sednav">
                                <li id="cars_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/auto/auto_management.jsp"><i class="glyphicon glyphicon-cloud"></i>&nbsp;<span>车辆管理</span></a></li>
                            </ul>
                        </li>
                        <li id="accounts_menu" class="panel" style="display: none;">
                            <a href="#account_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-heart"></i> &nbsp;<span>系统账号</span><b class="caret"></b></a>
                            <ul id="account_ul" class="collapse list-unstyled sednav">
                                <li id="in_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/account/inner_account_management.jsp"><i class="glyphicon glyphicon-leaf"></i>&nbsp;<span>内部账号</span></a></li>
                                <li id="out_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/account/outer_account_management.jsp"><i class="glyphicon glyphicon-fire"></i>&nbsp;<span>外部账号</span></a></li>
                            </ul>
                        </li>
                        <li id="roles_menu" class="panel" style="display: none;">
                            <a href="#role_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-cog"></i> &nbsp;<span>角色权限管理</span><b class="caret"></b></a>
                            <ul id="role_ul" class="collapse list-unstyled sednav">
                                <li id="role_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/role_and_permission/role_management.jsp"><i class="glyphicon glyphicon-star"></i>&nbsp;<span>角色属性</span></a></li>
                                <li id="permission_li" class="menu_li" style="display: none;"><a href="<%=request.getContextPath()%>/views/role_and_permission/permission_management.jsp"><i class="glyphicon glyphicon-star-empty"></i>&nbsp;<span>权限条目</span></a></li>
                            </ul>
                        </li>
                        <li id="task_menu" style="display:none;" class="panel">
                            <a href="#task_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-bookmark"></i> &nbsp;<span>定时任务管理</span><b class="caret"></b></a>
                            <ul id="task_ul" class="collapse list-unstyled sednav">
                                <li id="task_job" style=""><a href="<%=request.getContextPath()%>/views/task/task_job.jsp" ><i class="glyphicon glyphicon-time"></i>&nbsp;<span>定时任务管理</span></a></li>
                                <li>
                                    <a href="#task_tel_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#channel"><i class="glyphicon glyphicon-phone-alt"></i>&nbsp;<span>电销中心任务</span><b class="caret third-caret"></b></a>
                                    <ul id="task_tel_ul" class="collapse list-unstyled thirdnav">
                                        <li id="task_tel_li"><a href="<%=request.getContextPath()%>/views/task/task_import_marketing_success_data.jsp"><i class="glyphicon glyphicon-cloud-upload"></i>&nbsp;<span>活动数据导入</span></a></li>
                                    </ul>
                                </li>
                                <li id="exclude_channel_setting" style=""><a href="<%=request.getContextPath()%>/views/task/exclude_channel_setting.jsp" ><i class="glyphicon glyphicon-asterisk"></i>&nbsp;<span>过滤渠道配置</span></a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
        <div class="span10 right_div"></div>
    </div>
    <input type="hidden" id="internalUserId" name="internalUserId" value="">

    <jsp:include page="popup.jsp"/>

    <jsp:include page="resource.jsp"/>
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/navbar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/index.js"></script>
</html>
