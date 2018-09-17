<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head lang="en">
    <link rel="stylesheet" href="/libs/bootstrap-3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/popup.css">
    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="/css/formInput.css">
    <link rel="stylesheet" href="/libs/uploadify-3.2.1/css/uploadify.css">
    <link rel="stylesheet" href="/libs/bootstrap-3.3.4/css/bootstrap-multiselect.css">
</head>
<style>
</style>
<body>
<div class="container tab_body" id="form">
    <form class="form-horizontal" id="includeCodeForm"  method="post" enctype="multipart/form-data">
        <div class="form-group"  style="margin-top:50px;">
            <label class="col-sm-5 control-label">选择导入平台的excel文件</label>
            <div class="col-sm-5">
                <input type="file" id="codeFile" name="codeFile" accept=".xls,.xlsx" class="text-input-150" style="width:300px;">
            </div>
        </div>

        <div class="form-group">
            <label class="col-sm-5 control-label">
                <div class="btn-group">
                    <button id="save_button" type="button" class="btn btn-danger" style="width: 150px;">上传</button>
                </div></label>
            <div class="col-sm-5" style="padding-top: 7px;">
                <div class="btn-group">
                    <a id="download_button" class="btn btn-danger">导出EXCEL</a>
                </div>
            </div>
        </div>
        <div class="form-group">
            <span class="col-sm-4"><p>&nbsp;</p></span>
            <div class="col-sm-3 text-left">
                <p class="alert alert-danger text-input-20 alert-user error-msg none" id="errorT"><i class="glyphicon glyphicon-remove-sign"></i> <span id="errorText"></span></p>
            </div>
        </div>
    </form>
</div>
</body>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-1.11.2/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-form/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/cookie.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/uploadify-3.2.1/js/jquery.uploadify-3.2.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-validation-1.14.0/jquery.validate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/bootstrap-3.3.4/js/bootstrap-multiselect.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jqPaginator-1.2.0/jqPaginator.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/libs/jquery-cookie-1.4.1/jquery.cookie.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common/CUI.select.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/insureProduct/insureProduct.js"></script>
</html>
