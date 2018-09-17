<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .new {
            padding: 10px 0 0 20px;
        }

        .detail .form-horizontal .form-group {
            margin-top: 5px;
            margin-bottom: 5px;
        }

        .text-input-70 {
            width: 70px !important;
        }

        .text-input-88 {
            width: 88px !important;
        }

        .text-input-830 {
            width: 830px !important;
        }

        .form-horizontal .form-group {
            margin-right: 0 !important;
        }

        .error-line {
            height: 10px;
        }

        .error-msg {
            margin: 0 !important;
            padding: 2px !important;
        }

        .btn-finish {
            padding-top: 10px;
        }

        .selected span {
            color: #FFF;
            font-weight: bold;
        }

        .btn-area-group a {
            width: 80px;
            height: 30px;
        }

        .span-supplement {
            margin-top: -10px;
            margin-bottom: 3px !important;
        }
    </style>
</head>

<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select class="form-control  text-input-150" id="keyType" name="keyType">
                <option value="1" selected>合作商名称</option>
                <option value="2">商务活动名称</option>
            </select>

            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
            <div class="btn-group">
                <a id="toNew" class="btn btn-danger">新建商务活动</a>
            </div>
            <div class="btn-group">
                <a id="exportExcel" class="btn btn-danger">导出数据至EXCEL</a>
            </div>
        </div>
    </div>
</div>

<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="activity_tab">
        </table>
    </div>
