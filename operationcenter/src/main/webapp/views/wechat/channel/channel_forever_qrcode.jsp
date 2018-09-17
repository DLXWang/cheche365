<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title>永久二维码</title>
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
                <a style="margin-left: 5px;" id="yj_new" class="btn btn-danger">新建永久二维码</a>
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
<div id="yj_qrcode_new" class="none text-center">
    <div class="theme_poptit">
        <a id="yj_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center">新建永久二维码&nbsp;<span id="qrCodeSpan"></span></h4>
    </div>
    <div id="yj_new_qrcode_content" style="padding-top: 15px;padding-left: 25px;">
        <form id="yj_qrcode_new_form" class="form-input form-horizontal">
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道号：</span>

                <div class="col-sm-9 text-height-28 text-left">
                    <p id="channelNoText" class="text-left" style="font-weight: bold;">YJ00001</p>
                    <input type="hidden" id="code" name="code">
                    <input type="hidden" id="qrCodeType" name="qrCodeType" value="QR_LIMIT_SCENE">
                    <input type="hidden" id="updateFlag" name="updateFlag">
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
                           class="form-control text-input-300 text-height-28" placeholder="请输入所属部门，最多二十字"
                           maxlength="20">
                </div>
            </div>
            <div class="form-group form-inline">
                <span class="col-sm-3 text-height-28 text-right">返点金额：</span>

                <div class="col-sm-9 text-left">
                    <input id="rebate" name="rebate" type="text" class="form-control text-input-200 text-height-28"
                           placeholder="请输入返点金额"><span class="span-text">元</span>
                </div>
            </div>
            <div id="newCountGroup" class="form-group form-inline">
                <span class="col-sm-3 text-height-28 text-right">新建数量：</span>

                <div class="col-sm-9 text-left">
                    <button type="button" class="btn btn-danger" id="minus"><span style="font-weight: bold;">-</span>
                    </button>
                    <input id="newCount" name="newCount" type="text" class="form-control text-input-70 text-center"
                           style="margin-left: 0;" size="2" value="1" maxlength="3">
                    <button type="button" class="btn btn-danger" id="plus"><span style="font-weight: bold;">+</span>
                    </button>
                </div>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">备注：</span>

                <div class="col-sm-9 text-left">
                    <textarea id="comment" name="comment" class="form-control text-input-300" rows="8"
                              style="resize: none;" placeholder="请输入备注，最多二百字" maxlength="200"></textarea>
                </div>
            </div>
            <div id="remarkGroup" class="form-group">
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
            <div id="downloadGroup" class="form-group">
                <span class="col-sm-3 text-height-28 text-right"></span>

                <div class="col-sm-9 text-left">
                    <div class="checkbox">
                        <label>
                            <input id="downLoadFlag" name="downLoadFlag" type="checkbox" checked>完成新建后下载二维码至本地
                        </label>
                    </div>
                </div>
            </div>
            <div id="btnGroup" class="form-group">
                <span class="col-sm-3 text-height-28 text-right"></span>

                <div class="col-sm-9 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200" value="完成">
                </div>
            </div>
        </form>
    </div>
    <div id="yj_detail_qrcode" class="none text-center">
        <div class="theme_poptit">
            <a id="yj_detail_qrcode_close" href="javascript:;" title="关闭" class="close"><i
                class="glyphicon glyphicon-remove"></i></a>
            <h4 class="text-center" id="detail_title">永久二维码详情</h4>
        </div>
        <div id="yj_detail_qrcode_content" style="padding: 20px 20px 0 20px; height: 275px;">
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道号：</span>

                <p class="text-left" style="font-weight: bold;"><span id="detail_code"></span></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">渠道名：</span>

                <p id="detail_name" class="text-left"
                   style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""></p>
            </div>
            <div class="form-group">
                <span class="col-sm-3 text-height-28 text-right">所属部门：</span>

                <p id="detail_department" class="text-left"
                   style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis; width:197px;" title=""><span
                    id=""></span></p>
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
            <div class="form-group">
                <div class="col-sm-12 text-center" style="margin-top: 35px;">
                    <a id="export_count_excel" class="btn btn-danger text-input-160">导出扫描关注数Excel</a>
                    <a id="channel_forever_qrcode_edit" class="btn btn-danger text-input-80">编辑</a>
                </div>
            </div>
        </div>
        <div id="qrcodeImgDiv" class="form-group" style="float:right;padding-right: 30px;margin-top: -400px;">
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
<script type="text/javascript" src="<%=request.getContextPath()%>/js/wechat/channel/channel_forever_qrcode.js"></script>
