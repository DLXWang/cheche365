<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head lang="en">
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
        #show_div {
            height: 90%;
            overflow-y:auto;
            padding: 10px;
        }
    </style>
</head>

<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
        </table>
    </div>
</div>
<div id="user_red_packet_detail_div" class="table-responsive none">
    <div class="theme_poptit">
        <a id="detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="detail_title" class="text-center">详情</h4>
    </div>
    <div class="top-search">
        <div class="row">
            <div class="col-sm-8 form-inline">
                <select id="keyType" class="form-control text-input-150">
                    <option value="1">手机号</option>
                    <option value="2">车牌号</option>
                </select>
                <div class="input-group text-input-300">
                    <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                    <span class="input-group-btn">
                        <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                    </span>
                </div>
            </div>
            <div class="col-sm-8"></div>
        </div>
    </div>
    <div class="detail-together">
        <label>总记录数：</label>
        <span id="detail_totalCount" class="detail-all"></span>个
    </div>
    <div>
        <div class="col-sm-12">
            <table class="table table-bordered table-hover" id="detail_list_tab">
                <thead>
                <tr class="active">
                    <th class="text-center">序号</th>
                    <th class="text-center">手机号</th>
                    <th class="text-center">车牌号</th>
                    <th class="text-center">提交时间</th>
                    <th class="text-center">短信发送状态</th>
                    <th class="text-center">短信发送结果</th>
                    <th class="text-center">红包发放状态</th>
                    <th class="text-center">红包发放结果</th>
                    <th class="text-center">是否满足发送红包要求</th>
                    <th class="text-center">不能发送红包原因</th>
                    <th class="text-center">拍照信息序号</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <%--<div class="customer-pagination">--%>
            <%--<ul class="pagination"></ul>--%>
            <%--</div>--%>
            <div id="template_page_div" class="customer-pagination">
                <ul id="template_pagination" class="pagination"></ul>
            </div>
        </div>
    </div>
</div>
<jsp:include page="red_photo_pop.jsp"></jsp:include>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/red/red_management.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/red/user_red_packet_detail.js"></script>

