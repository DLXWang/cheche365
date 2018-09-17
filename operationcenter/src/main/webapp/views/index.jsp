<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>车车运营中心</title>
    <meta content="车车运营，有我陪伴" name="description"/>
    <meta name="Robots" content="none">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

    <jsp:include page="resource_css.jsp"/>
</head>
<body>
<div class="layout_div">
    <div id="head-nav" class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown user-dropdown">
                    <a id="user_dropdown" class="dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-user"></i>&nbsp;
                        <span id="login_user_name"></span>&nbsp; <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:;" id="modify_password"><i class="glyphicon glyphicon-star"></i>
                            修改密码</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="javascript:;" onclick="init.logout();"><i class="glyphicon glyphicon-off"></i> 退出系统</a>
                        </li>
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
            <div id="nav-top" class="collapse navbar-collapse old-left-nav" style="padding-top: 51px;">
                <ul class="nav navbar-nav" id="accordion" data-toggle="buttons">
                    <li id="invitecode_menu" style="display:none;" class="panel">
                        <a href="#invitecode_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-th"></i>
                            &nbsp;<span>用户邀请码</span><b class="caret"></b></a>
                        <ul id="invitecode_ul" class="collapse list-unstyled sednav">
                            <li id="invitecode_m" style="display:block;">
                                <a href="<%=request.getContextPath()%>/views/invitecode/invitecode_management.jsp"><i class="glyphicon glyphicon-edit"></i>&nbsp;<span>用户邀请码列表页</span></a>
                            </li>
                            <li id="invitecode_m2" style="display:block;">
                                <a href="<%=request.getContextPath()%>/views/invitecode/prize_distribution.jsp"><i class="glyphicon glyphicon-edit"></i>&nbsp;<span>奖品发放中心页</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="gde_menu" style="display:none;" class="panel">
                        <a href="#gde_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-th"></i>
                            &nbsp;<span>地推用户信息</span><b class="caret"></b></a>
                        <ul id="gde_ul" class="collapse list-unstyled sednav">
                            <li id="gde_m" style="display:block;">
                                <a href="<%=request.getContextPath()%>/views/appointmentinsurance/appointmentInsurance_management.jsp"><i class="glyphicon glyphicon-edit"></i>&nbsp;<span>地推用户信息</span></a>
                            </li>
                        </ul>
                    </li>
                    <!-- <li id="red_menu" style="display:none;" class="panel">
                        <a href="#red_ul" class="dropdown-toggle"  data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-usd"></i> &nbsp;<span>红包发送审核</span><b class="caret"></b></a>
                        <ul id="red_ul" class="collapse list-unstyled sednav">
                            <li id="red_m" style="display:none;"><a href="<%=request.getContextPath()%>/views/red/red_management.jsp"><i class="glyphicon glyphicon-usd"></i>&nbsp;<span>红包发送审核</span></a></li>
                        </ul>
                    </li> -->
                    <li id="partner_menu" style="display:none;" class="panel">
                        <a href="#partner_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-magnet"></i>
                            &nbsp;<span>合作商管理</span><b class="caret"></b></a>
                        <ul id="partner_ul" class="collapse list-unstyled sednav">
                            <li id="partner_m" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/partner/partner_management.jsp"><i class="glyphicon glyphicon-king"></i>&nbsp;<span>合作商管理</span></a>
                            </li>
                            <li id="activity_m" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/partner/activity_management.jsp"><i class="glyphicon glyphicon-leaf"></i>&nbsp;<span>商务活动管理</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="sms_menu" style="display:none;" class="panel">
                        <a href="#sms_ul" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-phone"></i>
                            &nbsp;<span>短信中心</span><b class="caret"></b></a>
                        <ul id="sms_ul" class="collapse list-unstyled sednav">
                            <li id="sms_template_m" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/sms/sms_template_management.jsp"><i class="glyphicon glyphicon-envelope"></i>&nbsp;<span>短信模板管理</span></a>
                            </li>
                            <li id="filter_user_m" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/sms/filter_user_management.jsp"><i class="glyphicon glyphicon-filter"></i>&nbsp;<span>筛选用户管理</span></a>
                            </li>
                            <li id="schedule_sms" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/sms/schedule_message_management.jsp"><i class="glyphicon glyphicon-bell"></i>&nbsp;<span>条件触发短信</span></a>
                            </li>
                            <li id="conditions_log_sms" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/sms/schedule_message_log.jsp"><i class="glyphicon glyphicon-cloud"></i>&nbsp;<span>条件触发短信日志</span></a>
                            </li>
                            <li id="adhoc_sms" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/sms/adhoc_message_management.jsp"><i class="glyphicon glyphicon-fullscreen"></i>&nbsp;<span>主动发送短信</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="wechat_menu" style="display:none;" class="panel">
                        <a href="#wechat_back" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-bookmark"></i>
                            &nbsp;<span>微信后台管理</span><b class="caret"></b></a>
                        <ul id="wechat_back" class="collapse list-unstyled sednav">
                            <li>
                                <a href="#wechat_back_channel" class="dropdown-toggle" data-toggle="collapse" data-parent="#channel"><i class="glyphicon glyphicon-book"></i>&nbsp;<span>二维码渠道管理</span><b class="caret third-caret"></b></a>
                                <ul id="wechat_back_channel" class="collapse list-unstyled thirdnav">
                                    <li id="temp_qrcode_m" style="display:none;">
                                        <a href="wechat/channel/channel_temp_qrcode.jsp" target="some"><i class="glyphicon glyphicon-qrcode"></i>&nbsp;<span>临时二维码</span></a>
                                    </li>
                                    <li id="forever_qrcode_m" style="display:none;">
                                        <a id="foreverQRCode" href="wechat/channel/channel_forever_qrcode.jsp" target="some"><i class="glyphicon glyphicon-qrcode"></i>&nbsp;<span>永久二维码</span></a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li id="activity_menu" style="display:none;" class="panel">
                        <a href="#activity" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>活动</span><b class="caret"></b></a>
                        <ul id="activity" class="collapse list-unstyled sednav">
                            <li id="activity_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/marketing_rule/marketing_list.jsp" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>活动列表</span></a>
                            </li>
                            <li id="gift_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/marketing_rule/gift_list.html" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>礼物管理</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="import_menu" style="display:none;" class="panel">
                        <a href="#import" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>产品导入</span><b class="caret"></b></a>
                        <ul id="import" class="collapse list-unstyled sednav">
                            <li id="import_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/insureProduct/insureProduct.jsp" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>阿宝在线产品导入</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="data_statistics_menu" style="display:none;" class="panel">
                        <a href="#data_search" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-align-left"></i>
                            &nbsp;<span>运营活动管理</span><b class="caret"></b></a>
                        <ul id="data_search" class="collapse list-unstyled sednav">
                            <li id="data_search_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/dataStatistics/dataSearch.html" target="some"><i class="glyphicon glyphicon-align-right"></i>&nbsp;<span>数据查询</span></a>
                            </li>
                            <li id="create_url_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/dataStatistics/createUrl.html" target="some"><i class="gglyphicon glyphicon-link"></i>&nbsp;<span>生成链接</span></a>
                            </li>
                            <li id="SEOTrace_li" style="display:none;">
                                <a href="<%=request.getContextPath()%>/views/seoTrace/seoTrace.html" target="some"><i class="glyphicon glyphicon-align-right"></i>&nbsp;<span>SEO跟踪</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="channel_rebate_menu" style="display: none;" class="panel">
                        <a href="#channel_rebate" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>渠道配置</span><b class="caret"></b></a>
                        <ul id="channel_rebate" class="collapse list-unstyled sednav">
                            <li id="channel_rebate_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/channelRebate/channel_rebate_list.html" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>渠道费率配置</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="quote_offline_menu" style="display: none;" class="panel">
                        <a href="#quote_offline_rebate" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>报价配置</span><b class="caret"></b></a>
                        <ul id="quote_offline_rebate" class="collapse list-unstyled sednav">
                            <li id="quote_offline_rebate_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/quoteOffline/quote_flow_config.html" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>报价配置</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="user_manager_menu" style="display: none;" class="panel">
                        <a href="#user_manager" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>用户管理</span><b class="caret"></b></a>
                        <ul id="user_manager" class="collapse list-unstyled sednav">
                            <li id="user_manager_info_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/userManager/user_info_manager_list.html" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>用户信息管理</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="third_party_menu" style="display: none;" class="panel">
                        <a href="#third_party" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion"><i class="glyphicon glyphicon-thumbs-up"></i>
                            &nbsp;<span>第三方合作</span><b class="caret"></b></a>
                        <ul id="third_party" class="collapse list-unstyled sednav">
                            <li id="official_partner_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/thirdParty/official_partner_list.jsp" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>合作商管理</span></a>
                            </li>
                            <li id="toc_official_partner_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/thirdParty/toC_manager_list.jsp" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>ToC合作管理</span></a>
                            </li>
                            <li id="toa_official_partner_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/thirdParty/toA_manager_list.html" target="some"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>ToA合作管理</span></a>
                            </li>
                        </ul>
                    </li>
                    <li id="rebate_manage_menu" style="display: none;" class="panel">
                        <a href="#rebate_manage" class="dropdown-toggle" data-toggle="collapse" data-parent="#accordion">
                            <i class="glyphicon glyphicon-thumbs-up"></i> &nbsp;<span>点位管理</span><b class="caret"></b>
                        </a>
                        <ul id="rebate_manage" class="collapse list-unstyled sednav">
                            <li id="institution_contract_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/tide/tide_contract_list.html" target="some">
                                    <i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>合约管理</span>
                                </a>
                            </li>
                            <li id="institution_rebate_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/tide/tide_rebate_list.html" target="some">
                                    <i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>原始点位管理</span>
                                </a>
                            </li>
                            <li id="institution_rebate_history_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/tide/tide_rebate_history.html" target="some">
                                    <i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>点位查询</span>
                                </a>
                            </li>
                            <li id="institution_rebate_draft_li" style="display: none;">
                                <a href="<%=request.getContextPath()%>/views/tide/tide_rebate_draft.html" target="some">
                                    <i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>草稿箱</span>
                                </a>
                            </li>
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
<jsp:include page="resource_script.jsp"/>
</body>
</html>
