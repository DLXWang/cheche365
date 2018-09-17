<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head lang="en">
    <title>新建活动</title>
    <link rel="stylesheet" href="/libs/bootstrap-3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/popup.css">
    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="/libs/uploadify-3.2.1/css/uploadify.css">
    <link rel="stylesheet" href="/libs/bootstrap-3.3.4/css/bootstrap-multiselect.css">
</head>
<style>

    .text-input-150{
        display:inline !important;
    }
    .tagator_tag {
        display: inline-block;
        background-color: #39f;
        border-radius: 2px;
        color: #fff;
        padding: 2px 20px 2px 4px;
        font-size: 13px;
        margin: 5px;
        position: relative;
        vertical-align: top;
    }
    .tagator_tag_remove {
        font-family: simsun;
        display: inline-block;
        font-weight: bold;
        color: #fff;
        margin: 0 0 0 5px;
        padding: 6px 2px 4px 2px;
        cursor: pointer;
        font-size: 12px;
        line-height: 10px;
        vertical-align: top;
        border-radius: 0 2px 2px 0;
        position: absolute;
        right: 0;
        top: 0;
        bottom: 0;
    }
    .discount_repeat p{
        margin-top: 10px;
        width:550px;
    }

    .add_del_span {
        padding: 5px;;
        float:right;
    }
    .margin-top-9{
        margin-top: 9px;;
    }
    li{
        list-style: none;
    }
    .width-180{
        width:180px;
    }

