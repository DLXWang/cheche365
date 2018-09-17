/**
 * Created by wangfei on 2015/9/9.
 */
var cookie = {
    /**
     * set cookie value and override if existed
     * @param name
     * @param value
     */
    setValue: function(name, value) {
        $.cookie(name, value);
    },
    /**
     * set cookie value and expires, override if existed
     * @param name
     * @param value
     * @param expires
     */
    setValueAndExpires: function(name, value, expires) {
        $.cookie(name, value, {expires:expires});
    },
    /**
     * get value form cookie by key name
     * @param name
     */
    getValue: function(name) {
        return $.cookie(name);
    },
    /**
     * delete cookie by key name
     * @param name
     */
    deleteCookie: function(name) {
        $.cookie(name, null);
    }

};
