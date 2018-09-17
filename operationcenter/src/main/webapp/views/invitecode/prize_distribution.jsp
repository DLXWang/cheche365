<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style type="text/css">
        .theme_poptit_color {
            background-color: #CE5F48;
        }
    </style>
</head>

<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-4 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="1" selected>用户信息</option>
                <option value="2">奖品名</option>
            </select>
            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder=""/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
        </div>
        <div class="col-sm-4 text-left">
            <select id="statusSel" class="form-control text-input-150">
                <option value="">全部状态</option>
                <option value="0">未处理</option>
                <option value="1">已处理</option>
            </select>
        </div>
        <div class="col-sm-4 text-left">
            <a id="exportExcel" class="btn btn-danger">导出奖品发放excel</a>
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
                <th class="text-center">用户信息</th>
                <th class="text-center">获得奖品</th>
                <th class="text-center">奖品类型</th>
                <th class="text-center">数量</th>
                <th class="text-center">获得时间</th>
                <th class="text-center">收货信息</th>
                <th class="text-center">状态</th>
                <th class="text-center">记录</th>
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
<div id="prize_address_div" class="none">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="partner_title" class="text-center">收货信息详情</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div >
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">姓名：</span>
                    <div class="col-sm-8 text-left">
                        <span id="userName" type="text" name="userName" class="text-height-28 text-input-200" >
                        <input type="text" id="addUserName" name="addUserName" placeholder="姓名" style="height: 28px;line-height:28px" class="form-control text-input-200 text-input">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">电话：</span>
                    <div class="col-sm-8 text-left">
                        <span id="phoneNo" type="text" name="phoneNo" class="text-height-28 text-input-200" >
                        <input type="text" id="addPhoneNo" name="addPhoneNo" placeholder="电话" style="height: 28px;line-height:28px" class="form-control text-input-200 text-input">
                    </div>
                </div>
                <div class="form-group" style="display: none" id="read_address">
                    <span class="col-sm-4 text-height-28 text-right">地址：</span>
                    <div class="col-sm-8 text-left">
                        <span id="address" type="text" name="address" class="text-height-28 text-input-200" >
                    </div>
                </div>
            </div>
            <div>
                <div class="form-group form-group-fix" id="add_address">
                    <div class="col-sm-2 text-height-28 text-right">地址：</div>
                    <div class="col-sm-10 text-height-28">
                        <p class="text-left text-show none" id="detail_address"></p>
                        <select id="select_province" class="form-control text-input-150 select none" name="province"></select>
                        <select id="select_city" class="form-control text-input-150 select none" name="city"></select>
                        <select id="select_district" class="form-control text-input-150 select none" name="district"></select>
                    </div>
                </div>
                <div class="form-group form-group-fix" id="add_street">
                    <div class="col-sm-2 text-height-28 text-right"></div>
                    <div class="col-sm-10 text-height-28">
                        <p class="text-left text-show none" id="detail_street"></p>
                        <input type="text" id="input_street" name="street" placeholder="街道" class="form-control text-height-28 text-input-400 text-input">
                    </div>
                </div>
                <div class="form-group error-line" style="margin-bottom: 0px;">
                    <span class="col-sm-4"></span>
                    <div class="col-sm-2 text-left none">
                        <p class="alert-danger text-input-150" style="text-align: center;"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                    </div>
                </div>
                <div class="col-sm-12 text-center btn-margin-bottom-10 btn-margin-top-10 text-center" style="height: 54px;line-height: 54px;">
                    <button id="toAdd" type="button" class="btn btn-danger text-width-100">新增</button>
                    <button id="toSave" type="submit" class="btn btn-danger text-width-100">保存</button>
                    <button id="toCancel" type="button" class="btn btn-danger text-width-100">取消</button>
                    <button id="toClose" type="button" class="btn btn-danger text-width-100">确定</button>
                </div>
            </div>
        </form>
    </div>
</div>
<div id="prize_comment_div" class="table-responsive none">
    <div class="theme_poptit theme_poptit_color">
        <a href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center title">备注</h4>
    </div>
    <div id="comment_list" style="min-height:200px;max-height: 280px;overflow-y: auto;padding: 10px;">
    </div>
    <div class="form-group text-center" style="border-top: 1px solid #ddd;">
        <div class="form-group text-center" style="border-top: 20px"></div>
        <form id="comment_form">
            <input type="hidden" id="prizeSendId" name="prizeSendId"/>
            <div class="col-sm-8 text-left btn-margin-bottom-10 btn-margin-top-10">
                <textarea id="comment" name="comment" class="form-control" placeholder="请输入备注..." rows="2" style="resize: none;height: 54px;" maxlength="200" data-value=""></textarea>
            </div>
            <div class="col-sm-4 text-center btn-margin-bottom-10 btn-margin-top-10" style="height: 54px;line-height: 54px;">
                <button id="toCreate" type="button" class="btn btn-danger btn-lg text-width-100 toAddComment">发送</button>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/invitecode/prize_distribution.js"></script>
