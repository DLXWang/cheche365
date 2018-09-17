/**
 * Created by wangfei on 2015/4/22.
 */
var common = {
    ajax: {
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
        getByAjax : function(async, methodType, returnType, url, data, callbackMethod_success, callbackMethod_error){
            $.ajax({
                async: async,
                type: methodType,
                dataType: returnType,
                url: url,
                data: data,
                success: function(data){
                    callbackMethod_success(data);
                },
                error: function(xhr, textStatus, errorThrown){
                    if (!common.ajax.sessionTimeOut(xhr) && !common.ajax.accessDenied(xhr) && !common.ajax.noPermissionLogin(xhr)) {
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
        sessionTimeOut: function(xhr) {
            if(xhr.getResponseHeader("sessionstatus") == "timeOut") {
                popup.mask.hideAllMask();
                popup.mould.popTipsMould("会话过期，请重新登录！", popup.mould.first, popup.mould.warning, "重新登录", "57%",
                    function() {window.location.href = xhr.getResponseHeader("loginPath");}
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
        accessDenied: function(xhr) {
            if(xhr.getResponseHeader("sessionstatus") == "accessDenied") {
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
        noPermissionLogin: function(xhr) {
            if(xhr.getResponseHeader("sessionstatus") == "noPermissionLogin") {
                popup.mask.hideAllMask();
                popup.mould.popTipsMould("你没有权限登录管理系统！", popup.mould.first, popup.mould.warning, null, "57%", null);
                return true;
            }
            return false;
        }
    },
    /**
     * 校验
     */
    validation: {
        /* 是否手机号 */
        isMobile : function(str){
            return this.isValidatePattern(str, /^(13|14|15|16|17|18|19)[0-9]{9}$/);
        },

        /* 是否固定电话 */
        isTelphone : function(str){
            return this.isValidatePattern(str, /^(0(([1-9]\d)|([3-9]\d{2}))(-?)((\d{7})|(\d{8})))$/);
        },

        /* 检测是否包含特殊字符 */
        isSpecialLetter : function(value) {
            var vkeyWords = /[\':;*?~`!@#$%^&+={}\[\]\<\>\(\),\.]/;
            if (vkeyWords.test(value)) {
                return true;
            } else {
                return false;
            }
        },

        /**
         * 字符串是否为空、undefined
         * @param obj
         * @returns {boolean}
         */
        isEmpty : function (obj) {
            return (typeof(obj) == "undefined" || !obj || $.trim(obj) == "");
        },

        /* 验证邮箱格式 */
        isEmail : function (str){
            return this.isValidatePattern(str, /^\w+@[a-z\d]+\.(com|cn|com.cn|net|org)$/);
        },

        /* 6-12为数字、字母或下划线 */
        isPassword : function (str){
            return this.isValidatePattern(str, /^\w{6,12}$/);
        },
        /* 必须包含大小写字母和数字 */
        isPasswordEx : function (str){
            return str.match(/([a-z])+/) && str.match(/([0-9])+/) && str.match(/([A-Z])+/)
        },

        /* 正则表达式校验 */
        isValidatePattern : function(value, pattern){
            var regex = pattern;
            return regex.test(value);
        }
    },
    /**
     * 工具
     */
    tools: {
        /**
         * 获取字符串长度，中文长度为2，英文为1
         * @param obj
         * @returns {number}
         */
        getLength : function(obj) {
            var l = 0;
            var a = obj.split("");
            for (var i = 0; i < a.length; i++) {
                if (a[i].charCodeAt(0) < 299) {
                    l++;
                } else {
                    l+=2;
                }
            }
            return l;
        },
        /**
         * 将空与undefined转为""
         * @param obj
         * @returns {*}
         */
        checkToEmpty: function(obj) {
            return (typeof(obj) != "undefined" && obj) ? obj : "";
        },
        /**
         * 页面移至页面顶端
         */
        scrollToTop: function() {
            window.scrollTo(0,0);
        },
        /**
         * 字符串长度大于某值后截取到length处，后加......
         * @param str
         * @param length
         * @returns {*}
         */
        getFormatComment : function (str, length) {
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
        getCommentMould: function(str, length) {
            return "<span title=\"" + str + "\">" + this.getFormatComment(str, length) + "</span>";
        },
        /**
         * 根据弹出层位置获取popInput dom obj
         * @param position 位置
         * @param isParent 是否在父页面， true(当前在父页面) false(当前在iframe页面)
         * @returns {*}
         */
        getPopInputDom: function(position, isParent) {
            switch (position) {
                case popup.mould.first:
                    return isParent ? $("#popover_normal_input") : window.parent.$("#popover_normal_input");
                case popup.mould.second:
                    return isParent ? $("#popover_normal_input_second") : window.parent.$("#popover_normal_input_second");
                default:
                    return "";
            }
        },
    },
    permission: {
        /**
         * 获取用户缓存cookie权限
         * @returns {Array}
         */
        getPermissionCodeArray: function(){
            var array = new Array();
            var permissionCode = cookie.getValue("adm_permission_code");
            if(!common.validation.isEmpty(permissionCode)){
                array = permissionCode.split(",");
            }
            return array;
        },
        /**
         * 用户权限校验
         * @param funPermission
         * @returns {boolean}
         */
        validUserPermission: function(funPermission) {
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
         * 是否为超级管理员
         * @returns {*|boolean}
         */
        isSuperMan: function() {
            var userName = cookie.getValue("login_user_email");
            return userName && userName == "superman@cheche365.com";
        }
    }
};

Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};

Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

Array.prototype.remove = function(s) {
    for (var i = 0; i < this.length; i++) {
        if (s == this[i])
            this.splice(i, 1);
    }
};

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
    this.put = function(key, value) {
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
    this.get = function(key) {
        return this.data[key];
    };

    /*
     * 遍历Map,执行处理函数
     * @param {Function} 回调函数 function(key,value,index){..}
     */
    this.each = function(fn){
        if(typeof fn != 'function'){
            return;
        }
        var len = this.keys.length;
        for (var i=0;i<len;i++) {
            var k = this.keys[i];
            fn(k,this.data[k],i);
        }
    };

    /*
     * 删除一个键值对
     * @param {String} key
     */
    this.remove = function(key) {
        this.keys.remove(key);
        this.data[key] = null;
    };

    /*
     * 判断Map是否为空
     */
    this.isEmpty = function() {
        return this.keys.length == 0;
    };

    /*
     * 获取键值对数量
     */
    this.size = function() {
        return this.keys.length;
    };

    /*
     * 清空Map
     */
    this.clear = function() {
        var len = this.keys.length;
        for (var i=0; i<len; i++) {
            this.remove(this.keys[i]);
        }
    }

}

var Properties = function(currentPage, keyword){
    this.currentPage = currentPage;
    this.pageSize = 20;
    this.visiblePages = 10;
    this.keyword = keyword;
};

/**
 * 限制输入框只能输入数字(JQuery插件)
 *
 * @example $("#amount").numeral()
 *
 * @example $("#amount").numeral(4) or $("#amount").numeral({'scale': 4})
 *
 * @example $(".x-amount").numeral()
 **/
$.fn.numeral = function() {
    var args = arguments;
    var json = typeof(args[0]) == "object";
    var scale = json ? args[0].scale : args[0];
    scale = scale || 0;
    $(this).css("ime-mode", "disabled");
    var keys = new Array(8, 9, 35, 36, 37, 38, 39, 40, 46);
    this.bind("keydown",function(e) {
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
            if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105))	{
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
    this.bind("blur", function() {
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
    this.bind("paste", function() {
        var s = window.clipboardData.getData('text');
        if (!/\D/.test(s));
        value = s.replace(/^0*/, '');
        return false;
    });
    this.bind("dragenter", function() {
        return false;
    });
    var format = function(value, scale){
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    };
    var getSelection = function(){
        if (window.getSelection) {
            return window.getSelection();
        }
        if (document.selection) {
            return document.selection.createRange().text;
        }
        return "";
    };
    Array.indexOf = function(array, value) {
        for (var i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }
};
