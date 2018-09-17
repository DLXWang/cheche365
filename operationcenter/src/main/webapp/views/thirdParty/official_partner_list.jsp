<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head >
    <meta charset="UTF-8">
</head>

<form id="refresh_form" method="post">
    <div id="top_div" class="top-search">
        <div class="row">
            <div class="col-sm-12 form-inline">
                <input type="text" class="form-control text-input-150" id="keyword" style="width: 200px;margin-left:15px;margin-right:15px;"
                       placeholder="请输入公司名称"/>
                <button id="searchButton" class="btn btn-danger" type="button" style="margin-left:15px;margin-right:15px;"> 搜索 </button>
                <button id="newPartner" class="btn btn-danger" type="button" style="margin-left:15px;margin-right:15px;">+ 新建</button>
            </div>
        </div>
    </div>


    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="partner_list"></table>
    </div>
</form>
<div id="history_content" class="container tab_body none">
    <div class="theme_poptit">
        <a id="close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove" id="close_history"></i></a>
        <h4 class="text-center" id="agent_rebate_history_title">历史纪录</h4>
    </div>
    <div style="height:490px;overflow:auto;margin-left:15px;margin-right:15px;">
        <table class="table table-bordered table-hover" id="tabHistory" class="table">
            <thead>
            <tr class="active">
                <th class="text-center">公司名称</th>
                <th class="text-center">备注</th>
                <th class="text-center">操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 id="partner_title" class="text-center">新建合作商</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height">
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">公司名称：</span>

                    <div class="col-sm-8 text-left">
                        <input id="name" name="name" type="text" class="form-control text-input-280 text-height-28"
                               placeholder="请输入第三方渠道所属公司中文全称" maxlength="20">
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">备注（选填）：</span>

                    <div class="col-sm-8 text-left">
                        <textarea id="comment" name="comment" class="form-control text-input-280" rows="5"
                                  style="resize: none;" placeholder="请输入备注，最多200字符" maxlength="200"></textarea>
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
            <div class="form-group btn-finish">
                <span class="col-sm-4 text-height-28 text-right"></span>

                <div class="col-sm-8 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200" value="提交">
                </div>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/thirdParty/official_partner_list.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.select.js"></script>

