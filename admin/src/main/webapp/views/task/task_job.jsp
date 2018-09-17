<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link type="text/css" href="<%=request.getContextPath()%>/css/jquery.dataTables.min.css" rel="stylesheet">
    <link type="text/css" href="<%=request.getContextPath()%>/css/dataTables.bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div id="top_div" class="top-search">
    <div class="col-sm-8 form-inline" style="margin-bottom:10px;">
        <input type="text" class="form-control text-input-150" id ="job_name_search" style="width: 150px;" placeholder="任务名称"/>
        <select id="status_search" class="form-control" style="width: 150px;margin-left:10px;margin-right:10px;">
            <option value="">状态</option>
            <option value="0">停用</option>
            <option value="1">启用</option>
        </select>
        <button  id="search" class="btn btn-danger">查询</button>
    </div>
    <div class="row">
        <div class="col-sm-8 form-inline">
            <span class="btn-group">
                <button id="toNew" class="btn btn-danger">新建任务</button>
            </span>
             <span class="btn-group">
                <button id="reset" class="btn btn-warning">同步任务</button>
            </span>
            <span class="btn-group">
                <button id="updateRedis" class="btn btn-warning">更新Redis</button>
            </span>
        </div>
    </div>
</div>
<div id="count_div" class="detail-together">
    <label>总共：</label>
    <span id="totalCount" class="detail-all"></span>条
</div>
<div id="show_div" class="table-responsive">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
            <thead>
            <tr class="active">
                <th class="text-center">序号</th>
                <th class="text-center">任务名称</th>
                <th class="text-center">时间表达式</th>
                <th class="text-center">状态</th>
                <th class="text-center">任务当前状态</th>
                <th class="text-center">上次执行时间</th>
                <th class="text-center">下次执行时间</th>
                <th class="text-center">创建时间</th>
                <th class="text-center">修改时间</th>
                <th class="text-center">操作人</th>
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
<div style="width: 494px; height: 654px; display: none; top: 40%;" id="popover_normal_input" class="theme_popover none">
    <div class="theme_poptit">
        <a id="task_detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="conditions_title" class="text-center">任务详情</h4>
    </div>
    <div class="new">
        <form id="job_form" class="form-input form-inline" style="padding: 20px;">
            <input id="id" name="id" type="hidden"/>
                <table class="table" style="border:0px;">
                    <tr>
                        <td>任务名称</td>
                        <td colspan="3"><input type="text" name="jobName" id="jobName" class="form-control text-input-200 text-height-30">
                        </td>
                    </tr>
                    <tr>
                        <td>执行对象</td>

                        <td colspan="3"><input type="text" name="jobClass" id="jobClass" class="form-control text-input-300 text-input text-height-30" style="width: 100%;"></td>
                    </tr>
                    <tr>
                        <td>时间表达式</td>
                        <td> <input type="text" name="jobCronExpression" id="jobCronExpression" class="form-control text-input-200 text-height-30"></td>
                        <td>状态</td>
                        <td> <select name="status" id="status" class="form-control text-input-200 text-height-30">
                            <option value="0">停用</option>
                            <option value="1">启用</option>
                        </select></td>
                    </tr>
                    <tr>
                        <td>参数1key</td>
                        <td><input type="text" name="paramKey1" id="paramKey1" class="form-control text-input-200 text-height-30"></td>
                        <td>参数1value</td>
                        <td> <input type="text" name="paramValue1" id="paramValue1" class="form-control text-input-200 text-height-30"></td>
                    </tr>
                    <tr>
                        <td>参数2key</td>
                        <td><input type="text" name="paramKey2" id="paramKey2" class="form-control text-input-200 text-height-30"></td>
                        <td>参数2value</td>
                        <td> <input type="text" name="paramValue2" id="paramValue2" class="form-control text-input-200 text-height-30"></td>
                    </tr>
                    <tr>
                        <td>参数3key</td>
                        <td><input type="text" name="paramKey3" id="paramKey3" class="form-control text-input-200 text-height-30"></td>
                        <td>参数3value</td>
                        <td> <input type="text" name="paramValue3" id="paramValue3" class="form-control text-input-200 text-height-30"></td>
                    </tr>
                    <tr>
                        <td>备注</td>
                        <td colspan="3"> <textarea id="comment" name="comment" class="form-control" placeholder="请输入备注..." rows="2" style="resize: none;height: 54px; width: 100%;" maxlength="200" data-value=""></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" align="center"> <div style="height: 24px;"><p class="alert alert-danger text-input-280 error-msg none"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p></div>
                            <input id="toCreate" type="button" class="btn btn-danger text-input-200" value="保存"></td>
                    </tr>
                </table>
        </form>
    </div>
</div>
<div style="width: 494px; height: 216px; display: none; top: 40%;" id="redis_input" class="theme_popover none">
    <div class="theme_poptit">
        <a id="redis_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 id="redis_title" class="text-center">redis更新</h4>
    </div>
    <div class="new">
        <form id="redis_form" class="form-input form-inline" style="padding: 20px;">
            <table class="table" style="border:0px;">
                <tr>
                    <td>redisKey</td>
                    <td> <input type="text" name="redisKey" id="redisKey" class="form-control text-input-200 text-height-30"></td>
                    <td>redisValue</td>
                    <td> <input type="text" name="redisValue" id="redisValue" class="form-control text-input-200 text-height-30"></td>
                </tr>
                <tr>
                    <td colspan="4" align="center"> <div style="height: 24px;"><p class="alert alert-danger text-input-280 error-msg none"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p></div>
                        <input id="toUpdateRedis" type="button" class="btn btn-danger text-input-200" value="保存"></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div style="width: 494px; height: 654px; display: none; top: 40%;" id="popover_record_input" class="theme_popover none">
    <div class="theme_poptit">
        <a id="record_detail_close" href="javascript:;" title="关闭" class="close"><i class="glyphicon glyphicon-remove"></i></a>
        <h4 class="text-center" id="detail_title">定时任务执行记录</h4>
    </div>
    <div class="form-input-top">
        <div class="col-sm-12" id="recordTb" style="overflow-y: auto;height: 85%;">
            <table class="table table-bordered" id="record_tab">
            </table>
        </div>
    </div>
</div>
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.grid.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/task/task_job.js"></script>
</html>
