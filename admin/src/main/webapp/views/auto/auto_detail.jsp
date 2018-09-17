<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .form-input .bold-font {
            font-weight: bold;
            font-size: 17px;
            padding: 0 20px;
        }
        .form-group {
            margin-bottom: 5px !important;
            margin-top: 0;
        }
    </style>
</head>
<div class="theme_poptit">
    <a id="auto_detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
    <h4 class="text-center" id="detail_title">车辆信息详情</h4>
</div>
<div class="form-input-top">
    <form class="form-horizontal form-input" style="padding: 0 20px;">
        <div class="form-group">
            <div class="col-sm-12 text-center">
                ————————————<span class="text-center bold-font">车主信息</span>————————————
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">车主姓名：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_owner"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">车主身份证：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left" id="detail_identity"></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-12 text-center">
                ————————————<span class="text-center bold-font">车辆信息</span>————————————
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">车牌号：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_licensePlateNo"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">车架号：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_vinNo"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">发动机号：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_engineNo"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">初登日期：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_enroll_date"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">保险到期日：</div>
            <div class="col-sm-4 text-height-28">
                <p class="text-left"><span id="detail_expire_date"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">品牌型号：</div>
            <div class="col-sm-9 text-height-28">
                <p class="text-left"><span id="detail_brand_code"></span></p>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-3 text-height-28 text-right">车型：</div>
            <div class="col-sm-9 text-height-28">
                <p class="text-left"><span id="detail_model"></span></p>
            </div>
        </div>
        <div class="form-group" style="float:right;padding-right: 30px;margin-top: -230px;">
            <img id="auto_img" src="" style="width: 150px;height: 150px;">
        </div>
        <div class="form-group">
            <div class="col-sm-12 text-center">
                ————————————<span class="text-center bold-font">用户信息</span>————————————
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-5 text-height-28 text-center">用户ID</div>
            <div class="col-sm-5 text-height-28 text-center">电话</div>
        </div>
        <div id="auto_user_info" class="form-group" style="height: 12%;overflow-y:auto;">
        </div>
    </form>
</div>