</div>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="new_activity_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">新建商务活动</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height">
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">商务活动名称：</span>

                    <div class="col-sm-8 text-left">
                        <input id="name" name="name" type="text" class="form-control text-height-28" maxlength="20"
                               placeholder="请输入商务活动名字，最多二十位">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">商务活动编号：</span>

                    <div class="col-sm-8 text-left">
                        <input id="code" name="code" type="text" class="form-control text-height-28" maxlength="30"
                               placeholder="请输入商务活动编号，最多三十位">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">合作商：</span>

                    <div class="col-sm-2" style="width: 19%;">
                        <select id="partnerSel" name="partner"
                                class="form-control text-height-28 select-28 text-input-150">
                            <option value="">请选择</option>
                        </select>
                    </div>
                    <span class="col-sm-2 text-height-28 text-right" style="width: 14%;">合作方式：</span>

                    <div class="col-sm-2">
                        <select id="cooperationModeSel" name="cooperationMode"
                                class="form-control text-height-28 select-28 text-input-100">
                            <option value="">请选择</option>
                        </select>
                    </div>
                    <span id="rebate_span" class="col-sm-2 text-height-28 text-right none"
                          style="margin-left: 2px;">佣金：</span>

                    <div id="rebate_div" class="col-sm-2 form-inline text-left none">
                        <input id="rebate" name="rebate" type="text"
                               class="form-control text-height-28 text-input-70"><span class="span-text">%</span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">城市：</span>

                    <div class="col-sm-6 areaClass">
                        <select id="areaSel" name="city" class="form-control text-height-28 select-28"
                                multiple="multiple">
                        </select>
                    </div>
                    <span class="col-sm-2 text-height-28 text-right">预算：</span>

                    <div class="col-sm-2 form-inline text-left">
                        <input id="budget" name="budget" type="text"
                               class="form-control text-height-28 text-input-88"><span class="span-text">元</span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">活动周期：</span>
                    <span class="col-sm-1 text-height-28 text-right"><p style="color: green;">起：</p></span>

                    <div class="col-sm-3 text-center">
                        <input type="text" id="startTimeShow" name="startTimeShow" placeholder="请选择开始时间"
                               class="form-control text-height-28 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH：mm',vel:'startTime',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss'});"
                               readonly>
                        <input id="startTime" name="startTime" type="hidden" value="">
                    </div>
                    <span class="col-sm-1 text-height-28 text-right"><p style="color: red;">止：</p></span>

                    <div class="col-sm-3 text-center">
                        <input type="text" id="endTimeShow" name="endTimeShow" placeholder="请选择结束时间"
                               class="form-control text-height-28 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH：mm',vel:'endTime',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss'});"
                               readonly>
                        <input id="endTime" name="endTime" type="hidden" value="">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">联系人：</span>

                    <div class="col-sm-3 text-left">
                        <input id="linkMan" name="linkMan" type="text"
                               class="form-control text-height-28 text-input-200" maxlength="10"
                               placeholder="请输入联系人，最多十位">
                    </div>
                    <span class="col-sm-2 text-height-28 text-right">联系方式：</span>

                    <div class="col-sm-3 text-left">
                        <input id="mobile" name="mobile" type="text" class="form-control text-height-28 text-input-200"
                               maxlength="20" placeholder="请输入联系方式">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">落地页：</span>

                    <div class="col-sm-3 text-left">
                        <select id="landingPageSel" name="landingPageType"
                                class="form-control text-height-28 select-28 text-input-200">
                            <option value="">选择落地页</option>
                            <option value="1">M站首页</option>
                            <option value="2">M站购买页</option>
                            <option value="3">M端活动页</option>
                            <option value="5">PC端首页</option>
                            <option value="4">PC端活动页</option>
                        </select>
                    </div>
                    <div id="marketing_div" class="none">
                        <span class="col-sm-2 text-height-28 text-right">推广活动：</span>

                        <div class="col-sm-3 text-left">
                            <select id="marketingSel" name="objId"
                                    class="form-control text-height-28 select-28 text-input-200">
                                <option value="">选择推广活动</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div id="new_marketing_remark_div" class="form-group span-supplement none">
                    <span class="col-sm-6"></span>
                    <span style="color: red;margin-left: -14px;">推广活动周期：<span id="startTimeRemark"></span> 至 <span
                        id="endTimeRemark"></span></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">落地页URL：</span>

                    <div class="col-sm-8 text-left">
                        <input id="landingPage" name="landingPage" type="text" class="form-control text-height-28"
                               readonly>
                    </div>
                </div>
                <div id="index_config" class="none">
                    <div class="form-group" id="back_display">
                        <span class="col-sm-6 text-height-28 text-right">返回首页：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="display" id="display_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="display" id="display_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">车车顶部品牌（涉及品牌）：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="topBrand" id="top_brand_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="topBrand" id="top_brand_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">我的中心：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="myCenter" id="my_center_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="myCenter" id="my_center_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">顶部轮播图（涉及品牌）：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="topCarousel" id="top_carousel_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="topCarousel" id="top_carousel_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">活动入口：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="activityEntry" id="activity_entry_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="activityEntry" id="activity_entry_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">我们的客户：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="ourCustomer" id="our_customer_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="ourCustomer" id="our_customer_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">底部轮播图（涉及品牌）：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="bottomCarousel" id="bottom_carousel_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bottomCarousel" id="bottom_carousel_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">底部信息（涉及品牌）：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="bottomInfo" id="bottom_info_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bottomInfo" id="bottom_info_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">底部下载（涉及品牌）：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="bottomDownload" id="bottom_download_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bottomDownload" id="bottom_download_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                </div>
                <div id="landing_config" class="none">
                    <div id="footer_div" class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">底部公司标识：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="footer" id="footer_yes" value="1" checked> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="footer" id="footer_no" value="0"> 不显示
                            </label>
                        </div>
                    </div>
                    <div id="btn_div" class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">提交订单后按钮：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="btn" id="btn_yes" value="1"> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="btn" id="btn_no" value="0" checked> 不显示
                            </label>
                        </div>
                    </div>
                    <div id="app_div" class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">提交订单后二维码：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="app" id="app_yes" value="1"> 显示
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="app" id="app_no" value="0" checked> 不显示
                            </label>
                        </div>
                    </div>
                    <div id="enable_div" class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">使用优惠券：</span>

                        <div class="col-sm-6 form-inline text-left">
                            <label class="radio-inline">
                                <input type="radio" name="enable" id="enable_yes" value="1"> 允许
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="enable" id="enable_no" value="0" checked> 不允许
                            </label>
                        </div>
                    </div>
                    <div id="payment_channel_div" class="form-group">
                        <span class="col-sm-6 text-height-28 text-right">支付方式：</span>

                        <div class="col-sm-3">
                            <select id="paymentChannelSel" name="paymentChannels"
                                    class="form-control text-height-28 select-28" multiple="multiple">
                            </select>
                        </div>
                    </div>
                </div>
                <div id="frequency_div" class="form-group">
                    <span class="col-sm-2 text-height-28 text-right" style="white-space: nowrap;">邮件报表发送频率：</span>

                    <div class="col-sm-6 form-inline text-left">
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="frequency_week" value="1"> 每周
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="frequency_month" value="2"> 每月
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="frequency_no" value="3" checked> 不发送
                        </label>
                    </div>
                </div>
                <div id="email_div" class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">报表接收邮箱：</span>

                    <div class="col-sm-9 text-left text-height-28">
                        <textarea id="email" name="email" class="form-control text-height-28" rows="1"
                                  style="resize: none;padding: 0 10px;" placeholder="请输入邮箱，多个邮箱用;分隔，最多二百位"
                                  maxlength="200"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-left">监控数据：</span>
                </div>
                <div class="form-group" style="margin-bottom: 0;">
                    <div class="col-sm-12 text-center">
                        <table class="table table-bordered table-hover">
                            <tr id="basicMonitorType_tr">
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="addCustomerField" class="form-group">
                    <div class="col-sm-12 text-center">
                        <button type="button" class="btn btn-default text-input-830"><span style="color:#d43f3a">新增自定义字段（最多新增5个）</span>
                        </button>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-2 text-height-28 text-right">备注：</span>

                    <div class="col-sm-9 text-left">
                        <textarea id="comment" name="comment" class="form-control" rows="3" style="resize: none;"
                                  placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>

                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div>
                <input type="hidden" id="activityId" name="id" value="0">
            </div>
            <div class="form-group btn-finish">
                <div class="col-sm-12 text-center">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-100" value="保存">
                </div>
            </div>
        </form>
    </div>
