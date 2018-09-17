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

        .divedit {
            width: 650px;
            margin: 100px auto 0;
            position: relative;
        }

        .textinput {
            width: 400px;
            min-height: 120px;
            max-height: 300px;
            _height: 120px;
            margin-left: auto;
            margin-right: auto;
            padding: 3px;
            outline: 0;
            border: 1px solid #a0b3d6;
            font-size: 12px;
            word-wrap: break-word;
            overflow-x: hidden;
            overflow-y: auto;
            _overflow-y: visible;
        }

        .texthover .textinput {
            border-color: #f5507a;
        }

        .texthover .arrowline {
            border-color: transparent transparent #f5507a transparent;
        }

        .arrowbox {
            width: 20px;
            height: 20px;
            position: absolute;
            left: 52px;
            top: -9px;
        }

        .arrow {
            width: 0;
            height: 0;
            font-size: 0;
            line-height: 0;
            position: absolute;
            overflow: hidden;
        }

        .arrowline {
            top: -1px;
            border-style: dashed dashed solid;
            border-width: 5px;
            border-color: transparent transparent #d9d9d9 transparent;
        }

        .arrowbg {
            top: 0;
            border-style: dashed dashed solid;
            border-width: 5px;
            border-color: transparent transparent #ff
    </style>
</head>
<div id="top_div" class="top-search">
    <div class="row">
        <div class="col-sm-8 form-inline">
            <select id="keyType" class="form-control text-input-150">
                <option value="5">名字</option>
                <option value="3">备注</option>
            </select>

            <div class="input-group text-input-300">
                <input type="text" class="form-control" id="keyword" placeholder="请输入搜索内容"/>
                <span class="input-group-btn">
                    <button id="searchBtn" class="btn btn-danger" type="button">搜索</button>
                </span>
            </div>
            <span class="btn-group">
                <button id="toNew" class="btn btn-danger">新建筛选用户</button>
            </span>
        </div>
    </div>
</div>
<div id="show_div">
    <div class="col-sm-12">
        <table class="table table-bordered table-hover" id="list_tab">
        </table>
    </div>
</div>
<div id="new_content" class="none">
    <div class="theme_poptit">
        <a id="ls_new_qrcode_close" href="javascript:;" title="关闭" class="close"><i
            class="glyphicon glyphicon-remove"></i></a>
        <h4 id="filter_title" class="text-center">新建筛选用户功能</h4>
    </div>
    <div class="new">
        <form id="new_form" class="form-input form-horizontal">
            <div class="diy-height">
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">名字：</span>

                    <div class="col-sm-7 text-left">
                        <input id="name" name="name" type="text" class="form-control text-height-28"
                               placeholder="请输入名字，最多二十位" maxlength="20">
                    </div>
                </div>

                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">SQL模板：</span>

                    <div class="col-sm-7 text-left">
                        <select id="sqlTemplateSel" name="sqlTemplateId" class="form-control text-height-28 select-28">
                            <option value="">请选择SQL模板</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">SQL：</span>

                    <div class="col-sm-7 text-left">
                        <%--<textarea id="content" name="content" class="form-control" rows="5" style="resize: none;" readonly maxlength="1000"></textarea>--%>
                        <div id="content" name="content" class="form-control textinput" rows="5" style="resize: none;"
                             readonly maxlength="1000"></div>
                    </div>
                </div>
                <div id="sqlParameter">

                </div>
                <div class="form-group">
                    <span class="col-sm-4 text-height-28 text-right">备注：</span>

                    <div class="col-sm-7 text-left">
                        <textarea id="comment" name="comment" class="form-control" rows="5" style="resize: none;"
                                  placeholder="请输入备注，最多二百位" maxlength="200"></textarea>
                    </div>
                </div>
            </div>
            <div class="form-group error-line">
                <span class="col-sm-4"></span>

                <div class="col-sm-7 text-left none">
                    <p class="alert alert-danger text-input-280 error-msg"><i
                        class="glyphicon glyphicon-remove-sign"></i> <span id="errorText">错误提示</span></p>
                </div>
            </div>
            <div class="form-group btn-finish">
                <span class="col-sm-4 text-height-28 text-right"></span>

                <div class="col-sm-7 text-left">
                    <input id="toCreate" type="submit" class="btn btn-danger text-input-200" value="保存">
                </div>
            </div>
            <div>
                <input type="hidden" id="filterId" name="id" value="0">
                <input type="hidden" id="parameter" name="parameter" value="">
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/plugins/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/datatable_util.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/sms/filter_user_management.js"></script>



