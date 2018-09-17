<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%
    response.setHeader("Cache-Control","no-cache");
    response.setHeader("Pragma","no-cache");
    response.setDateHeader ("Expires", 0);
    response.flushBuffer();
%>
<head lang="en">
    <title>ToC渠道合作详情页</title>
    <link rel="stylesheet" href="../../libs/bootstrap-3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="../../css/style.css">
    <link type="text/css" href="../../libs/bootstrap-3.3.4/css/bootstrap-switch.min.css" rel="stylesheet">
    <link rel="stylesheet" href="../../libs/layer/theme/default/layer.css">
    <style type="text/css">
        div {
            background-color: white;
        }

        .btn_line {
            border-bottom: 2px solid #ccc;
            margin-bottom: 10px;
        }

        .btn_blue_line {
            border-bottom: 20px solid #F0F2F5;
        }

        .btn_blue_below {
            border-bottom: 50px solid #F0F2F5;
        }

        .bgcolor_blue {
            background-color: #F0F2F5;
        }

        .row {
            padding-bottom: 10px;
        }

        .container_add {
            padding-top: 10px;
        }

        .sub_title {
            padding-top: 5px;
            padding-bottom: 15px;
            font-size: 20px;
            font-weight: bold;
        }

        .content_middle {
            width: 1170px;
            margin: auto;
        }
    </style>

</head>
<body>
<div class="btn_blue_line">
    <input type="hidden" id="id" name="id" value="">
    <div class="title btn_line container">
        <h2 class="text-center">第三方渠道合作详情</h2>
    </div>
    <div class="container">
        <div class="row tips">
            <div class="col-sm-6">
                <div class="row">
                    <div class="col-sm-12">
                        <span class="sub_title" id="channelName"></span>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-6">合作商：<span id="partnerName" name="compName"></span></div>
                    <div class="col-sm-6">第三方渠道英文简称：<span id="channelCode" name="nickName"></span></div>
                </div>
                <div class="row">
                    <div class="col-sm-6">创建时间：<span id="createdTime" name="ownerMobile"></span></div>
                    <div class="col-sm-6">对接车车：<span id="platform" name="platform"></span></div>
                </div>
            </div>

            <div class="col-sm-6">
                <div class="row">
                    <div class="col-sm-12 pull-right">
                        <div class="pull-right">
                            <a class="btn btn-default" id="downloadConfig"  onclick="channelDetail.downloadUrl()">下载配置参数</a>
                            <button id="updateChannel" class="btn btn-danger">修改配置</button>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-12 pull-right">
                        <div class="pull-right">
                            <div class="switch">
                                <input type="checkbox" id="disabledChannel" name="disabledChannel">
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>



        <div class="nav-tabs-row">
            <ul class="nav nav-tabs" id="myTab">
                <li class="active"><a href="#config_info">合作配置内容</a></li>
                <!-- <li><a href="#operate_log">配置变更操作日志</a></li> -->
            </ul>
        </div>
    </div>
</div>

