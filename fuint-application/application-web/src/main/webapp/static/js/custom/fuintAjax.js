/**
 * 麻花自定义ajax
 */
(function ($) {
    var ajaxParam = {
        async: true,//默认异步
        cache: false,//不开启缓存
        dataType: 'json',//默认json格式
        timeout: 60000,//超时时间1分钟
        type: 'POST',
        url: '',
        shade: true,//默认开启loading
        showErrorMsg: true//默认提示系统错误
    };
    $.mh = $.extend({}, $.mh);
    $.mh.ajax = function (options) {
        options = $.extend({}, ajaxParam, options);
        if (!options.url || options.url.length == 0) {
            $.error("url不能为空");
            return;
        }
        //开启遮罩
        if (options.shade) {
            $.load();
        }
        var error = options.error;
        var success = options.success;

        options.error = function (msg) {
            $.close();
            if (msg.status == '555') {
                $.error(msg.responseText + "(" + msg.statusText + ")");
            } else if (msg.status == '501') {
                location.href = 'login';
            } else {
                $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
            }
            if (error) {
                error.call();
            }
            //setNoDisplay();
            return;
        };
        options.success = function (data) {
            layer.close();
            if (!data) {
                if (error) {
                    if (options.showErrorMsg) {
                        $.error("系统错误");
                    }
                    error.call(this, data);
                }
            }
            success.call(this, data);
            setNoDisplay();
        };
        $.ajax(options);
    }
})(jQuery);

