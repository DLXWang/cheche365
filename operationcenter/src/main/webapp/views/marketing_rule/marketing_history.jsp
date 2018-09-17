<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head lang="en">
    <link type="text/css" href="../../bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link type="text/css" href="../../css/common.css">
    <link rel="stylesheet" href="../../css/popup.css">
    <link rel="stylesheet" href="../../css/search_list.css">
</head>
<body>
<div id="detail_content" class="none " style="width:70%;margin:auto;margin-top: 50px;">
    <div class="theme_poptit">
        <h4 class="text-center" id="detail_title">查看历史</h4>
        <input id="editType" type="hidden" value=""/>
    </div>
    <div class="table-responsive">
        <div class="col-sm-12">
            <table class="table table-bordered table-hover" id="perfect_driver_tab">
                <thead>
                <tr class="active">
                    <th class="text-center">活动ID</th>
                    <th class="text-center">生效时间</th>
                    <th class="text-center">活动类别</th>
                    <th class="text-center">活动平台</th>
                    <th class="text-center">活动主标题</th>
                    <th class="text-center">活动副标题</th>
                    <th class="text-center">活动政策</th>
                    <th class="text-center">活动支持政策公司</th>
                    <th class="text-center">活动支持的城市</th>
                    <th class="text-center">有无使用优惠券</th>
                    <th class="text-center">需购买几种险种才能享受优惠</th>
                    <th class="text-center">满额包含的险种</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <div class="customer-pagination">
                <ul class="pagination" id="pageUl"></ul>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript" src="../../js/jquery/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="../../bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../../js/common/common.js"></script>
<script type="text/javascript" src="../../js/common/popup.js"></script>
<script type="text/javascript" src="../../My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../../js/quote/quote_help.js"></script>
<script type="text/javascript" src="../../js/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="../../js/common/cookie.js"></script>
<script type="text/javascript" src="../../js/common/application_log.js"></script>
<script type="text/javascript" src="../../js/quote/quote_photo_pop.js"></script>
<script type="text/javascript" src="../../js/common/CUI.js"></script>
<script type="text/javascript" src="../../js/common/CUI.select.js"></script>
<script type="text/javascript" >
    $(function(){
        $("#quote_pp").load("../../page/quote/quote_photo_pop.jsp");
    });
</script>
</html>