<div class="btn_blue_below bgcolor_blue">
    <!-- 合作配置内容start -->
    <div id="config_info" class="content_middle">
        <!-- 通用功能配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">通用功能配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">预约功能：<span id="reserve" name="reserve"></span></div>
                <div class="col-sm-6">图片上传功能：<span id="supportPhoto" name="supportPhoto"></span></div>
            </div>
            <div class="row">
                <div class="col-sm-6">在线客服功能：<span id="showCustomService" name="showCustomService"></span></div>
                <div class="col-sm-6">客服电话：<span id="serviceTel" name="serviceTel"></span></div>
            </div>
        </div>
        <!-- 落地页&报价方式配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">落地页&报价方式配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">落地页：<span id="home" name="home"></span></div>
                <!-- <div class="col-sm-6">报价方式：<span id="quoteWay" name="nickName"></span></div> -->
            </div>
        </div>
        <!-- 首页配置 -->
        <div class="container container_add btn_blue_line" id="homePage">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">首页配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">吸底按钮：<span id="homeFixBottom" name="homeFixBottom"></span></div>
                <div class="col-sm-6">合作伙伴：<span id="showPartner" name="showPartner"></span></div>
            </div>
        </div>
        <!-- 基本首页配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">基本信息页配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">手机输入框：<span id="baseLogin" name="baseLogin"></span></div>
                <div class="col-sm-6">拍照报价/人工客服报价：<span id="baseCustomAndPhoto" name="baseCustomAndPhoto"></span></div>
            </div>
            <div class="row">
                <div class="col-sm-6">顶部banner：<span id="baseBanner" name="baseBanner"></span></div>
                <div class="col-sm-6">订单中心/个人中心：<span id="baseOrder" name="baseOrder"></span></div>
            </div>
            <div class="row">
                <div class="col-sm-6">个人中心是否展示钱包：<span id="cheWallet" name="cheWallet"></span></div>
                <div class="col-sm-6">钱包提现时验证方式：<span id="verifyWay" name="nickName"></span></div>
            </div>
        </div>
        <!-- 提交订单配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">提交订单配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">优惠券模块：<span id="orderGift" name="orderGift"></span></div>
                <div class="col-sm-6">投保车辆信息展示附加页：<span id="orderInsuredCar" name="orderInsuredCar"></span></div>
            </div>
        </div>
        <!-- 支付完成页配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">支付完成页配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">"查看订单"按钮：<span id="successOrder" name="successOrder"></span></div>
                <div class="col-sm-6">"返回首页"按钮对应的页面是否为车车订单页：<span id="isHomePage" name="isHomePage"></span></div>
            </div>
            <div class="row">
                <div class="col-sm-6" id="homeAddress">首页链接地址：<span id="homeUrl" name="homeUrl"></span></div>
            </div>
        </div>
        <!-- 参数配置 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">参数配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">Google统计位：<span id="googleTrackId" name="googleTrackId"></span></div>
                <div class="col-sm-6">页面底色色值：<span id="pageDownTone" name="nickName"></span></div>
            </div>
        </div>
        <!-- 后台配置todo -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">后台配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">出单中心是否可下单：<span id="hasOrderCenter" name="hasOrderCenter"></span></div>
                <div class="col-sm-6">数据是否进电销：<span id="isTelemarketing" name="compName"></span></div>
            </div>
            <div class="row">
                <div class="col-sm-6">是否支持增补：<span id="isSupplement" name="supplement"></span></div>
                <div class="col-sm-6">上传证件：<img id="logoImage" class="image" style="width:100px;height:100px;" alt="logo图片" ></div>
            </div>
        </div>
        <!-- 订单相关配置 -->
        <div class="container container_add btn_blue_line" id="orderConf">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">订单相关配置</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">是否支持订单同步：<span id="isSync" name="needSyncOrder"></span></div>
                <div class="col-sm-6 isSyncHide">第三方提供的同步订单的地址：<span id="syncSite" name="syncOrderUrl"></span></div>
            </div>
        </div>

        <!-- 提供给第三方的配置项 -->
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">提供给第三方的配置项</span>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">签名方法：<span id="sign" name="compName">HMAC-SHA1</span></div>
            </div>
        </div>

    </div>
    <!-- 合作配置内容end -->

    <!-- 配置变更操作日志start -->
    <div class="content_middle none" id="operate_log">
        <div class="container container_add btn_blue_line">
            <div class="row tips btn_line">
                <div class="col-sm-12">
                    <span class="sub_title">操作日志</span>
                </div>
            </div>
            <div class="row">
                <table class="table table-bordered table-hover" id="log_list"></table>
            </div>
        </div>
    </div>
    <!-- 配置变更操作日志end -->
</div>
<jsp:include page="../popup.jsp"/>

<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-1.11.2/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-form/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript"
        src="<%=request.getContextPath()%>/libs/uploadify-3.2.1/js/jquery.uploadify-3.2.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-validation-1.14.0/jquery.validate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-cookie-1.4.1/jquery.cookie.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/thirdParty/toC_details.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap-switch.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/layer/layer.js"></script>

<script>
    (function ($, undefined) {
        var id = common.getUrlParam("id")
        $('#disabledChannel').bootstrapSwitch({
            onText: '已生效',
            offText: '已失效',
            size: 'small',
            onSwitchChange: function (even, status) {
                var that = this
                var mess = '确认禁用此第三方渠道合作内容吗？'
                if (status) mess = '确认启用此第三方渠道合作内容吗？'
                layer.confirm(mess, {closeBtn: 0}, function (index) {
                    // TODO 更改失效状态
                    $.get('/operationcenter/thirdParty/tocCooperate/chgAble', {
                        id: id,
                        status: status ? true : false
                    })
                    layer.close(index)
                }, function () {
                    $(that).bootstrapSwitch('state', !status, true)
                })
            }
        })

    }(window.jQuery))
</script>
</body>
</html>
