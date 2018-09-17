/**
 * Created by wangfei on 2015/4/22.
 */
var common = {
    /* ajax请求 */
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
                if (!common.sessionTimeOut(xhr) && !common.accessDenied(xhr)) {
                    console.log(xhr.responseText)
                    callbackMethod_error(xhr);
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
            popup.mould.popTipsMould("会话过期，请重新登录！", popup.mould.first, popup.mould.warning, "重新登录", "57%",
                function () {
                    window.location.href = xhr.getResponseHeader("loginPath");
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
            popup.mould.popTipsMould("对不起，您没有权限执行此操作！", popup.mould.first, popup.mould.warning, null, "57%", null);
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
    permission: {
        /**
         * 获取用户缓存cookie权限
         * @returns {Array}
         */
        getPermissionCodeArray: function () {
            var array = new Array();
            var permissionCode = cookie.getValue("opc_permission_code");
            if (!common.isEmpty(permissionCode)) {
                array = permissionCode.split(",");
            }
            return array;
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
                popup.mask.hideAllMask();
                popup.mould.popTipsMould("对不起，您没有权限执行此操作！", popup.mould.first, popup.mould.warning, "", "57%", null);
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
                popup.mould.popTipsMould("对不起，您没有权限执行此操作！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            }
        },
        validUserHasPermission: function (funPermission) {
            var permissions = common.permission.getPermissionCodeArray();
            if (permissions.indexOf(funPermission) > -1) {
                return true;
            } else {
                return false;
            }
        }
    },
    /* 空值转换"" */
    checkToEmpty: function (obj) {
        if (obj) {
            return obj;
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

    /* 是否手机号 */
    isMobile: function (str) {
        return this.isValidatePattern(str, /^(13|14|15|16|17|18|19)[0-9]{9}$/);
    },

    /* 是否手机号 */
    isTelphone: function (str) {
        return this.isValidatePattern(str, /^((13|14|15|16|17|18|19)[0-9]{9})|(0(([1-9]\d)|([3-9]\d{2}))(-?)((\d{7})|(\d{8})))$/);
    },

    /* 身份证验证 */
    isIdCardNo: function (num) {
        num = num.toUpperCase();
        //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X。
        if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
            //alert('输入的身份证号长度不对，或者号码不符合规定！\n15位号码应全为数字，18位号码末位可以为数字或X。');
            return false;
        }
        //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
        //下面分别分析出生日期和校验位
        var len, re;
        len = num.length;
        if (len == 15) {
            re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
            var arrSplit = num.match(re);

            //检查生日日期是否正确
            var dtmBirth = new Date('19' + arrSplit[2] + '/' + arrSplit[3] + '/' + arrSplit[4]);
            var bGoodDay;
            bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
            if (!bGoodDay) {
                //alert('输入的身份证号里出生日期不对！');
                return false;
            }
            else {
                //将15位身份证转成18位
                //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
                var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
                var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
                var nTemp = 0, i;
                num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
                for (i = 0; i < 17; i++) {
                    nTemp += num.substr(i, 1) * arrInt[i];
                }
                num += arrCh[nTemp % 11];
                return num;
            }
        }
        if (len == 18) {
            re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
            var arrSplit = num.match(re);

            //检查生日日期是否正确
            var dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
            var bGoodDay;
            bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
            if (!bGoodDay) {
                //alert(dtmBirth.getYear());
                //(arrSplit[2]);
                //alert('输入的身份证号里出生日期不对！');
                return false;
            }
            else {
                //检验18位身份证的校验码是否正确。
                //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
                var valnum;
                var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
                var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
                var nTemp = 0, i;
                for (i = 0; i < 17; i++) {
                    nTemp += num.substr(i, 1) * arrInt[i];
                }
                valnum = arrCh[nTemp % 11];
                if (valnum != num.substr(17, 1)) {
                    //alert('18位身份证的校验码不正确！应该为：' + valnum);
                    return false;
                }
                return num;
            }
        }
        return false;
    },

    /* 验证车牌号 */
    validateLicenseNo: function (_licensePlateNo) {
        var flag = true;
        var _length = _licensePlateNo.length;
        var _text = _licensePlateNo;
        if (_length > 8) {//长度少于7或者大于8
            flag = false;
        }

        var first_letter = _text.toUpperCase().substring(0, 1);
        if (first_letter == "B") {//车牌号首字母不可以B开头
            flag = false;
        }

        var first_letter_1 = _text.substring(0, 1);
        if (!this.isChineseChar(first_letter_1)) {
            return false;
        }

        var first_letter_2 = _text.substring(1, 2);
        var re = /^[a-zA-Z][a-zA-Z0-9]*$/;
        if (!re.test($.trim(first_letter_2))) {//车牌号必须为字母或数字且首字符必须是字母
            flag = false;
        }

        return flag;
    },

    //对于车主姓名的验证
    validateName: function (name) {
        var flag = true;
        var chineseChar = this.isChineseChar(name);
        if (chineseChar) {//输入的是中文字符
            var _chineseCharlength = name.length;
            if (_chineseCharlength < 2 || _chineseCharlength > 4) {//请至少输入2个汉字 最多只可以输入4汉字
                flag = false;
            }
        } else {//输入非中文字符
            var _englishCharlength = name.length;
            if (_englishCharlength < 4 || _englishCharlength > 20) {//车主姓名的长度为2~4个汉字或者不少于4个英文字母 英文字母最多输入20个字符
                flag = false;
            }
        }

        return flag;
    },

    //对于车架号的验证
    validateVinNo: function (_vinNo_text) {
        var flag = true;
        var _upper_text = _vinNo_text.toUpperCase();
        //车辆识别代码不可以包含字符I O Q
        if (_upper_text.indexOf("I") > 0 || _upper_text.indexOf("O") > 0 || _upper_text.indexOf("Q") > 0) {
            flag = false;
        }
        //只能是字母或数字
        var re = /[a-zA-Z0-9]*$/;
        if (!re.test($.trim(_vinNo_text))) {
            flag = false;
        }
        //车辆识别代码必须是17位
        if (_vinNo_text.length != 17) {
            flag = false;
        }

        return flag;
    },

    //验证发动机号
    validateEngineNo: function (engine_text) {
        var flag = true;
        //验证特殊符号 * . - 除外
        var vkeyWords = /[\':;?~`!@#$%^&+=_{}\[\]\<\>\(\),]/;
        if (vkeyWords.test(engine_text)) {
            flag = false;
        }

        if (common.isChineseChar(engine_text)) {
            flag = false;
        }

        if (engine_text.length < 6) {
            flag = false;
        }

        return flag;
    },

    /**
     * 验证Excel格式/是否为空
     * @param codeFile  $("#codeFile").val() 文件的value
     * @return {string} 返回报错信息 无错返""
     */
    validateExcel: function (codeFile) {
        var exceltype = codeFile.substring(codeFile.lastIndexOf('.') + 1).toLowerCase();
        if (common.isEmpty(codeFile)) return "请选择要上传的文件！";
        if (!(exceltype == "xls" || exceltype == "xlsx")) return "格式必须为excel 2003或者2007的一种！";
        return "";
    },
    /**
     * 验证Excel格式/是否为空
     * @param url               跳转url
     * @param form              form元素
     * @param codeFile {string} file元素value
     * @param callback          成功后回调方法
     */
    excelImport: function (url, form, codeFile, callback) {
        var validateStr = common.validateExcel(codeFile);
        if (!common.isEmpty(validateStr)) {
            layer.alert(validateStr);
            return false;
        }
        var options = {
            url: url,
            async: false,
            type: "post",
            dataType: "text",
            success: function (responseStr) {
                if (responseStr == 'success') {
                    $(".file-input").val(null);
                    layer.msg("上传成功！", {time: 1000});
                    if (callback) {
                        callback(responseStr);
                    }
                } else {
                    $(".file-input").val(null);
                    layer.alert("上传失败！", {time: 100000, icon: 7});
                }
            },
            error: function (responseStr) {
                layer.alert("上传失败！", {time: 100000, icon: 3});
            }
        };
        form.ajaxSubmit(options);
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
        return str == null || $.trim(str) == "";
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
        return str.match(/([a-z])+/) && str.match(/([0-9])+/) && str.match(/([A-Z])+/);
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

    /* 验证纯数字 */
    isPureNumber: function (str) {
        return this.isValidatePattern(str, /^\d+?$/);
    },

    /* 验证日期大小 */
    checkDate: function (date1, date2) {
        var firstDate = date1;
        var secondDate = date2;
        //如果是字符串转换为日期型
        if (typeof date1 == 'string') {
            firstDate = new Date(date1);
        }
        if (typeof date2 == 'string') {
            secondDate = new Date(date2);
        }
        return firstDate >= secondDate;
    },

    checkUrl: function (urlString) {
        if (urlString != "") {
            var reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
            return reg.test(urlString)
        }
    },


    /* 验证日期时间间隔 */
    dateDiff: function (startDate, endDate, interval) {
        var dtStart = startDate;
        var dtEnd = endDate;
        //如果是字符串转换为日期型
        if (typeof startDate == 'string') {
            dtStart = new Date(startDate);
        }
        if (typeof endDate == 'string') {
            dtEnd = new Date(endDate);
        }

        switch (interval) {
            case 'second' :
                return parseInt((dtEnd - dtStart) / 1000);
            case 'minute' :
                return parseInt((dtEnd - dtStart) / 60000);
            case 'hour' :
                return parseInt((dtEnd - dtStart) / 3600000);
            case 'day' :
                return parseInt((dtEnd - dtStart) / 86400000);
            case 'week' :
                return parseInt((dtEnd - dtStart) / (86400000 * 7));
            case 'month' :
                return (dtEnd.getMonth() + 1) + ((dtEnd.getFullYear() - dtStart.getFullYear()) * 12) - (dtStart.getMonth() + 1);
            case 'year' :
                return dtEnd.getFullYear() - dtStart.getFullYear();
        }
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
    formatDate: Date.prototype.Format = function (date, fmt) {
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
        if (r != null) return unescape(r[2]);
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
            if (obj == "ROLE_INTERNAL_USER_ADMIN") {
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
            if (obj == "ROLE_INTERNAL_USER_CUSTOMER") {
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
            if (obj == "ROLE_INTERNAL_USER_INTERNAL") {
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
            if (obj == "ROLE_INTERNAL_USER_STATUS_CHANGE") {
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
            if (obj == "ROLE_INTERNAL_USER_CPS") {
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
            if (obj == "ROLE_INTERNAL_USER_VIP") {
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
            if (obj == "ROLE_INTERNAL_USER_INPUT") {
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
        window.scrollTo(0, 0);
    },
    /*只允许输入数字和.*/
    numeral: function (e) {
        var obj = e.srcElement || e.target;
        var dot = obj.value.indexOf(".");//alert(e.which);
        var key = e.keyCode || e.which;
        if (key == 8 || key == 9 || key == 46 || (key >= 37 && key <= 40))//这里为了兼容Firefox的backspace,tab,del,方向键
            return true;
        if (key <= 57 && key >= 48) { //数字
            if (dot == -1)//没有小数点
                return true;
            else if (obj.value.length <= dot + 1)//两位小数
                return true;
        } else if ((key == 46) && dot == -1) {//小数点
            return true;
        }
        return false;
    },
    /*class为className的input标签是否都有内容.*/
    checkInput: function (className) {
        var result = true;
        $("." + className).each(function () {
            if (common.isEmpty(this.value)) {
                result = false;
            }
        });
        return result;
    },
}

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
            this.remove(this.keys[i]);
        }
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
        if (!/\D/.test(s)) ;
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

(function () {
    var resource_char = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var resource_number = '0123456789';

    $.generateRand = function (length, resource) {
        var len = length || 8

        var s = '';
        for (var i = 0; i < len; i++) {
            s += resource.charAt(
                Math.ceil(Math.random() * 1000) % resource.length
            )
        }
        return s
    }

    $.RandCode = function () {
        return $.generateRand(3, resource_char) + $.generateRand(5, resource_number)
    }
    $.close = function () {
        window.opener = null;
        window.open('', '_top');
        window.top.close();
    }

    if (window.layer) {
        layer.success = function (content, callBack, btnText) {
            layer.confirm('', {
                title: false,
                closeBtn: 0,
                btnAlign: 'c',
                btn: [btnText || '确定'],
                content: '<div class="col-md-12 text-center"><i class="glyphicon glyphicon-ok-circle text-success" style="font-size: 80px;"></i></div>' +
                '<div class="col-md-12 text-center" style="padding:20px 0;"><span style="font-size:30px;">操作成功</span></div>' +
                '<div class="col-md-12 text-center" style="padding:30px 0;"><span>' + (content || '') + '</span></div>'
            }, function (index) {
                layer.close(index)
                if (typeof callBack === 'function') {
                    callBack.apply(this, Array.prototype.slice.call(arguments))
                }
            })
        }

        layer.error = function (content, callBack, btnText) {
            layer.confirm('', {
                title: false,
                closeBtn: 0,
                btnAlign: 'c',
                btn: [btnText || '确定'],
                content: '<div class="col-md-12 text-center"><i class="glyphicon glyphicon-remove-circle text-danger" style="font-size: 80px;"></i></div>' +
                '<div class="col-md-12 text-center" style="padding:20px 0;"><span style="font-size:30px;">提交失败</span><br><span style="line-height: 50px;">请核对并修改以下信息后，再重新提交。</span></div>' +
                '<div class="col-md-12 text-center" style="padding:30px 0;"><span>' + (content ? '<i class="glyphicon glyphicon-remove-circle text-danger"></i>' + content : '') + '</span></div>'
            }, function (index) {
                layer.close(index)
                if (typeof callBack === 'function') {
                    callBack.apply(this, Array.prototype.slice.call(arguments))
                }
            })
        }
    }
}())
