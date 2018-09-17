/**
 * Created by wangfei on 2015/4/22.
 */
var common = {
    /* ajax请求 */
    getByAjax: function (async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error) {
        return $.ajax({
            async: async,
            type: methodType,
            dataType: returnType,
            url: url,
            data: data,
            success: function (data) {
                callbackMethod_success(data);
            },
            error: function (xhr, textStatus, errorThrown) {
                if (!common.sessionTimeOut(xhr) && !common.accessDenied(xhr) && !common.noPermissionLogin(xhr)) {
                    callbackMethod_error();
                }
            }
        });
    },
    /**
     * session timeout
     * @param xhr
     * @returns {boolean}
     */
    sessionTimeOut: function (xhr) {
        if (xhr.getResponseHeader("sessionstatus") == "timeOut") {
            popup.mask.hideAllMask();
            popup.mould.popTipsMould(false, "会话过期，请重新登录！", popup.mould.first, popup.mould.warning, "重新登录", "57%",
                function () {
                    window.parent.location.href = xhr.getResponseHeader("loginPath");
                }
            );
            return true;
        }
        return false;
    },
    /**
     * access denied
     * @param xhr
     * @returns {boolean}
     */
    accessDenied: function (xhr) {
        if (xhr.getResponseHeader("sessionstatus") == "accessDenied") {
            popup.mask.hideAllMask();
            popup.mould.popTipsMould(false, "对不起，您没有权限执行此操作！", popup.mould.first, popup.mould.warning, null, "57%", null);
            return true;
        }
        return false;
    },
    /**
     * no permission to login
     * @param xhr
     * @returns {boolean}
     */
    noPermissionLogin: function (xhr) {
        if (xhr.getResponseHeader("sessionstatus") == "noPermissionLogin") {
            popup.mask.hideAllMask();
            popup.mould.popTipsMould(false, "你没有权限登录管理系统！", popup.mould.first, popup.mould.warning, null, "57%", null);
            return true;
        }
        return false;
    },

    /**
     * 判断的字节长度
     * @param text
     * @returns {number}
     */
    getByteLength: function (text) {
        var count = 0;
        return text.length;
    },

    /* 空值转换"" */
    checkToEmpty: function (obj) {
        if (obj) {
            return obj == "null" ? "" : obj;
        }
        return "";
    },

    /**
     * 字符串长度大于某值后截取到length处，后加......
     * @param str
     * @param length
     * @returns {*}
     */
    getFormatComment: function (str, length) {
        if (!str) {
            return "";
        }

        if (str.length > length) {
            return str.substring(0, length) + "……";
        } else {
            return str;
        }
    },

    /**
     * 适应table中备注字数固定加TITLE的需求
     * @param str
     * @param length
     * @returns {string}
     */
    getCommentMould: function (str, length) {
        return "<span title=\"" + str + "\">" + this.getFormatComment(str, length) + "</span>";
    },

    /**
     * 下拉框格式化
     * @param data_list
     * @param key_name
     * @param value_name
     * @returns {string}
     */
    getFormatOptionList: function (data_list, key_name, value_name) {
        var option_list;
        $.each(data_list, function (index, data) {
            option_list += '<option value="' + data[key_name] + '">' + data[value_name] + '</option>';
        });
        return option_list;
    },

    /* 是否手机号 */
    isMobile: function (str) {
        return this.isValidatePattern(str, /^(13|14|15|16|17|18|19)[0-9]{9}$/);
    },

    /* 身份证验证 */
    isIdCardNo: function (num) {
        num = num.toUpperCase();
        //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X。
        if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
            return false;
        }
        //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
        var arrInt = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
        var arrCh = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
        //下面分别分析出生日期和校验位
        var len, re, arrSplit, dtmBirth, bGoodDay, nTemp = 0;
        len = num.length;
        if (len == 15) {
            re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
            arrSplit = num.match(re);

            //检查生日日期是否正确
            dtmBirth = new Date('19' + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
            bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
            if (!bGoodDay) {
                return false;
            }
            else {
                //将15位身份证转成18位
                num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
                for (var i = 0; i < 17; i++) {
                    nTemp += num.substr(i, 1) * arrInt[i];
                }
                num += arrCh[nTemp % 11];
                return num;
            }
        } else if (len == 18) {
            re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
            arrSplit = num.match(re);

            //检查生日日期是否正确
            dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
            bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
            if (!bGoodDay) {
                return false;
            } else {
                //检验18位身份证的校验码是否正确。
                //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
                var valNum;
                for (var j = 0; j < 17; j++) {
                    nTemp += num.substr(j, 1) * arrInt[j];
                }
                valNum = arrCh[nTemp % 11];
                if (valNum != num.substr(17, 1)) {
                    return false;
                }
                return num;
            }
        }
        return false;
    },

    /* 验证车牌号 */
    validateLicenseNo: function (licensePlateNo) {
        licensePlateNo = licensePlateNo.toUpperCase();
        var flag = false, msg = '';
        if (this.isEmpty(licensePlateNo)) {
            msg = "请输入车牌号";
        } else if (!this.isChineseChar(licensePlateNo.substring(0, 1))) {
            msg = "车牌号地区简称应为中文"
        } else if (!/^[a-zA-Z]/.test(licensePlateNo.substring(1, 2))) {
            msg = "车牌号地区之后应为字母"
        } else if ("京B" == licensePlateNo.substring(0, 2)) {
            msg = "不支持京B开头的车牌号"
        } else if (this.isChineseChar(licensePlateNo.substring(1))) {
            msg = "车牌号地区之后不应有中文"
        } else if (this.getByteLength(licensePlateNo) > 8) {
            msg = "车牌号最长是8位";
        } else if (this.getByteLength(licensePlateNo) < 7) {
            msg = "车牌号最小长度是7位";
        } else if (/\s/.test(licensePlateNo)) {
            msg = '车牌号不能包含空格';
        } else {
            flag = true;
        }
        //  return {flag: flag, msg: msg};
        return flag;
    },

    //对于车主姓名的验证
    validateName: function (owner) {
        var flag = false, msg = "";
        if (this.isEmpty(owner)) {
            msg = "请输入姓名";
        } else if (this.getByteLength(owner) < 2) {
            msg = "车主姓名的长度为2~4个汉字或者不少于4个英文字母";
        } else if (this.getByteLength(owner) > 45) {
            msg = "车主姓名不能多于45个汉字或90个英文字母";
        } else {
            flag = true;
        }
        // return {flag: flag, msg: msg};
        return flag;
    },

    //对于车架号的验证
    validateVinNo: function (vinNo) {
        //vinNo = vinNo.toUpperCase();
        var flag = false, msg = '';
        if (this.isEmpty(vinNo)) {
            msg = '车辆识别代号不能为空';
        } else if (this.isSpecialLetter(vinNo)) {
            msg = '车辆识别代号不可以包含 “*”、“.”“-”特殊字符';
        } else if (this.isChineseChar(vinNo)) {
            msg = "车辆识别代号不可以包含汉字";
        } else if (/[IOQ]/.test(vinNo)) {
            msg += "车辆识别代号不可以包含字母" + (/[IOQ]/.exec(vinNo)[0]);
        } else if (vinNo.length != 17) {
            msg = "车辆识别代号必须是17位";
        } else if (vinNo.match(/([a-z])+/)) {
            msg = "车架号字母必须是大写";
        } else {
            flag = true;
        }
        // return {flag: flag, msg: msg};
        return flag;
    },

    //验证发动机号
    validateEngineNo: function (engineNo) {
        // engineNo = engineNo.toUpperCase();
        var flag = false, msg = '';
        if (this.isEmpty(engineNo)) {
            msg = '发动机号不能为空';
        } else if (!/[0-9a-zA-Z*、._ ]{5,}/.test(engineNo)) {
            msg = '发动机号仅可以包含 “*”、“.”“-”特殊字符';
        } else if (this.isChineseChar(engineNo)) {
            msg = "发动机号不可以包含汉字";Git
        } else if (engineNo.length < 5) {
            msg = "发动机号至少是5位";
        } else if (engineNo.match(/([a-z])+/)) {
            msg = "车架号字母必须是大写";
        } else {
            flag = true;
        }
        // return {flag: flag, msg: msg};
        return flag;
    },

    validIdentity: function(identityType,identityNo) {
        if(identityType == 1) {
            if (common.isIdCardNo(identityNo)) {
                return true;
            } else {
                return false;
            }
        }else{
            if(common.isEmpty(identityNo)){
                return false;
            }else{
                return true;
            }
        }
        // }else if(identityType == 11){
        //     if(common.validateLicenseNo(identityNo)){
        //         return true;
        //     } else {
        //         return false;
        //     }
        // }else if(identityType == 12){
        //     if(common.isBusinessRegNo(identityNo)){
        //         return true;
        //     } else {
        //         return false;
        //     }
        // }else if(identityType == 13){
        //     if(common.isCreditCode(identityNo)){
        //         return true;
        //     } else {
        //         return false;
        //     }
        // }
    },

    /* 检测是否包含特殊字符 */
    isSpecialLetter: function (value) {
        var vkeyWords = /[\':;*?~`!@#$%^&+={}\[\]\<\>\(\),\.]/;
        if (vkeyWords.test(value)) {
            return true;
        } else {
            return false;
        }
    },

    /* 判断文本是否存在中文字符,并且返回其个数 */
    isChineseChar: function (str) {
        var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;//判断是否包含中文字符,正正则表达式,也可以检测日文,韩文
        return reg.test(str);
    },

    /* 是否为空或者全部都是空格 */
    isEmpty: function (str) {
        return $.trim(str) == "";
    },

    /* 验证邮箱格式 */
    isEmail: function isEmail(str) {
        return this.isValidatePattern(str, /^\w+@[a-z\d]+\.(com|cn|com.cn|net|org)$/);
    },

    /* 6-12为数字、字母或下划线 */
    isPassword: function (str) {
        return this.isValidatePattern(str, /^\w{6,12}$/);
    },
    /* 必须包含大小写字母和数字 */
    isPasswordEx: function (str) {
        return str.match(/([a-z])+/) && str.match(/([0-9])+/) && str.match(/([A-Z])+/)
    },

    /* 验证金额 小数点后允许两位，小数点前不限 */
    isMoney: function (str) {
        return this.isValidatePattern(str, /^(([1-9]\d*)|0)(\.(\d){1,2})?$/);
    },

    /* 验证数值 */
    isNumber: function (str) {
        return this.isValidatePattern(str, /^\d+(\.\d+)?$/);
    },

    /* 验证单引号 */
    isSingleQuote: function (str) {
        var reg = /[']/;
        return reg.test(str);
    },
    /* 组织机构代码校验 */
    isOrgCode :function(orgCode){
        var values=orgCode.split("-");
        var ws = [3, 7, 9, 10, 5, 8, 4, 2];
        var str = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        var reg = /^([0-9A-Z]){8}$/;
        if (!reg.test(values[0])) {
            return true
        }
        var sum = 0;
        for (var i = 0; i < 8; i++) {
            sum += str.indexOf(values[0].charAt(i)) * ws[i];
        }
        var C9 = 11 - (sum % 11);
        var YC9=values[1]+'';
        if (C9 == 11) {
            C9 = '0';
        } else if (C9 == 10) {
            C9 = 'X'  ;
        } else {
            C9 = C9+'';
        }
        return YC9!=C9;
    },
    /* 工商注册号校验*/
    isBusinessRegNo :function(regNo){
        var result=false;
        if(regNo.length==15){
            var s=[];
            var p=[];
            var a=[];
            var m=10;
            p[0]=m;
            for(var i=0;i<regNo.length;i++){
                a[i]=parseInt(regNo.substring(i,i+1),m);
                s[i]=(p[i]%(m+1))+a[i];
                if(0==s[i]%m){
                    p[i+1]=10*2;
                }else{
                    p[i+1]=(s[i]%m)*2;
                }
            }
            if(1==(s[14]%m)){
                result=true;
            }else{
                result=false;
            }
        }
        return result;
    },

    /* 社会统一信用码校验*/
    isCreditCode :function(code){
        var patrn = /^[0-9A-Z]+$/;
        //18位校验及大写校验
        if ((code.length != 18) || (patrn.test(code) == false))
        {
            return false;
        }else{
            var Ancode;//统一社会信用代码的每一个值
            var Ancodevalue;//统一社会信用代码每一个值的权重
            var total = 0;
            var weightedfactors = [1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28];//加权因子
            var str = '0123456789ABCDEFGHJKLMNPQRTUWXY';
            //不用I、O、S、V、Z
            for (var i = 0; i < Code.length - 1; i++){
                Ancode = code.substring(i, i + 1);
                Ancodevalue = str.indexOf(Ancode);
                total = total + Ancodevalue * weightedfactors[i];
                //权重与加权因子相乘之和
            }
            var logiccheckcode = 31 - total % 31;
            if (logiccheckcode == 31){
                logiccheckcode = 0;
            }
            var Str = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,J,K,L,M,N,P,Q,R,T,U,W,X,Y";
            var Array_Str = Str.split(',');
            logiccheckcode = Array_Str[logiccheckcode];
            var checkcode = code.substring(17, 18);
            if (logiccheckcode != checkcode){
                return false;
            }
        }
        return true;
    },

    /*
     日期格式化
     对Date的扩展，将 Date 转化为指定格式的String
     月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
     年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
     例子：
     (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
     (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
     */
    formatDate: Date.prototype.Format = function (date, fmt, add) {
        if (add) {
            date.setDate(date.getDate() + add);
        }
        var o = {
            "M+": date.getMonth() + 1,                 //月份
            "d+": date.getDate(),                    //日
            "h+": date.getHours(),                   //小时
            "m+": date.getMinutes(),                 //分
            "s+": date.getSeconds(),                 //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    },

    /* 公用确认提示框 */
    showPublicTips: function (content) {
        window.parent.$("#theme_popover_publicConfirm").find(".tipsContent").html(content);
        window.parent.$(".theme_popover_mask").show();
        window.parent.$("#theme_popover_publicConfirm").show();
    },

    /* 等待框*/
    showWaitTips: function (content) {
        window.parent.$("#theme_popover_wait").find(".tipsContent").html(content);
        window.parent.$(".theme_popover_mask").show();
        window.parent.$("#theme_popover_wait").show();
    },

    /* 自定义弹出框 */
    showUserDefinedTips: function (content, height, width) {
        window.parent.$("#theme_popover_userDefined").html(content);
        window.parent.$("#theme_popover_userDefined").height(height);
        window.parent.$("#theme_popover_userDefined").width(width);
        window.parent.$(".theme_popover_mask").show();
        window.parent.$("#theme_popover_userDefined").show();
    },

    /* 公用提示框 注意：已经有弹框的时候需要再弹框提示使用common.showSecondTips二级弹框提示 */
    showTips: function (content) {
        window.parent.$("#theme_popover_tips").find(".tipsContent").html(content);
        window.parent.$(".theme_popover_mask").show();
        window.parent.$("#theme_popover_tips").show();
        window.parent.$("#theme_popover_tips").find(".confirm").unbind("click").bind({
            click: function () {
                common.hideMask();
            }
        });
    },

    /* 公用父页面二级提示框 */
    showParentSencondTips: function (content) {
        $("#theme_popover_second").find(".tipsContent").html(content);
        $(".theme_popover_mask_second").show();
        $("#theme_popover_second").show();
        $("#theme_popover_second").find(".confirm").unbind("click").bind({
            click: function () {
                common.hideParentSecondMask();
            }
        });
    },

    /* 二层弹框提示 */
    showSecondTips: function (content) {
        window.parent.$("#theme_popover_second").find(".tipsContent").html(content);
        window.parent.$(".theme_popover_mask_second").show();
        window.parent.$("#theme_popover_second").show();
        window.parent.$("#theme_popover_second").find(".confirm").unbind("click").bind({
            click: function () {
                common.hideSecondMask();
            }
        });
    },

    /*  隐藏二层弹框提示 */
    hideSecondMask: function () {
        window.parent.$('.theme_popover_mask_second').hide();
        window.parent.$('.theme_popover_second').hide();
    },

    /* 隐藏提示框 */
    hideMask: function () {
        window.parent.$('.theme_popover_mask').hide();
        window.parent.$('.theme_popover').hide();
    },

    /* 隐藏父页面二级提示框 */
    hideParentSecondMask: function () {
        $('.theme_popover_mask_second').hide();
        $('.theme_popover_second').hide();
    },

    /* 获取url中指定name参数 */
    getUrlParam: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)return unescape(r[2]);
        return null;
    },

    /* 数字格式化 s为需转化数字，n为要预留的小数位 */
    formatMoney: function (s, n) {
        if (s == null) {
            return "0.00";
        }
        n = n > 0 && n <= 20 ? n : 2;
        s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
        var l = s.split(".")[0].split("").reverse(),
            r = s.split(".")[1];
        t = "";
        for (i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "" : "");
        }
        return t.split("").reverse().join("") + "." + r;
    },

    /* 正则表达式校验 */
    isValidatePattern: function (value, pattern) {
        var regex = pattern;
        return regex.test(value);
    },

    /* 是否有管理员角色  */
    isCustomerAdmin: function (data) {
        var isCustomerAdmin = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心管理员") {
                isCustomerAdmin = true;
                return false;
            }
        });
        return isCustomerAdmin;
    },

    /* 是否有客服角色 */
    isCustomer: function (data) {
        var isCustomer = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心客服") {
                isCustomer = true;
                return false;
            }
        });
        return isCustomer;
    },

    /* 是否有内勤角色 */
    isInternal: function (data) {
        var isInternal = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心内勤") {
                isInternal = true;
                return false;
            }
        });
        return isInternal;
    },

    /* 是否有状态变更角色 */
    isStatusChange: function (data) {
        var isStatusChange = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心修改状态") {
                isStatusChange = true;
                return false;
            }
        });
        return isStatusChange;
    },

    /* 是否有渠道角色 */
    isCPSChannel: function (data) {
        var isCPSChannel = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心CPS") {
                isCPSChannel = true;
                return false;
            }
        });
        return isCPSChannel;
    },

    /* 是否有大客户角色 */
    isVip: function (data) {
        var isVip = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心大客户") {
                isVip = true;
                return false;
            }
        });
        return isVip;
    },

    /* 是否录单员 */
    isInsuranceInputter: function (data) {
        var isInputter = false;

        $.each(data, function (i, obj) {
            if (obj == "出单中心录单员") {
                isInputter = true;
                return false;
            }
        });
        return isInputter;
    },

    /* 获取指定字符长度，区分中英文 一个中文长度为2 */
    getLength: function (obj) {
        var l = 0;
        var a = obj.split("");
        for (var i = 0; i < a.length; i++) {
            if (a[i].charCodeAt(0) < 299) {
                l++;
            } else {
                l += 2;
            }
        }
        return l;
    },
    scrollToTop: function () {
        window.parent.scrollTo(0, 0);
    },
    getOrderIcon: function (path) {
        if (!common.isEmpty(path)) {
            return "<img src='" + path + "' style='height:16px;width:16px;float:left;position:absolute;margin:2px 0px 0px -20px;'>";
        } else {
            return "";
        }
    },
    getOrderIconClean: function (path) {
        if (!common.isEmpty(path)) {
            return "<img src='" + path + "' style='height:16px;width:16px;float:left;margin:2px 0px 0px 0px;'>";
        } else {
            return "";
        }
    },
    getOrderIconByData: function (iconPath, data) {
        if (!common.isEmpty(iconPath)) {
            return "<span><img src='" + iconPath + "' style='height:16px;width:16px;margin-right:3px;'>" + data + "</span>";
        } else {
            return data;
        }
    },
    getTelIconByData: function (data, sourceId, registerChannelId) {
        var iconData = "";
        // 支付宝加油服务
        if (sourceId == 90 || sourceId == 91 || sourceId == 92 || sourceId == 31 || sourceId == 61 || sourceId == 112) {
            iconData = common.getOrderIconByData(10, data);
        }
        // 支付宝服务窗
        else if (sourceId == 93 || sourceId == 94) {
            iconData = common.getOrderIconByData(21, data);
        }
        // 汽车之家
        else if (sourceId == 100 || sourceId == 119 || sourceId == 130) {
            iconData = common.getOrderIconByData(13, data);
        }
        // 百度地图
        else if (sourceId == 110 || sourceId == 111 || sourceId == 116) {
            iconData = common.getOrderIconByData(15, data);
        }
        // 途虎养车
        else if (sourceId == 113 || sourceId == 114 || sourceId == 117 || sourceId == 135) {
            iconData = common.getOrderIconByData(18, data);
        }
        // 车享网
        else if (sourceId == 120 || sourceId == 121) {
            iconData = common.getOrderIconByData(19, data);
        }
        // 未购买订单用户，注册渠道：支付宝加油服务
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 10) {
            iconData = common.getOrderIconByData(10, data);
        }
        // 未购买订单用户，注册渠道：支付宝服务窗
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 21) {
            iconData = common.getOrderIconByData(21, data);
        }
        // 未购买订单用户，注册渠道：汽车之家
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 13) {
            iconData = common.getOrderIconByData(13, data);
        }
        // 未购买订单用户，注册渠道：油客网
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 14) {
            iconData = common.getOrderIconByData(14, data);
        }
        // 未购买订单用户，注册渠道：百度地图
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 15) {
            iconData = common.getOrderIconByData(15, data);
        }
        // 未购买订单用户，注册渠道：途虎养车
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 203) {
            iconData = common.getOrderIconByData(203, data);
        }
        // 未购买订单用户，注册渠道：车享网
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 19) {
            iconData = common.getOrderIconByData(19, data);
        }
        // 未购买订单用户，注册渠道：人保寿险
        else if (sourceId == 40 && registerChannelId != null && registerChannelId == 23) {
            iconData = common.getOrderIconByData(23, data);
        } else {
            iconData = data;
        }
        return iconData;
    },
    /* ------------------分割线（新增加的请按分类添加，新的功能用到的老的代码移至对应的分类下，老的不用再维护，请按规范填写）--------------- */
    ajax: {
        /**
         * 设置ajax请求头header属性
         * @param xhr
         */
        setHeaderParams: function (xhr, headerParamsMap) {
            if (!headerParamsMap || !headerParamsMap.size()) {
                return;
            }
            headerParamsMap.each(function (key, value, index) {
                xhr.setRequestHeader(key, value);
            });
        },
        /**
         * ajax请求数据
         * @param async
         * @param methodType
         * @param returnType
         * @param url
         * @param data
         * @param callbackMethod_success
         * @param callbackMethod_error
         */
        getByAjax: function (async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error) {
            $.ajax({
                async: async,
                type: methodType,
                dataType: returnType,
                url: url,
                data: data,
                success: function (data) {
                    callbackMethod_success(data);
                },
                error: function (xhr, textStatus, errorThrown) {
                    if (!common.ajax.sessionTimeOut(xhr) && !common.ajax.accessDenied(xhr) && !common.ajax.noPermissionLogin(xhr)) {
                        callbackMethod_error();
                    }
                }
            });
        },
        /**
         * ajax请求数据
         * @param async
         * @param methodType
         * @param returnType
         * @param url
         * @param data
         * @param callbackMethod_success
         * @param callbackMethod_error
         * @param headerParamsMap header头参数列表,map格式
         */
        getByAjaxWithHeader: function (async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error, headerParamsMap) {
            $.ajax({
                async: async,
                type: methodType,
                dataType: returnType,
                url: url,
                data: data,
                beforeSend: function (xhr) {
                    common.ajax.setHeaderParams(xhr, headerParamsMap);
                },
                success: function (data) {
                    callbackMethod_success(data);
                },
                error: function (xhr, textStatus, errorThrown) {
                    if (!common.ajax.sessionTimeOut(xhr) && !common.ajax.accessDenied(xhr) && !common.ajax.noPermissionLogin(xhr)) {
                        callbackMethod_error();
                    }
                }
            });
        },
        /**
         * ajax请求数据(json格式发送)
         * @param async
         * @param methodType
         * @param returnType
         * @param url
         * @param data
         * @param callbackMethod_success
         * @param callbackMethod_error
         */
        getByAjaxWithJson: function (async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error) {
            $.ajax({
                async: async,
                type: methodType,
                contentType: "application/json",
                dataType: returnType,
                url: url,
                data: convertJson(data),
                success: function (data) {
                    callbackMethod_success(data);
                },
                error: function (xhr, textStatus, errorThrown) {
                    if (!common.ajax.sessionTimeOut(xhr) && !common.ajax.accessDenied(xhr) && !common.ajax.noPermissionLogin(xhr)) {
                        callbackMethod_error();
                    }
                }
            });

            function convertJson(data) {
                if (!data) {
                    return {};
                }
                return JSON.stringify(data);
            }
        },
        /**
         * ajax请求数据(json格式发送)
         * @param async
         * @param methodType
         * @param returnType
         * @param url
         * @param data
         * @param callbackMethod_success
         * @param callbackMethod_error
         * @param headerParamsMap header头参数列表,map格式
         */
        getByAjaxWithJsonAndHeader: function (async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error, headerParamsMap) {
            $.ajax({
                async: async,
                type: methodType,
                contentType: "application/json",
                dataType: returnType,
                url: url,
                data: convertJson(data),
                beforeSend: function (xhr) {
                    common.ajax.setHeaderParams(xhr, headerParamsMap);
                },
                success: function (data) {
                    callbackMethod_success(data);
                },
                error: function (xhr, textStatus, errorThrown) {
                    if (!common.ajax.sessionTimeOut(xhr) && !common.ajax.accessDenied(xhr) && !common.ajax.noPermissionLogin(xhr)) {
                        callbackMethod_error();
                    }
                }
            });
            function convertJson(data) {
                if (!data) {
                    return {};
                }
                return JSON.stringify(data);
            }
        },
        /**
         * session timeout
         * @param xhr
         * @returns {boolean}
         */
        sessionTimeOut: function (xhr) {
            if (xhr.getResponseHeader("sessionstatus") == "timeOut") {
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(false, "会话过期，请重新登录！", popup.mould.first, popup.mould.warning, "重新登录", "57%",
                    function () {
                        window.parent.location.href = xhr.getResponseHeader("loginPath");
                    }
                );
                return true;
            }
            return false;
        },
        /**
         * access denied
         * @param xhr
         * @returns {boolean}
         */
        accessDenied: function (xhr) {
            if (xhr.getResponseHeader("sessionstatus") == "accessDenied") {
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(false, "对不起，您没有权限执行此操作！", popup.mould.first, popup.mould.warning, null, "57%", null);
                return true;
            }
            return false;
        },
        /**
         * no permission to login
         * @param xhr
         * @returns {boolean}
         */
        noPermissionLogin: function (xhr) {
            if (xhr.getResponseHeader("sessionstatus") == "noPermissionLogin") {
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(false, "你没有权限登录管理系统！", popup.mould.first, popup.mould.warning, null, "57%", null);
                return true;
            }
            return false;
        }
    },
    /**
     * 公共校验
     */
    validations: {
        /**
         * 字符串是否为空、undefined
         * @param obj
         * @returns {boolean}
         */
        isEmpty: function (obj) {
            return (typeof(obj) == "undefined" || !obj || $.trim(obj) == "");
        },
        /**
         * 检测是否包含特殊字符
         * @param value
         * @returns {boolean}
         */
        isSpecialLetter: function (value) {
            var vkeyWords = /[\':;*?~`!@#$%^&+={}\[\]\<\>\(\),\.]/;
            if (vkeyWords.test(value)) {
                return true;
            } else {
                return false;
            }
        },
        /**
         * 判断文本是否存在中文字符,并且返回其个数
         * @param str
         * @returns {boolean}
         */
        isChineseChar: function (str) {
            var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;//判断是否包含中文字符,正正则表达式,也可以检测日文,韩文
            return reg.test(str);
        },
        /**
         * 车牌号校验
         * @param licensePlateNo
         * @returns {boolean}
         */
        validateLicenseNo: function (licensePlateNo) {
            licensePlateNo = licensePlateNo.toUpperCase();
            var flag = false, msg = '';
            if (common.validations.isEmpty(licensePlateNo)) {
                msg = "请输入车牌号";
            } else if (!common.validations.isChineseChar(licensePlateNo.substring(0, 1))) {
                msg = "车牌号地区简称应为中文"
            } else if (!/^[a-zA-Z]/.test(licensePlateNo.substring(1, 2))) {
                msg = "车牌号地区之后应为字母"
            } else if ("京B" == licensePlateNo.substring(0, 2)) {
                msg = "不支持京B开头的车牌号"
            } else if (common.validations.isChineseChar(licensePlateNo.substring(1))) {
                msg = "车牌号地区之后不应有中文"
            } else if (common.tools.getByteLength(licensePlateNo) > 8) {
                msg = "车牌号最长是8位";
            } else if (common.tools.getByteLength(licensePlateNo) < 7) {
                msg = "车牌号最小长度是7位";
            } else {
                flag = true;
            }
            return {flag: flag, msg: msg};
        },

        /**
         * 对于车主姓名的验证
         * @param owner
         * @returns {{flag: boolean, msg: string}}
         */
        validateName: function (owner) {
            var flag = false, msg = "";
            if (common.validations.isEmpty(owner)) {
                msg = "请输入姓名";
            } else if (common.tools.getByteLength(owner) < 2) {
                msg = "车主姓名的长度为2~4个汉字或者不少于4个英文字母";
            } else if (common.tools.getByteLength(owner) > 10) {
                msg = "车主姓名不能多于10个汉字或20个英文字母";
            } else {
                flag = true;
            }
            return {flag: flag, msg: msg};
        },

        /**
         * 对于车架号的验证
         * @param vinNo
         * @returns {{flag: boolean, msg: string}}
         */
        validateVinNo: function (vinNo) {
            vinNo = vinNo.toUpperCase();
            var flag = false, msg = '';
            if (common.validations.isEmpty(vinNo)) {
                msg = '车辆识别代号不能为空';
            } else if (common.validations.isSpecialLetter(vinNo)) {
                msg = '车辆识别代号不可以包含 “*”、“.”“-”特殊字符';
            } else if (common.validations.isChineseChar(vinNo)) {
                msg = "车辆识别代号不可以包含汉字";
            } else if (/[IOQ]/.test(vinNo)) {
                msg += "车辆识别代号不可以包含字母" + (/[IOQ]/.exec(vinNo)[0]);
            } else if (vinNo.length != 17) {
                msg = "车辆识别代号必须是17位";
            } else {
                flag = true;
            }
            return {flag: flag, msg: msg};
        },

        /**
         * 对于车架号的验证
         * @param engineNo
         * @returns {{flag: boolean, msg: string}}
         */
        validateEngineNo: function (engineNo) {
            engineNo = engineNo.toUpperCase();
            var flag = false, msg = '';
            if (common.validations.isEmpty(engineNo)) {
                msg = '发动机号不能为空';
            } else if (!/[0-9a-zA-Z*、._ ]{5,}/.test(engineNo)) {
                msg = '发动机号仅可以包含 “*”、“.”“-”特殊字符';
            } else if (common.validations.isChineseChar(engineNo)) {
                msg = "发动机号不可以包含汉字";
            } else if (engineNo.length < 5) {
                msg = "发动机号至少是5位";
            } else {
                flag = true;
            }
            return {flag: flag, msg: msg};
        },

        /**
         * 校验身份证号
         * @param num
         * @returns {*}
         */
        isIdCardNo: function (num) {
            num = num.toUpperCase();
            //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X。
            if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
                return false;
            }
            //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
            var arrInt = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
            var arrCh = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
            //下面分别分析出生日期和校验位
            var len, re, arrSplit, dtmBirth, bGoodDay, nTemp = 0;
            len = num.length;
            if (len == 15) {
                re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
                arrSplit = num.match(re);

                //检查生日日期是否正确
                dtmBirth = new Date('19' + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
                if (!bGoodDay) {
                    return false;
                }
                else {
                    //将15位身份证转成18位
                    num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
                    for (var i = 0; i < 17; i++) {
                        nTemp += num.substr(i, 1) * arrInt[i];
                    }
                    num += arrCh[nTemp % 11];
                    return num;
                }
            } else if (len == 18) {
                re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
                arrSplit = num.match(re);

                //检查生日日期是否正确
                dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
                if (!bGoodDay) {
                    return false;
                } else {
                    //检验18位身份证的校验码是否正确。
                    //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
                    var valNum;
                    for (var j = 0; j < 17; j++) {
                        nTemp += num.substr(j, 1) * arrInt[j];
                    }
                    valNum = arrCh[nTemp % 11];
                    if (valNum != num.substr(17, 1)) {
                        return false;
                    }
                    return num;
                }
            }
            return false;
        },
        /**
         * 校验QQ号
         * @param num
         * @returns {*}
         */
        isQQ: function (str) {
            var reg = /^[1-9][0-9]{4,9}$/;
            return reg.test(str);
        },

        /**
         * 验证零或正整数
         * @param str
         * @returns {*}
         */
        isZeroAndPosNumber: function (str) {
            var reg = /^[0-9]*$/;
            return reg.test(str);
        }
    },
    permission: {
        /**
         * 获取用户缓存cookie权限
         * @returns {Array}
         */
        getPermissionCodeArray: function () {
            var array = new Array();
            var permissionCode = cookie.getValue("odc_permission_code");
            if (!common.isEmpty(permissionCode)) {
                array = permissionCode.split(",");
            }
            return array;
        },
        /**
         * 是否有call权限
         * @returns {boolean}
         */
        isAbleCall: function () {
            return cookie.getValue("able_call") == "true"? true:false;
        },
        /**
         * 是否有permissionCode权限
         * @param permissionCode
         * @returns {boolean}
         */
        hasPermission: function (permissionCode) {
            var permissions = common.permission.getPermissionCodeArray();
            return permissions.indexOf(permissionCode) > -1;
        },
        /**
         * 用户权限校验
         * @param funPermission
         * @returns {boolean}
         */
        validUserPermission: function (funPermission) {
            var permissions = common.permission.getPermissionCodeArray();
            if (permissions.indexOf(funPermission) > -1) {
                return true;
            } else {
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(false, "对不起，您没有权限执行此操作！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
        },
        /**
         * 用户权限校验
         * @param funPermission
         * @returns {boolean}
         */
        validUserPermissionForSecondPup: function (funPermission) {
            var permissions = common.permission.getPermissionCodeArray();
            if (permissions.indexOf(funPermission) > -1) {
                return true;
            } else {
                popup.mould.popTipsMould(false, "对不起，您没有权限执行此操作！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            }
        }
    },
    /**
     * 公用的工具
     */
    tools: {
        /**
         * 获取字符串长度，中文长度为2，英文为1
         * @param obj
         * @returns {number}
         */
        getLength: function (obj) {
            var l = 0;
            var a = obj.split("");
            for (var i = 0; i < a.length; i++) {
                if (a[i].charCodeAt(0) < 299) {
                    l++;
                } else {
                    l += 2;
                }
            }
            return l;
        },
        /**
         * 将空与undefined转为""
         * @param obj
         * @returns {*}
         */
        checkToEmpty: function (obj) {
            return (typeof(obj) != "undefined" && obj) ? obj : "";
        },
        /**
         * 字符串长度大于某值后截取到length处，后加......
         * @param str
         * @param length
         * @returns {*}
         */
        getFormatComment: function (str, length) {
            if (!str) {
                return "";
            }

            if (str.length > length) {
                return str.substring(0, length) + "……";
            } else {
                return str;
            }
        },
        /**
         * 适应table中备注字数固定加TITLE的需求
         * @param str
         * @param length
         * @returns {string}
         */
        getCommentMould: function (str, length) {
            return "<span title=\"" + str + "\">" + this.getFormatComment(str, length) + "</span>";
        },
        /**
         * 判断的字节长度
         * @param text
         * @returns {number}
         */
        getByteLength: function (text) {
            return text.length;
        },
        /**
         * 数字格式化 s为需转化数字，n为要预留的小数位
         * @param s
         * @param n
         * @returns {string}
         */
        formatMoney: function (s, n) {
            if (!s) {
                return "0.00";
            }
            n = n > 0 && n <= 20 ? n : 2;
            s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
            var l = s.split(".")[0].split("").reverse(),
                r = s.split(".")[1];
            t = "";
            for (i = 0; i < l.length; i++) {
                t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "" : "");
            }
            return t.split("").reverse().join("") + "." + r;
        },
        /**
         * 日期格式化
         * 对Date的扩展，将 Date 转化为指定格式的String
         * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
         * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
         * 例子：
         * (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
         * (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
         */
        formatDate: Date.prototype.Format = function (date, fmt, add) {
            if (add) {
                date.setDate(date.getDate() + add);
            }
            var o = {
                "M+": date.getMonth() + 1,                 //月份
                "d+": date.getDate(),                    //日
                "h+": date.getHours(),                   //小时
                "m+": date.getMinutes(),                 //分
                "s+": date.getSeconds(),                 //秒
                "q+": Math.floor((date.getMonth() + 3) / 3), //季度
                "S": date.getMilliseconds()             //毫秒
            };
            if (/(y+)/.test(fmt))
                fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        },
        addDays: function (date, days) {
            var nd = new Date(date);
            nd = nd.valueOf();
            nd = nd + days * 24 * 60 * 60 * 1000;
            nd = new Date(nd);
            var y = nd.getFullYear();
            var m = nd.getMonth() + 1;
            var d = nd.getDate();
            if (m <= 9) m = "0" + m;
            if (d <= 9) d = "0" + d;
            var cdate = y + "-" + m + "-" + d;
            return cdate;
        },
        DateDiff: function (d1, d2) {
            var day = 24 * 60 * 60 * 1000;
            try {
                var dateArr = d1.split("-");
                var checkDate = new Date();
                checkDate.setFullYear(dateArr[0], dateArr[1] - 1, dateArr[2]);
                var checkTime = checkDate.getTime();

                var dateArr2 = d2.split("-");
                var checkDate2 = new Date();
                checkDate2.setFullYear(dateArr2[0], dateArr2[1] - 1, dateArr2[2]);
                var checkTime2 = checkDate2.getTime();

                var cha = (checkTime - checkTime2) / day;
                return cha;
            } catch (e) {
                return false;
            }
        },


        /**
         * 跳至页面顶端
         */
        scrollToTop: function () {
            window.parent.scrollTo(0, 0);
        },
        /**
         * 根据弹出层位置获取popInput dom obj
         * @param position 位置
         * @param isParent 是否在父页面， true(当前在父页面) false(当前在iframe页面)
         * @returns {*}
         */
        getPopInputDom: function (position, isParent) {
            switch (position) {
                case popup.mould.first:
                    return isParent ? $("#popover_normal_input") : window.parent.$("#popover_normal_input");
                case popup.mould.second:
                    return isParent ? $("#popover_normal_input_second") : window.parent.$("#popover_normal_input_second");
                default:
                    return "";
            }
        },
        /**
         * dom元素去除左右空格
         * @param dom
         */
        doDomTrimOnBlur: function (dom) {
            $(dom).val($.trim($(dom).val()));
        },
        /**
         * 日期比较
         * @param startdate
         * @param enddate
         * @returns {boolean}
         */
        dateCompare: function (startdate, enddate) {
            var arr = startdate.split("-");
            var start = new Date(arr[0], arr[1], arr[2]);
            var starttimes = start.getTime();

            var arrs = enddate.split("-");
            var end = new Date(arrs[0], arrs[1], arrs[2]);
            var endtimes = end.getTime();
            if (starttimes >= endtimes) {
                return false;
            }
            else {
                return true;
            }
        },
        /**
         * 精确到时分秒的日期比较
         * @param startTime
         * @param endTime
         * @returns {number}
         * number>0 endTime>startTime
         * number=0 endTime=startTime
         * number<0 endTime<startTime
         */
        dateTimeCompare: function (startTime, endTime) {
            var beginTimes = startTime.substring(0, 10).split('-');
            var endTimes = endTime.substring(0, 10).split('-');
            startTime = beginTimes[1] + '/' + beginTimes[2] + '/' + beginTimes[0] + ' ' + startTime.substring(10, 19);
            endTime = endTimes[1] + '/' + endTimes[2] + '/' + endTimes[0] + ' ' + endTime.substring(10, 19);
            return (Date.parse(endTime) - Date.parse(startTime)) / 3600 / 1000;
        },
        /**
         * 只能输入数字及小数点
         * @param domInput
         */
        setDomNumAction: function (domInput) {
            $(domInput).css("ime-mode", "disabled");
            $(domInput).bind("keypress", function (e) {
                var code = (e.keyCode ? e.keyCode : e.which);  //兼容火狐 IE
                return code >= 48 && code <= 57 || code == 46;
            });
            $(domInput).bind("blur", function () {
                if (this.value.lastIndexOf(".") == (this.value.length - 1)) {
                    this.value = this.value.substr(0, this.value.length - 1);
                } else if (isNaN(this.value)) {
                    this.value = " ";
                }
            });
            $(domInput).bind("paste", function () {
                var s = clipboardData.getData('text');
                if (!/\D/.test(s));
                this.value = s.replace(/^0*/, '');
                return false;
            });
            $(domInput).bind("dragenter", function () {
                return false;
            });
            $(domInput).bind("keyup", function () {
                this.value = this.value.replace(/[^\d.]/g, "");
                //必须保证第一个为数字而不是.
                this.value = this.value.replace(/^\./g, "");
                //保证只有出现一个.而没有多个.
                this.value = this.value.replace(/\.{2,}/g, ".");
                //保证.只出现一次，而不能出现两次以上
                this.value = this.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            });
        },
        /**
         * 下拉框readonly
         * @param domSelect
         */
        setSelectReadonly: function (domSelect) {
            domSelect.attr("readonly", true);
            domSelect.unbind("click").bind({
                click: function () {
                    $(this).blur();
                },
            });
        },
        /**
         * 下拉框readonly
         * @param domSelect
         */
        unsetSelectReadonly: function (domSelect) {
            domSelect.attr("readonly", false);
            domSelect.unbind("click");
        },
        strToObj:function(str){
            str = str.replace(/&/g,"','");
            str = str.replace(/=/g,"':'");
            str = "({'"+str +"'})";
            obj = eval(str);
            return obj;
         }
    }
};

Array.prototype.indexOf = function (val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};

Array.prototype.remove = function (val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

Array.prototype.remove = function (s) {
    for (var i = 0; i < this.length; i++) {
        if (s == this[i])
            this.splice(i, 1);
    }
}

/*
 * 自定义Map和一些常用方法
 */
function Map() {
    /* 存放键的数组(遍历用到) */
    this.keys = new Array();
    /* 存放数据 */
    this.data = new Object();

    /*
     * 放入一个键值对
     * @param {String} key
     * @param {Object} value
     */
    this.put = function (key, value) {
        if (this.data[key] == null) {
            this.keys.push(key);
        }
        this.data[key] = value;
    };

    /*
     * 获取某键对应的值
     * @param {String} key
     * @return {Object} value
     */
    this.get = function (key) {
        return this.data[key];
    };

    /*
     * 遍历Map,执行处理函数
     * @param {Function} 回调函数 function(key,value,index){..}
     */
    this.each = function (fn) {
        if (typeof fn != 'function') {
            return;
        }
        var len = this.keys.length;
        for (var i = 0; i < len; i++) {
            var k = this.keys[i];
            fn(k, this.data[k], i);
        }
    };

    /*
     * 删除一个键值对
     * @param {String} key
     */
    this.remove = function (key) {
        this.keys.remove(key);
        this.data[key] = null;
    };

    /*
     * 判断Map是否为空
     */
    this.isEmpty = function () {
        return this.keys.length == 0;
    };

    /*
     * 获取键值对数量
     */
    this.size = function () {
        return this.keys.length;
    };

    /*
     * 清空Map
     */
    this.clear = function () {
        var len = this.keys.length;
        for (var i = 0; i < len; i++) {
            this.data[this.keys[i]] = null;
        }
        this.keys.length = 0;
    }

}

var Action = function () {
    /* 保存 */
    Action.prototype.save = function () {
    };
    /* 列表 */
    Action.prototype.list = function () {
    };
    /* 更新 */
    Action.prototype.update = function () {
    };
    /* 删除 */
    Action.prototype.delete = function () {
    };
    /* 清空form */
    Action.prototype.clearForm = function (form) {
        form[0].reset();
    };
    /* 编辑 */
    Action.prototype.edit = function () {
    };
}

var Properties = function (currentPage, keyword) {
    this.currentPage = currentPage;
    this.pageSize = 20;
    this.visiblePages = 10;
    this.keyword = keyword;
}

/**
 * 限制输入框只能输入数字(JQuery插件)
 *
 * @example $("#amount").numeral()
 *
 * @example $("#amount").numeral(4) or $("#amount").numeral({'scale': 4})
 *
 * @example $(".x-amount").numeral()
 **/
$.fn.numeral = function () {
    var args = arguments;
    var json = typeof(args[0]) == "object";
    var scale = json ? args[0].scale : args[0];
    scale = scale || 0;
    $(this).css("ime-mode", "disabled");
    var keys = new Array(8, 9, 35, 36, 37, 38, 39, 40, 46);
    this.bind("keydown", function (e) {
        e = window.event || e;
        var code = e.which || e.keyCode;
        if (e.shiftKey) {
            return false;
        }
        var idx = Array.indexOf(keys, code);
        if (idx != -1) {
            return true;
        }
        var value = this.value;
        if (code == 190 || code == 110) {
            if (scale == 0 || value.indexOf(".") != -1) {
                return false;
            }
            return true;
        } else {
            if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) {
                if (scale > 0 && value.indexOf(".") != -1) {
                    var reg = new RegExp("^[0-9]+(\.[0-9]{0," + (scale - 1) + "})?$");
                    var selText = getSelection();
                    if (selText != value && !reg.test(value)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    });
    this.bind("blur", function () {
        if (this.value.lastIndexOf(".") == (this.value.length - 1)) {
            this.value = this.value.substr(0, this.value.length - 1);
        } else if (isNaN(this.value)) {
            this.value = "";
        } else {
            var value = this.value;
            if (scale > 0 && value.indexOf(".") != -1) {
                var reg = new RegExp("^[0-9]+(\.[0-9]{0," + scale + "})?$");
                if (!reg.test(value)) {
                    this.value = format(value, scale);
                }
            }
        }
    });
    this.bind("paste", function () {
        var s = window.clipboardData.getData('text');
        if (!/\D/.test(s));
        value = s.replace(/^0*/, '');
        return false;
    });
    this.bind("dragenter", function () {
        return false;
    });
    var format = function (value, scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    };
    var getSelection = function () {
        if (window.getSelection) {
            return window.getSelection();
        }
        if (document.selection) {
            return document.selection.createRange().text;
        }
        return "";
    };
    Array.indexOf = function (array, value) {
        for (var i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }
};


