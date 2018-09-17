<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <div id="detail_content" class="none">
        <div class="theme_poptit">
            <a id="auto_detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
            <h4 class="text-center" id="detail_title">拍照信息详情</h4>
        </div>
        <div class="form-input-top">
            <div class="row" style="margin-left: -30px;height: 450px;">
                <div id="img_content" class="col-sm-7">
                    <ul style="list-style:none;">
                        <li id="item" class="" class="viewer" style="width:400px;height:400px;overflow:hidden;position:absolute;">
                            <img src="" id="cropper" class="cropper" width="400" height="400" style="border:5px solid #fff;position:absolute"/>
                        </li>
                       <!-- <input type="button" id="setPic"  value="设为行驶证照片" style="position:absolute;top:300px;left:200px;float:left; display: none;">-->
                    </ul>

                    <div id="Layer1" style="position:absolute;top:10px;left:60px; display:block; float:left; ">
                        <table cellspacing="2" cellpadding="0" border="0">
                            <tbody>
                            <tr><td>&nbsp; </td>
                                <td><a href="javascript:;"><img title="向上" style="cursor: hand" id="up" height="20" src="../../images/up.gif" width="20"></a> </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr><td><a href="javascript:;"><img title="向左" style="cursor: hand" id="left" height="20" src="../../images/left.gif" width="20"></a></td>
                                <td><a href="javascript:;"><img title="还原" style="cursor: hand" id="reset" height="20" src="../../images/zoom.gif" width="20"></a></td>
                                <td><a href="javascript:;"><img title="向右" style="cursor: hand" id="right" height="20" src="../../images/right.gif" width="20"></a></td>
                            </tr>
                            <tr><td>&nbsp;</td>
                                <td><a href="javascript:;"><img title="向下" style="cursor: hand" id="down" height="20" src="../../images/down.gif" width="20"></a>
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr><td>&nbsp;</td>
                                <td><a href="javascript:;"><img title="放大" style="cursor: hand" id="big" height="20" src="../../images/zoom_in.gif" width="20"></a></td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr><td>&nbsp;</td>
                                <td><a href="javascript:;"><img title="缩小" style="cursor: hand" id="lit" height="20" src="../../images/zoom_out.gif" width="20"></a></td>
                                <td>&nbsp;</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="rotateTool" style="position:absolute;top:360px;left:220px; float:left;">
                        <a href="javascript:;" title="左旋转"><img src="../../images/leftRotate.gif" alt="左旋转"id="leftRotate"></a>
                        <a href="javascript:;" title="右旋转"><img src="../../images/rightRotate.gif" alt="右旋转"id="rightRotate"></a>
                    </div>
                    <div id="img_bottom" class="img-bottom" style="background-color: #A19DA7;width: 240px; height: 70px; border:4px solid #DEDCDC; margin-top:410px;margin-left: 107px;">
                        <ul style="list-style:none;">
                            <li style="float: left;margin-left: 15px;margin-top: 4px;" class="active" ><a href="#item0" class="img-control" ><img src="" width="50" height="50" ></a></li>
                            <li style="float: left;margin-left: 20px;margin-top: 4px;" ><a href="#item1" class="img-control" ><img src="" width="50" height="50"></a></li>
                        </ul>
                    </div>
                </div>
                <div class="col-sm-5">
                    <div id="disable_content" class="text-center none" style="height: 450px;margin-right: 70px;">
                        <button type="button" class="btn btn-success btn-lg user-lg-btn enable" style="margin-bottom: 60px;margin-top: 80px;">有效</button><br/>
                        <button type="button" class="btn btn-danger btn-lg user-lg-btn disable">无效</button>
                    </div>
                    <div id="license_plate_no_content" class="none">
                        <div class="form-inline" style="margin-top: 70px;margin-bottom: 100px;">
                            <label>车牌号：</label>
                            <div id="old_license_plate_no_group" style="width: 150px;height: 200px;margin-left: 80px;margin-top:-25px;overflow-y: auto;">
                                <table class="table table-bordered table-hover">
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="form-inline">
                            <label>车牌号：</label>
                            <input type="text" class="form-control text-height-28" id="new_license_plate_no" name="newLicensePlateNo" placeholder="请输入车牌号" style="width: 200px;"/>
                        </div>
                        <div class="form-group text-center" style="margin-top: 20px;margin-right: 75px;">
                            <button type="button" class="btn btn-danger text-input-100" id="btn_create">新建</button>
                        </div>
                    </div>
                    <div id="existent_auto_content" class="none">
                        <form id="quote_photo_form" class="form-horizontal form-input" style="padding: 0;margin-left: -106px;margin-right: 4px;">
                            <input type="hidden" id="id" name="id" value="">
                            <input type="hidden" id="userId" name="userId" value="">
                            <div class="diy-height" style="height: 480px;">
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-12 text-center">
                                        —————————<span class="text-center bold-font">车主信息</span>—————————
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">车主姓名：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_owner"></span></p>
                                        <input id="input_owner" name="owner" type="text" class="form-control text-height-28 text-input-200 text-input none" >
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">车主身份证：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none" id="detail_identity"></p>
                                        <input id="input_identity" name="identity" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-12 text-center">
                                        ————————<span class="text-center bold-font">被保险人信息</span>————————
                                    </div>
                                </div>
                                <div id="insured_owner_radio" class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">被保险人：</div>
                                    <div class="col-sm-8 text-left">
                                        <label class="radio-inline">
                                            <input type="radio" id="insuredType_1" name="insuredType" value="1"> 车主
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" id="insuredType_2" name="insuredType" value="2" checked> 非车主
                                        </label>
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">姓名：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none" id="detail_insuredName"></p>
                                        <input id="input_insuredName" name="insuredName" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">身份证：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none" id="detail_insuredIdNo"></p>
                                        <input id="input_insuredIdNo" name="insuredIdNo" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-12 text-center">
                                        —————————<span class="text-center bold-font">车辆信息</span>—————————
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">车牌号：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left"><span id="detail_licensePlateNo"></span></p>
                                        <input type="hidden" id="licensePlateNoHid" name="licensePlateNo">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">车架号：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_vinNo"></span></p>
                                        <input id="input_vinNo" name="vinNo" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">发动机号：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_engineNo"></span></p>
                                        <input id="input_engineNo" name="engineNo" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">初登日期：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_enrollDate"></span></p>
                                        <input type="text" class="form-control Wdate text-height-28 text-input-200 text-input none" id="input_enrollDate" name="enrollDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate: '%y-%M-%d'});" readonly>
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">保险到期日：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_expireDate"></span></p>
                                        <input type="text" class="form-control Wdate text-height-28 text-input-200 text-input none" id="input_expireDate" name="expireDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true});" readonly>
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">品牌型号：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_code"></span></p>
                                        <input id="input_code" name="code" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">车型：</div>
                                    <div class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_model"></span></p>
                                        <input id="input_model" name="model" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-12 text-center">
                                        —————————<span class="text-center bold-font">用户信息</span>—————————
                                    </div>
                                </div>
                                <div class="form-group form-group-fix">
                                    <div class="col-sm-4 text-height-28 text-right">电话：</div>
                                    <div id="mobile_content" class="col-sm-8 text-height-28">
                                        <p class="text-left text-show none"><span id="detail_mobile"></span></p>
                                        <input id="input_mobile" name="mobile" type="text" class="form-control text-height-28 text-input-200 text-input none">
                                    </div>
                                </div>
                            </div>
                            <div id="btn_group" class="form-group">
                                <div class="col-sm-12 text-center">
                                    <button type="button" class="btn btn-danger btn-show toChangeAuto none">换辆车</button>
                                    <button type="button" class="btn btn-danger btn-show toQuote none">报价</button>
                                    <button type="button" class="btn btn-danger btn-show toEdit none">编辑</button>
                                    <button type="button" class="btn btn-danger btn-edit none toSave none">保存</button>
                                    <button type="button" class="btn btn-danger btn-edit none toCancel none">取消</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <!--<div class="btn-group" style="margin-left: 95px;">
                <button id="rotate_left" type="button" class="btn btn-default"><i class="fa fa-rotate-left"></i></button>
                <button type="button" class="btn btn-default"><i class="fa fa-rotate-right"></i></button>
                <button type="button" class="btn btn-default"><i class="glyphicon glyphicon-plus-sign"></i></button>
                <button type="button" class="btn btn-default"><i class="glyphicon glyphicon-minus-sign"></i></button>
            </div>-->
        </div>
    </div>
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/red/red_photo_pop.js"></script>
</html>
