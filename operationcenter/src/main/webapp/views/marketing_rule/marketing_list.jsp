<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head lang="en">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<form id="refresh_form" method="post">
    <div id="top_div" class="top-search">
        <div class="row">
            <div class="col-sm-12 form-inline">
                <input type="text" class="form-control text-input-150" id="marketingName" style="width: 200px"
                       placeholder="活动主标题"/>
                <select id="activityType" class="form-control text-input-150"
                        style="margin-left:15px;margin-right:15px;width: 200px">
                    <option value="">请选择活动类别</option>
                </select>
                <select id="status" name="status" class="form-control text-input-150"
                        style="margin-left:15px;margin-right:15px;width: 200px">
                    <option value="">请选择活动状态</option>
                </select>

                <p></p>
                <!--<input type="text" class="form-control text-input-150" id="area"  style="width: 200px" placeholder="活动支持城市" style="margin-left:15px;margin-right:15px;margin-top: 5px;"/> -->
                <div id="triggerCityDiv" style="float:left;overflow: hidden;">
                    <input type="text" id="result_detail" name="resultDetail" placeholder="活动支持城市"
                           class="form-control text-input-400" style="width: 200px;" AutoComplete="off"
                           onpaste="return false" oncontextmenu="return false">
                </div>
                <input type="hidden" id="trigger_city" name="triggerCity">
                <table style="float:left;display: block">
                    <tr>
                        <td>
                            <select id="channelFather" name="channelFather" class="form-control text-input-150"
                                    style="margin-left:20px;margin-right:15px;width: 200px">
                                <option value="">请选择活动平台分类</option>
                                <option value="official">官网平台</option>
                                <option value="thirdParty">第三方平台</option>
                                <option value="all">全平台</option>
                            </select>
                        </td>
                        <td>
                            <div id="channelDiv"><select id='channel' name='channel' class='form-control text-input-150'
                                                         style='margin-left:18px;margin-right:15px;width: 200px'></select>
                            </div>
                        </td>
                        <td>
                            <select id="insuranceCompany" class="form-control text-input-150"
                                    style="margin-left:15px;margin-right:15px;width: 200px">
                                <option value="">请选择支持保险公司</option>
                            </select>
                        </td>
                    </tr>
                </table>
                <span style="display: block;">
                    <button id="searchBtn" class="btn btn-danger" type="button">筛选</button>
                    <button id="addActivity" class="btn btn-danger" type="button" style="margin-left: 20px;"
                            onclick="window.open('/views/marketing_rule/marketing_add.jsp')">新建活动
                    </button>
                    <button id="refreshBtn" class="btn btn-danger" type="button" style="margin-left: 20px;">刷新</button>
                </span>
            </div>
        </div>
    </div>


    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="marketing_list"></table>
    </div>
</form>
<div id="history_content" class="container tab_body none">
    <div class="theme_poptit">
        <a id="close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"
                                                                      id="close_history"></i></a>
        <h4 class="text-center" id="agent_rebate_history_title">历史纪录</h4>
    </div>
    <div style="height:490px;overflow:auto;margin-left:15px;margin-right:15px;">
        <table class="table table-bordered table-hover" id="tabHistory" class="table">
            <thead>
            <tr class="active">
                <th class="text-center">ID</th>
                <th class="text-center">生效日期</th>
                <th class="text-center">类别</th>
                <th class="text-center">平台</th>
                <th class="text-center">主标题</th>
                <th class="text-center">副标题</th>
                <th class="text-center">政策</th>
                <th class="text-center">支持的保险公司</th>
                <th class="text-center">支持的城市</th>
                <th class="text-center">状态</th>
                <th class="text-center">编辑</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/marketingRule/marketing_rule.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.select.js"></script>