</style>
<body>
<div   style="width:90%;margin:auto;">
    <div class="theme_poptit">
        <h4 class="text-center" id="detail_title">创建优惠政策</h4>
        <input id="editType" type="hidden" value=""/>
    </div>
    <form id="add_form" method="post">

        <table class="table table-bordered" style="margin-top:5px; ">
            <tr>
                <td class="tab_td" style="width: 220px;text-align:right;" ><label class="control-label">活动主标题</label></td>
                <td id="titleTbl">
                    <div class="controls">
                        <input type="hidden" id="id" name="id"/>
                        <input type="text" class="form-control text-input-150" id="title" name="title" style="width: 200px" placeholder="活动主标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                        <small class="text-danger">字数在14个汉字以内</small>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">活动副标题</label></td>
                <td id="subTitleTbl">
                    <div class="controls">
                        <input type="text" class="form-control text-input-150" id="sub_title" name="subTitle"  style="width: 200px" placeholder="活动副标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                        <small class="text-danger">字数在16个汉字以内</small>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">优惠政策</label></td>
                <td id="strategyTbl" >
                    <div class="controls">
                        <textarea id="description" name="description" class="form-control text-input-400" rows="5" style="resize: none;" maxlength="200"></textarea>
                        <small class="text-danger">字数在200个汉字以内</small>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">使用规则</label></td>
                <td id="userGuideTbl" >
                    <div class="controls">
                        <input class="form-control text-input-150" id="userGuide" name="userGuide" style="width: 400px" placeholder="使用规则" value="本次活动不与其他优惠活动共享。" type="text">
                    </div>
                </td>
            </tr>
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">优惠类别</label></td>
                <td id="activityTypeInfo">
                    <div class="controls">
                        <select id="activityType" name="activityType" class="form-control text-input-150" style="margin-left:15px;margin-right:15px;margin-top: 5px;">
                            <option value="0">请选择优惠类别</option>
                        </select>
                    </div>

                    <!======================= 1 =======================>
                    <div id="discountByMoney" style="display: none">
                        <ul id="discountUL" >
                            <li class="discount_repeat">
                                <p>
                                    满<input type="text" class="discountByMoneyInput form-control text-input-150" name="discountByMoneyList[0].full"  style="width: 100px;margin-top: 5px;" maxlength="8"/>元
                                    减<input type="text" class="discountByMoneyInput form-control text-input-150" name="discountByMoneyList[0].discount"   style="width: 100px" style="margin-top: 5px;"  maxlength="8"/>元<span class="glyphicon glyphicon-plus add_del_span" id="addDiscount" ></span>
                                </p>
                            </li>
                        </ul>
                        <p style="margin-left: 50px;margin-top: 10px;" id="isAccumulate1">
                        </p>
                        <p style="margin-left: 50px;margin-top: 10px;" id="notMoreThan1Parent">
                            最高减免<span id="notMoreThan1"></span>元
                        </p>
                    </div>

                    <!======================= 2 =======================>
                    <div id="discountByInsurance" style="display: none;" >
                        <p style="margin-left: 40px;">减免</p>
                        <table style="margin-left: 60px;">
                            <tr>
                                <td style="padding-right: 20px;">
                                    减免的险种:
                                </td>
                                <td style="padding-right: 20px;">
                                    <p style="margin-bottom: 0px;display:block;" id="insuranceTypeDiv"></p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-right: 20px;">最高不超过</td>
                                <td style="padding-right: 20px;"><p style="margin-bottom: 0px;display:block;" id="notMoreThanInsuranceTypeDiv"></p></td>
                                <td style="padding-right: 20px;">的<input type="text" class="form-control text-input-150" id="discountInsuranceNotMoreThan"  name="discountByInsurance.notMoreThan"  style="width: 50px" style="margin-top: 5px;"  maxlength="6"/>%</td>
                            </tr>
                        </table>
                    </div>

                    <!======================= 3 =======================>
                    <div id="present" style="display: none">
                        <ul id="presentUL" >
                            <li class="discount_repeat">
                                <p>
                                    满<input type="text" class="presentInput form-control text-input-150" name="presentList[0].full" style="width: 100px" style="margin-top: 5px;"  maxlength="8"/>元
                                    送<input type="text" class="presentInput form-control text-input-150" name="presentList[0].discount"  style="width: 100px" style="margin-top: 5px;"  maxlength="8"/>元
                                    <select name="presentList[0].present" id="choosePresent" class="presentSelect form-control text-input-150"  style="margin-left:15px;margin-right:15px;margin-top: 5px;">
                                        <option value="0">请选择礼物 </option>
                                    </select>
                                    <span class="glyphicon glyphicon-plus add_del_span" id="addPresent"></span>
                                </p>
                            </li>
                        </ul>
                        <p style="margin-left: 50px;margin-top: 10px;" id="isAccumulate2">
                        </p>
                        <p style="margin-left: 50px;margin-top: 10px;" id="notMoreThan2Parent">
                            最高赠送<span id="notMoreThan2"></span>
                        </p>
                    </div>
                    <!======================= 4 =======================>
                    <div id="discountGift" style="display: none">
                        <div class="theme_poptit">
                            门槛（非必填）
                            <table style="margin-left: 60px;">
                                <tr>
                                    <td style="padding-right: 20px;">满</td>
                                    <td style="padding-right: 20px;"><p style="margin-bottom: 0px;display:block;" id="notMoreThanInsuranceTypeByGiftDiv"></p></td>
                                    <td style="padding-right: 20px;"><input type="text" class="form-control text-input-150" id="notMoreThanInsurancePerc"  name="discountGift.notMoreThanPerc"  style="width: 100px" style="margin-top: 5px;" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"  maxlength="8" />元</td>
                                </tr>
                            </table>
                        </div>
                        <div class="theme_poptit">
                            优惠（必填一项）
                            <table style="margin-left: 60px;">
                                <tr><td style="padding-right: 20px;">赠送  交强险的</td><td style="padding-right: 20px;"><input type="text" class="form-control text-input-150 discountGift" id="discountGiftInsuranceMust" name="discountGift.insuranceMust"  style="width: 70px" style="margin-top: 5px;" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"  maxlength="6"/></td><td style="padding-right: 20px;" >% 现金</td></tr>
                                <tr><td style="padding-right: 20px;">赠送  商业险的</td><td style="padding-right: 20px;"><input type="text" class="form-control text-input-150 discountGift" id="discountGiftCommercial" name="discountGift.commercial"  style="width: 70px" style="margin-top: 5px;" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" maxlength="6" /></td><td style="padding-right: 20px;"  >% 现金</td></tr>
                                <tr><td style="padding-right: 20px;">赠送  车船税的</td><td style="padding-right: 20px;"><input type="text" class="form-control text-input-150 discountGift" id="discountGiftVehicleTax" name="discountGift.vehicleTax"  style="width: 70px" style="margin-top: 5px;" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" maxlength="6" /></td><td style="padding-right: 20px;" >% 现金</td></tr>
                            </table>
                        </div>
                        <div class="theme_poptit">
                            封顶（非必填）
                            <p style="margin-left: 50px;margin-top: 10px;" id="notMoreThan2Parent1">
                                最高赠送
                                <input type="text" class="form-control text-input-150" name="discountGift.notMoreThanMoney" id="notMoreThanMoney"  style="margin-top: 5px;width: 100px" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"  maxlength="8"/>
                                元
                            </p>
                        </div>
                    </div>
                    <!======================= extra =======================>
                    <div id="extraPresent" style="display: none">
                        <ul id="extraPresentUL">
                            <li class="discount_repeat">
                                <p>
                                    <input type="checkbox" name="activityTypeInfo" value="extraPresent" id="extraCheckbox"/>
                                    满<input type="text" class="extraInput form-control text-input-150" name="extraPresentList[0].full"   style="width: 100px" style="margin-top: 5px;"  maxlength="8"/>元
                                    送<input type="text" class="extraInput form-control text-input-150" name="extraPresentList[0].discount"   style="width: 100px" style="margin-top: 5px;"  maxlength="8"/>元
                                    <select name="extraPresentList[0].present" id="chooseExtraPresent" class="extraSelect form-control text-input-150"  style="margin-left:15px;margin-right:15px;margin-top: 5px;">
                                        <option value="0">请选择礼物 </option>
                                    </select>
                                    <span class="glyphicon glyphicon-plus add_del_span margin-top-9" id="addExtra"></span>
                                </p>
                            </li>
                        </ul>
                    </div>
                </td>
            </tr>
            <tr class="gift_hidden">
                <td class="tab_td"  style="width: 120px;text-align:right;" ><label class="control-label">
                    需购买哪几种险种才能享受优惠
                </label></td>
                <td>
                    <div id="insuranceMustDiv">
                    </div>
                </td>
            </tr>
            <tr class="gift_hidden">
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    满额包含的险种
                </label></td>
                <td>
                    <div  id="fullIncludesDiv">
                    </div>
                </td>
            </tr>
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" >
                    <label class="control-label">
                        活动平台
                    </label>
                </td>
                <td id="channelTbl">
                    <div class="controls">
                        <select id="channelSelect" class="form-control text-input-150" style="margin-left:15px;margin-right:15px;margin-top: 5px;">
                            <option value="0">请选择活动平台</option>
                            <option value="official">官网平台</option>
                            <option value="thirdParty">第三方平台</option>
                            <option value="all">全平台</option>
                        </select>
                    </div>
                    <div id="channelDiv" style="margin-top:20px;">

                    </div>
                </td>
            </tr>
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" >
                    <label class="control-label">
                        活动支持的保险公司
                    </label>
                </td>
                <td>
                    <div id="insuranceCompanyDiv">
                    </div>
                </td>
            </tr>
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    活动支持的城市
                </label></td>
                <td id="areaTbl">
                    <div id="triggerCityDiv">
                        <input type="text" id="result_detail" name="resultDetail"  placeholder="城市名称"  class="form-control text-input-400" style="width: 175px;" AutoComplete="off" onpaste="return false" oncontextmenu="return false">
                    </div>
                    <input type="hidden" id="trigger_city" name="triggerCity">
                    <div class="tagator_tags">
                    </div>
                </td>
            </tr>
            <!-- <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    是否生成优惠券
                </label></td>
                <td>
                    <div class="controls">
                        <label class="radio-inline">
                            <input type="radio" name="cutting_type" value="male" />是
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="cutting_type" value="male" />否
                        </label>
                    </div>
                </td>
            </tr> -->
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    是否支持微信分享
                </label></td>
                <td id="wechatSharedTbl">
                    <div class="controls">
                        <label class="radio-inline">
                            <input type="radio" id="wechatyes" name="marketingShared.wechatShared" value="1" onclick="marketingAdd.wechatyes()"/>是
                        </label>
                        <label class="radio-inline">
                            <input type="radio" id="wechatno" name="marketingShared.wechatShared" value="0" checked="checked"  onclick="marketingAdd.wechatno()"/>否
                        </label>
                    </div>
                </td>
            </tr>
            <tr id="weixinShareTr"  style="display: none">
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    微信分享文案
                </label></td>
                <td>
                    <div class="controls">
                        <p style="margin-top: 10px;" id="wechatMainTitleTbl">
                            主标题<input type="text" id="wechatMainTitle" class="form-control text-input-150" name="marketingShared.wechatMainTitle"  style="width: 900px;margin-left: 10px;" placeholder="主标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                            <small class="text-danger">字数在15个汉字以内</small>
                        </p>
                        <p style="margin-top: 10px;" id="wechatSubTitleTbl">
                            副标题<input type="text" id="wechatSubTitle" class="form-control text-input-150" name="marketingShared.wechatSubTitle"  style="width: 900px;margin-left: 10px;" placeholder="副标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                            <small class="text-danger">字数在50个汉字以内</small>
                        </p>
                    </div>
                </td>
            </tr>
            <tr>
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    是否支持支付宝分享
                </label></td>
                <td   id="alipaySharedTbl">
                    <div class="controls">
                        <label class="radio-inline">
                            <input type="radio" id="alipayyes" name="marketingShared.alipayShared" value="1"  onclick="marketingAdd.alipayyes()" />是
                        </label>
                        <label class="radio-inline">
                            <input type="radio" id="alipayno" name="marketingShared.alipayShared" value="0"  checked="checked"  onclick="marketingAdd.alipayno()"/>否
                        </label>
                    </div>
                </td>
            </tr>
            <tr id="alipayShareTr"  style="display: none">
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">支付宝分享文案</label></td>
                <td>
                    <div class="controls">
                        <p style="margin-top: 10px;" id="alipayMainTitleTbl">
                            主标题<input type="text" id="alipayMainTitle" class="form-control text-input-150" name="marketingShared.alipayMainTitle"  style="width: 900px;margin-left: 10px;" placeholder="主标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                            <small class="text-danger">字数在15个汉字以内</small>
                        </p>
                        <p style="margin-top: 10px;" id="alipaySubTitleTbl">
                            副标题<input type="text" id="alipaySubTitle" class="form-control text-input-150" name="marketingShared.alipaySubTitle"  style="width: 900px;margin-left: 10px;" placeholder="副标题" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/>
                            <small class="text-danger">字数在50个汉字以内</small>
                        </p>
                    </div>
                </td>
            </tr>
            <tr  id="sharedPicTr" style="display: none">
                <td class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    分享小图
                </label></td>
                <td>
                    <div class="controls" id="shardIconTbl">
                        <input id="shared_icon_pic_file" runat="server" name="UpLoadSharedFile" type="file"  accept=".jpg"/>
                        <img id="sharedIconImage" class="image" style="width:100px;height:100px;display:none;" alt="分享小图" >
                        <a href="javascript:;" class="del" type="sharedIcon" title="删除图片" style="position: relative;top:-50px;left:-20px;display: none;"><img src="/images/del.gif" ></a>
                        <input type="hidden" name="marketingShared.sharedIcon" id="sharedIconPic" class="hidden_input">
                        </p><small class="text-danger">仅支持jpg格式 图片尺寸200*200</small>
                    </div>
                </td>
            </tr>
            <tr id="activityPicTr">
                <td  class="tab_td" style="width: 120px;text-align:right;" ><label class="control-label">
                    活动图片
                </label></td>
                <td>
                    <div class="controls" id="activityPicTbl">
                        <input id="activity_pic_file" runat="server" name="UpLoadActivityFile" type="file"  accept=".jpg"/>
                        <img id="activityImage" class="image" style="width:100px;height:100px;display:none;" alt="活动图片" >
                        <a href="javascript:;" class="del" type="activityPic" title="删除图片" style="position: relative;top:-50px;left:-20px;display: none;"><img src="/images/del.gif" ></a>
                        <input type="hidden" name="topImage" id="activityPic" class="hidden_input">
                        </p><small class="text-danger">仅支持jpg格式 图片尺寸750*420</small>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tab_td" style="width: 220px;text-align:right;" ><label class="control-label">生效日期</label></td>
                <td id="effectiveDateId">
                    <div class="controls">
                        <input type="text" id="effectiveDate" name="effectiveDate" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'%y-%M-{%d+1}',startDate:'%y-%M-{%d+1}'});" style="width: 175px;height: 27px;">
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tab_td" style="width: 220px;text-align:right;" ><label class="control-label">失效日期</label></td>
                <td id="expireDateId">
                    <div class="controls">
                        <input type="text" id="expireDate" name="expireDate" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'%y-%M-{%d+1}',startDate:'%y-%M-{%d+1}'});" style="width: 175px;height: 27px;">
                    </div>
                </td>
            </tr>
            <tbody>
            </tbody>
        </table>

    </form>
    <div class="detail_content_errorText" style="padding-top:90px;padding-bottom:10px;">
        <div>
            <span class="col-sm-3 text-height-28 text-center"></span>
            <div class="col-sm-8 text-left">
                <p class="alert alert-danger text-input-280 alert-user error-msg none" style="display: none;"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText"></span></p>
            </div>
        </div>
        <div class="form-group btn-finish">
            <div class="col-sm-12 text-center" style="padding-top:10px;padding-bottom:10px;">
                <input id="toSave" type="button" class="btn btn-danger text-input-200" value="提交">
            </div>
        </div>
    </div>
</div>
<!-- 二级弹层提示框 -->
<div id="theme_popover_second" class="theme_popover_second" style="display: none;width: 400px; height: 250px;">
    <div class="theme_poptit">
        <h3>温馨提示</h3>
    </div>
    <div style="padding-top: 40px; padding-bottom: 30px; text-align: center; font-size: 16px;">
        <label class="tipsContent">确定？</label>
    </div>
    <div style="text-align: center;">
        <div class="btn-group">
            <button style="font-size: 16px;" type="button" class="btn btn-primary confirm">确定</button>
        </div>
    </div>
</div>
<jsp:include page="../popup.jsp"/>
</body>

<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-1.11.2/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-form/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/cookie.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/uploadify-3.2.1/js/jquery.uploadify-3.2.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-validation-1.14.0/jquery.validate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap-multiselect.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jqPaginator-1.2.0/jqPaginator.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-cookie-1.4.1/jquery.cookie.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/marketingRule/marketing_add_image.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.select.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/marketingRule/marketing_add.js" ></script>
</html>