</div>

<div id="detail_content" class="none">
    <div class="theme_poptit">
        <a id="detail_activity_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">查看商务活动详情</h4>
        <h4 class="text-center" id="detail_id_name">&lt;ID&gt; 商务活动名字</h4>
    </div>
    <div class="new detail">
        <form id="detail_form" class="form-input form-horizontal">
            <div class="diy-height">
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">合作商：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_partner"></span>
                    <span class="col-sm-3 text-height-28 text-right">合作方式：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_cooperationMode"></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">预算：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_budget"></span>

                    <div id="detail_rebate_div">
                        <span class="col-sm-3 text-height-28 text-right">佣金：</span>
                        <span class="col-sm-3 text-height-28 text-left" id="detail_rebate"></span>
                    </div>
                </div>
                <div id="detail_marketing_div" class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">推广活动：</span>
                    <span class="col-sm-9 text-height-28 text-left" id="detail_marketing"></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">城市：</span>
                    <span class="col-sm-9 text-height-28 text-left" id="detail_city" title=""></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">活动周期：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_startTime"></span>
                    <span class="col-sm-1"></span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_endTime"></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">落地页：</span>
                    <span class="col-sm-9 text-height-28 text-left" id="detail_landingPage"></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">联系人：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_linkMan"></span>
                    <span class="col-sm-3 text-height-28 text-right">联系方式：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_mobile"></span>
                </div>
                <div id="detail_frequency_div" class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">邮件报表发送频率：</span>
                    <span class="col-sm-3 text-height-28 text-left" id="detail_frequency"></span>
                </div>
                <div id="detail_email_div" class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">报表接收邮箱：</span>
                    <span class="col-sm-9 text-height-28 text-left" id="detail_email"></span>
                </div>
                <div id="detail_index_config" class="none">
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">我的中心：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_my_center"></span>
                        <span class="col-sm-4 text-height-28 text-right">车车顶部品牌（涉及品牌）：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_top_brand"></span>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">活动入口：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_activity_entry"></span>
                        <span class="col-sm-4 text-height-28 text-right">顶部轮播图（涉及品牌）：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_top_carousel"></span>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">我们的客户：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_our_customer"></span>
                        <span class="col-sm-4 text-height-28 text-right">底部轮播图（涉及品牌）：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_bottom_carousel"></span>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">底部下载（涉及品牌）：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_bottom_download"></span>
                        <span class="col-sm-4 text-height-28 text-right">底部信息（涉及品牌）：</span>
                        <span class="col-sm-2 text-height-28 text-left" id="detail_bottom_info"></span>
                    </div>
                </div>
                <div id="detail_landing_config" class="none">
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">使用优惠券：</span>
                        <span class="col-sm-3 text-height-28 text-left" id="detail_enable"></span>
                        <span class="col-sm-3 text-height-28 text-right">底部公司标识：</span>
                        <span class="col-sm-3 text-height-28 text-left" id="detail_footer"></span>
                    </div>
                    <div class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">提交订单后按钮：</span>
                        <span class="col-sm-3 text-height-28 text-left" id="detail_btn"></span>
                        <span class="col-sm-3 text-height-28 text-right">提交订单后二维码：</span>
                        <span class="col-sm-3 text-height-28 text-left" id="detail_app"></span>
                    </div>
                    <div id="detail_payment_channel_div" class="form-group">
                        <span class="col-sm-3 text-height-28 text-right">支付方式：</span>
                        <span class="col-sm-9 text-height-28 text-left" id="detail_payment"></span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-3 text-height-28 text-right">备注：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="detail_comment" class="form-control" rows="3" style="resize: none;"
                                  readonly></textarea>
                    </div>
                </div>
                <hr>
                <div>
                    <input type="hidden" id="detail_activityId">
                    <input type="hidden" id="detail_refreshFlag">
                </div>
                <div>
                    <h4 class="text-center">监控数据</h4>
                </div>
                <div class="col-sm-12 text-right" style="padding-right: 10px;">
                    <label class="text-center">数据更新时间：</label>
                    <span class="text-right" id="refreshTime"></span>
                </div>
                <div>
                    <div class="tabs">
                        <div class="col-sm-12">
                            <div id="areaGroup" class="btn-group" role="group">
                            </div>
                        </div>
                    </div>
                    <div id="monitor_data" class="col-sm-12 text-center"
                         style="width: 880px;overflow-x: auto;margin-top: 1px;">
                        <table class="table table-bordered table-hover" id="monitorData_tab">
                            <thead>
                            <tr class="active" id="monitor_data_tr">
                                <th class="text-center">日期</th>
                                <th class="text-center">PV</th>
                                <th class="text-center">UV</th>
                                <th class="text-center">注册</th>
                                <th class="text-center">试算</th>
                                <th class="text-center">提交订单数</th>
                                <th class="text-center">提交订单总额</th>
                                <th class="text-center">支付订单数</th>
                                <th class="text-center">支付订单总额</th>
                                <th class="text-center">不包含车船税总额</th>
                                <th class="text-center">特殊监控</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-sm-12 text-center">
                <%--<a id="refreshData" class="btn btn-danger">更新数据</a>--%>
                <a id="exportHourExcel" class="btn btn-danger">导出时段数据至EXCEL</a>
                <a id="editActivity" class="btn btn-danger">编辑</a>
            </div>
        </form>
    </div>
