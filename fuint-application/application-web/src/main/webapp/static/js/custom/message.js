/**
 * Created by hanxiaoqiang on 16/7/4.
 */

/**
 * 错误提示框
 *
 * message: 错误提示框
 */
(function ($) {
    $.error = function (message) {
        layer.alert(message, {
            icon: 2
        });
    };
})(jQuery);

/**
 * 成功提示框
 * message: 成功提示框
 */
(function ($) {
    $.success = function (message) {
        layer.alert(message, {
            icon: 1
        });
    };
})(jQuery);