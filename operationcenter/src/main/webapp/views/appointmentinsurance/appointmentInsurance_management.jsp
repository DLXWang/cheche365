<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .upload-btn {
            padding-top: 0 !important;
        }
        .new {
            padding-top: 15px;
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
        .remove-a {
            padding: 0 10px;
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
                <option value="1" selected>姓名</option>
                <option value="2">手机号</option>
                <option value="3">车牌号</option>
            </select>
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder=""/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
        </div>
        <div class="col-sm-4 form-inline">
            <span class="col-sm-5 text-height-28 text-right">此日期前车险到期：</span>
            <input type="text" id="startTimeShow" name="startTimeShow" placeholder="请选择日期" style="width: 185px;" class="form-control text-height-38 Wdate" onfocus="WdatePicker({onpicked:function(dp){appointmentinsurance.timeChange();},dateFmt:'yyyy-MM-dd',vel:'startTime',realDateFmt:'yyyy-MM-dd'});" readonly>
            <input id="startTime" name="startTime" type="hidden" value="">
        </div>
        <div class="col-sm-4 text-left">
            <a id="exportExcel" class="btn btn-danger">导出EXCEL</a>
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
                <th class="text-center">用户ID</th>
                <th class="text-center">用户姓名</th>
                <th class="text-center">用户手机</th>
                <th class="text-center">车牌号</th>
                <th class="text-center">车险到期日</th>
                <th class="text-center">提交时间</th>
                <th class="text-center">购买情况</th>
                <th class="text-center">来源渠道</th>
                <th class="text-center">状态</th>
                <th class="text-center">备注</th>
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
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="partner_title" class="text-center">编辑地推用户信息</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height" style="height:275px;">
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">姓名：</span>
                    <div class="col-sm-8 text-left">
                        <input id="name" name="name" type="text" class="form-control text-input-280 text-height-28" placeholder="" maxlength="20">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">车牌号：</span>
                    <div class="col-sm-8 text-left">
                        <input id="autoNo" name="autoNo" type="text" class="form-control text-input-280 text-height-28" placeholder="" maxlength="20">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">车险到期日：</span>
                    <div class="col-sm-8 text-left">
                        <input id="endDate" type="text" name="endDate" class="form-control text-height-28 text-input-200 Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly>
                        <input id="startTimeEnd" name="startTime" type="hidden" value="">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-10 text-right">备注：</span>
                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control text-input-280" rows="5" style="resize: none;" placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>
                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <span class="col-sm-4 text-height-28 text-right"></span>
                <div class="col-sm-8 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200">
                </div>
            </div>
            <div>
                <input type="hidden" id="appointmentInsurance" name="appointmentInsurance" value="">
                <input type="hidden" id="updateUserId" name="updateUserId" value="">
            </div>
        </form>
    </div>
</div>
<div id="yj_detail_qrcode" class="none text-center">
    <div class="theme_poptit">
        <a id="yj_detail_qrcode_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center" id="detail_title">永久二维码详情</h4>
    </div>
    <div id="yj_detail_qrcode_content" style="padding: 20px 20px 0 20px; height: 275px;">
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">渠道号：</span>
            <p class="text-left" style="font-weight: bold;"><span id="detail_code"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">渠道名：</span>
            <p id="detail_name" class="text-left" style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">所属部门：</span>
            <p id="detail_department" class="text-left" style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""><span id=""></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">扫描数：</span>
            <p class="text-left"><span id="detail_scanning_count"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">关注数：</span>
            <p class="text-left"><span id="detail_attention_count"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">绑定手机数：</span>
            <p class="text-left"><span id="detail_bindingMobile_count"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">成功订单数：</span>
            <p class="text-left"><span id="detail_successOrder_count"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">返点金额：</span>
            <p class="text-left"><span id="detail_rebate"></span></p>
        </div>
        <div class="form-group">
            <span class="col-sm-3 text-height-28 text-right">备注：</span>
            <div class="col-sm-9 text-left">
                <textarea id="detail_comment" class="form-control text-input-300" rows="3" style="resize: none;margin-left: -13px;" readonly></textarea>
            </div>
        </div>
    </div>
    <div id="qrcodeImgDiv" class="form-group" style="float:right;padding-right: 30px;margin-top: -350px;">
        <img id="qrCodeImg" src="" style="width: 150px;height: 150px;">
        <div class="text-right" style="margin-top: 10px;">
            <a class="btn btn-default text-right" style="width: 150px;">二维码</a>
        </div>
    </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/appointmentinsurance/appointmentinsurance_management.js"></script>
