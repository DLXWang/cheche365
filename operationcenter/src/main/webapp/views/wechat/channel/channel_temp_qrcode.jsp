<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title>临时二维码</title>
    <style type="text/css">
        .top-search {
            padding-top: 15px;
        }
    </style>
</head>
<body>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="0" selected>渠道号</option>
                <option value="1">渠道名</option>
                <option value="2">到期时间</option>
            </select>

            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="渠道号"/>
                    <span class="input-group-btn">
                        <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                    </span>
            </div>
            <div class="btn-group">
                <a id="export_excel" class="btn btn-danger">当前列表导出成Excel</a>
            </div>
            <div class="btn-group">
                <a style="margin-left: 5px;" id="ls_new" class="btn btn-danger">新建临时二维码</a>
            </div>
        </div>
    </div>
</div>
<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="channel_tab">
        </table>
    </div>
</div>
<div id="ls_new_qrcode" class="none text-center">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">新建临时二维码&nbsp;<span id="qrCodeSpan">LS00001</span></h4>
    </div>
    <div style="padding-top: 15px;padding-left: 25px;">
        <form id="ls_qrcode_new_form" class="form-input form-horizontal">
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道号：</span>

                <div class="col-sm-9 text-height-28 text-left">
                    <p id="channelNoText" class="text-left" style="font-weight: bold;">LS00001</p>
                    <input type="hidden" id="code" name="code">
                    <input type="hidden" id="qrCodeType" name="qrCodeType" value="QR_SCENE">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道名：</span>

                <div class="col-sm-9 text-left">
                    <input id="name" name="name" type="text" class="form-control text-input-300 text-height-28"
                           placeholder="请输入渠道名，最多二十字" maxlength="20">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">所属部门：</span>

                <div class="col-sm-9 text-left">
                    <input id="department" name="department" type="text"
                           class="form-control text-input-300 text-height-28" placeholder="请输入所属部门名称，最多二十字"
                           maxlength="20">
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">到期时间：</span>

                <div class="col-sm-9 text-left">
                    <input type="text" name="expireTimeShow" class="form-control text-height-28 text-input-200 Wdate"
                           onfocus="WdatePicker({dateFmt:'yyyy年MM年dd日 HH时',vel:'expireTime',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss',minDate:'%y-%M-%d {%H+1}',maxDate:'%y-%M-{%d+7} {%H+1}'});"
                           readonly>
                    <input id="expireTime" name="expireTime" type="hidden" value="">
                </div>
            </div>
            <div class="form-group form-inline">
                <span class="col-sm-3 text-height-28 text-right">返点金额：</span>

                <div class="col-sm-9 text-left">
                    <input id="rebate" name="rebate" type="text" class="form-control text-input-200 text-height-28"
                           placeholder="请输入返点金额"><span class="span-text">元</span>
                </div>
            </div>
            <div class="form-group form-inline">
                <span class="col-sm-3 text-height-28 text-right">新建数量：</span>

                <div class="col-sm-9 text-left">
                    <button type="button" class="btn btn-danger" id="minus"><span style="font-weight: bold;">-</span>
                    </button>
                    <input id="newCount" name="newCount" type="text" class="form-control  text-center"
                           style="margin-left: 0;" size="2" value="1" maxlength="3">
                    <button type="button" class="btn btn-danger" id="plus"><span style="font-weight: bold;">+</span>
                    </button>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">备注：</span>

                <div class="col-sm-9 text-left">
                    <textarea id="comment" name="comment" class="form-control text-input-300" rows="5"
                              style="resize: none;" placeholder="请输入备注，最多二百字" maxlength="200"></textarea>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right"></span>

                <div class="col-sm-9 text-left">
                    <p class="text-left alert alert-info text-input-300" style="margin: 0;padding: 2px;">
                        注意：新建数多于1时将批量新建，渠道号递<br/>增，渠道名后续加序号。</p>
                </div>
            </div>
            <div class="form-group" style="height: 10px;">
                <span class="col-sm-3"></span>

                <div class="col-sm-8 text-left none">
                    <p class="alert alert-danger text-input-300" style="margin: 0;padding: 2px;"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="errorText"></span></p>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right"></span>

                <div class="col-sm-9 text-left">
                    <div class="checkbox">
                        <label>
                            <input id="downLoadFlag" name="downLoadFlag" type="checkbox" checked>完成新建后下载二维码至本地
                        </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right"></span>

                <div class="col-sm-9 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200" value="完成">
                </div>
            </div>
        </form>
    </div>
    <div id="ls_detail_qrcode" class="none text-center">
        <div class="theme_poptit">
            <a id="ls_detail_qrcode_close" href="javascript:;" title="关闭" class="close"><i
                class="glyphicon glyphicon-remove"></i></a>
            <h4 class="text-center" id="detail_title">临时二维码详情</h4>
        </div>
        <div style="padding: 20px 20px 0 20px;height: 470px;">
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道号：</span>

                <p class="text-left" style="font-weight: bold;"><span id="detail_code"></span></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道名：</span>

                <p class="text-left" id="detail_name"
                   style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">所属部门：</span>

                <p class="text-left" id="detail_department"
                   style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">有效状态：</span>

                <p class="text-left"><span id="detail_status"></span></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">到期时间：</span>

                <p class="text-left"><span id="detail_expire_time"></span></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">扫描数：</span>

                <p class="text-left"><span id="detail_scan_count"></span></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">关注数：</span>

                <p class="text-left"><span id="detail_subscribe_count"></span></p>
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
                    <textarea id="detail_comment" class="form-control text-input-300" rows="3"
                              style="resize: none;margin-left: -13px;" readonly></textarea>
                </div>
            </div>
        </div>
        <div class="form-group">
            <span class="col-sm-4 text-height-28 text-right"></span>

            <div class="col-sm-8 text-left">
                <a id="export_count_excel" class="btn btn-danger text-input-200">导出扫描关注数Excel</a>
            </div>
        </div>
        <div class="form-group" style="float:right;padding-right: 30px;margin-top: -450px;">
            <img id="qrCodeImg" src="" style="width: 150px;height: 150px;">

            <div class="text-right" style="margin-top: 10px;">
                <a id="qrCodeImg_download" href="javascript:;" target="_self" class="btn btn-default text-right"
                   style="width: 150px;">下载二维码</a>
            </div>
        </div>
    </div>
</div>
<form id="downloadForm" class="none" action="" method="post">
</form>
</body>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/wechat/channel/channel_qrcode.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/wechat/channel/channel_temp_qrcode.js"></script>