</div>

<div id="edit_content" class="none" style="height:100%">
    <div class="theme_poptit">
        <a id="edit_activity_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">编辑商务活动详情</h4>
        <h4 class="text-center" id="title_id_name"></h4>
        <h4 class="text-center" id="title_partner_cooperationMode"></h4>
        <h4 class="text-center" id="title_landingPage"></h4>
    </div>
    <div class="new">
        <form id="edit_form" class="form-input form-horizontal">
            <div class="diy-height" style="height: 360px;">
                <!--由于有些字段有校验，所以必须要满足校验，故写了很多hidden-->
                <input type="hidden" id="edit_id" name="id"/>
                <input type="hidden" id="edit_name" name="name"/>
                <input type="hidden" id="edit_partner" name="partner"/>
                <input type="hidden" id="edit_objId" name="objId"/>
                <input type="hidden" id="edit_cooperationMode" name="cooperationMode"/>
                <input type="hidden" id="edit_cooperationModeName" name="cooperationModeName"/>
                <input type="hidden" id="edit_landingPage" name="landingPage"/>

                <div class="form-group">
                    <div class="col-sm-12 text-center">
                        <span style="color: red;">注意:  仅下列各项可编辑，如欲修改其他项，请另行新建商务活动。</span>
                    </div>
                </div>
                <div id="edit_rebate_div" class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">佣金：</span>

                    <div class="col-sm-7 form-inline text-left">
                        <input id="edit_rebate" name="rebate" type="text"
                               class="form-control text-height-28 text-input-200"><span class="span-text">%</span>
                    </div>
                </div>
                <div id="edit_frequency_div" class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">邮件报表发送频率：</span>

                    <div class="col-sm-7 form-inline text-left">
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="edit_frequency_week" value="1"> 每周
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="edit_frequency_month" value="2"> 每月
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="frequency" id="edit_frequency_no" value="3"> 不发送
                        </label>
                    </div>
                </div>
                <div id="edit_email_div" class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">报表接收邮箱：</span>

                    <div class="col-sm-7 text-left">
                        <textarea id="edit_email" name="email" class="form-control text-input-840" rows="2"
                                  style="resize: none;" placeholder="请输入邮箱，多个邮箱用;分隔，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">联系人：</span>

                    <div class="col-sm-7 form-inline text-left">
                        <input id="edit_linkMan" name="linkMan" type="text"
                               class="form-control text-height-28 text-input-200" maxlength="10"
                               placeholder="请输入联系人，最多十位">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">联系方式：</span>

                    <div class="col-sm-7 form-inline text-left">
                        <input id="edit_mobile" name="mobile" type="text"
                               class="form-control text-height-28 text-input-200" maxlength="20" placeholder="请输入联系方式">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">预算：</span>

                    <div class="col-sm-7 form-inline text-left">
                        <input id="edit_budget" name="budget" type="text"
                               class="form-control text-height-28 text-input-200"><span class="span-text">元</span>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">活动周期：起：</span>

                    <div class="col-sm-7 text-left">
                        <input type="text" id="editStartTimeShow" name="editStartTimeShow" style="width:200px;"
                               placeholder="请选择开始时间" class="form-control text-height-28 text-input-200 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH：mm',vel:'edit_startTime',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss'});"
                               readonly>
                        <input id="edit_startTime" name="startTime" type="hidden" value="">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">止：</span>

                    <div class="col-sm-2 text-left">
                        <input type="text" id="editEndTimeShow" name="editEndTimeShow" style="width:200px;"
                               placeholder="请选择结束时间" class="form-control text-height-28 text-input-200 Wdate"
                               onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH：mm',vel:'edit_endTime',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss'});"
                               readonly>
                        <input id="edit_endTime" name="endTime" type="hidden" value="">
                    </div>
                </div>
                <div id="edit_marketing_remark_div" class="form-group span-supplement none">
                    <span class="col-sm-4"></span>
                    <span class="col-sm-8" style="color: red;">推广活动周期：<span id="edit_startTimeRemark"></span> 至 <span
                        id="edit_endTimeRemark"></span></span>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">备注：</span>

                    <div class="col-sm-7 text-left">
                        <textarea id="edit_comment" name="comment" class="form-control text-input-840" rows="5"
                                  style="resize: none;" placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>

                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-300 error-msg"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="edit_errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <div class="col-sm-12 text-center">
                    <input id="update" type="submit" class="btn btn-danger" value="完成">
                </div>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/partner/activity_management.js"></script>
