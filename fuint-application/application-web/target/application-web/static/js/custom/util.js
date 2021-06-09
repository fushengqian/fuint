/**
 * 是否为空
 */
(function ($) {
    $.isBlank = function (obj) {
        return (!obj || $.trim(obj) === "");
    };
})(jQuery);

/**
 * 加载控件
 */
(function ($) {
    $.load = function () {
        layer.load();
    }
})(jQuery);
/**
 * 关闭加载控件
 */
(function ($) {
    $.close = function () {
        layer.closeAll();
    }
})(jQuery);
/**
 * 重置控件
 */
(function ($) {
    $.reset = function (formId) {
        $("#" + formId).find("input[type='text']").each(function (index) {
            this.value = "";
        });
        $("#" + formId).find("select").each(function (index) {
            this.value = "";
        });
    }

})(jQuery);
/**
 * 获取tree选中的值
 */
(function ($) {
    $.selectedTreeNode = function () {
        var treeObj = $.fn.zTree.getZTreeObj("treeArea"),
            nodes = treeObj.getCheckedNodes(true),
            v = "";
        for (var i = 0; i < nodes.length; i++) {
            v += nodes[i].id + ",";
        }
        return v;
    }
})(jQuery);

function resetForm(formId) {
    $.reset(formId);
}
/**
 * 清空form中非button、summit、reset、hidden的input元素的值
 * */
function resetUserForm(formId) {
    $.resetForm(formId);
}
//初始化是否需要展示添加层级（满额优惠活动中的每满减活动不需要展示）
function setAddLevel() {
    if ($('select[name="subType"]').val() == '4') {
        $("#addLevelId").attr('style', 'display:none');
    } else {
        $("#addLevelId").removeAttr('style')
        $("#addLevelId").attr('style', 'display:block');
    }
}
/**
 * 重置控件
 */
(function ($) {
    $.resetForm = function (formId) {
        $(':input', '#' + formId).not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected');
    }

})(jQuery);

/**
 *form表单 数组 序列化成json字符串
 */
(function ($) {
    $.fn.serializeJson = function () {
        var jsonData1 = {};
        var serializeArray = this.serializeArray();
        // 先转换成{"id": ["12","14"], "name": ["aaa","bbb"], "pwd":["pwd1","pwd2"]}这种形式
        $(serializeArray).each(function () {
            if (jsonData1[this.name]) {
                if ($.isArray(jsonData1[this.name])) {
                    jsonData1[this.name].push(this.value);
                } else {
                    jsonData1[this.name] = [jsonData1[this.name], this.value];
                }
            } else {
                jsonData1[this.name] = this.value;
            }
        });
        // 再转成[{"id": "12", "name": "aaa", "pwd":"pwd1"},{"id": "14", "name": "bb", "pwd":"pwd2"}]的形式
        var vCount = 0;
        // 计算json内部的数组最大长度
        for (var item in jsonData1) {
            var tmp = $.isArray(jsonData1[item]) ? jsonData1[item].length : 1;
            vCount = (tmp > vCount) ? tmp : vCount;
        }

        if (vCount > 1) {
            var jsonData2 = new Array();
            for (var i = 0; i < vCount; i++) {
                var jsonObj = {};
                for (var item in jsonData1) {
                    jsonObj[item] = jsonData1[item][i];
                }
                jsonData2.push(jsonObj);
            }
            return JSON.stringify(jsonData2);
        } else {
            return "[" + JSON.stringify(jsonData1) + "]";
        }
    };
})(jQuery);
function clearContent(obj) {
    $(obj).find("input").each(function () {
        if ($(this).attr("type") != "hidden" || $(this).attr('name') == 'pic' || $(this).attr('name') == 'id') {
            $(this).val('');
        }
    });
    $(obj).find("textarea").each(function () {
        $(this).val('');
    });
    $(obj).find("img").each(function () {
        $(this).attr('src', '');
    });
}


/**
 * 校验结束时间与开始时间
 * @param  externalDivId 时间控件外层DIV的ID
 * @param  inputStartTimeName 开始时间的NAME值
 * @param  inputEndTimeName 结束时间的NAME值
 * add by chenggang 2016/9/21
 */

function checkStartAndEndTime(externalDivId, inputStartTimeName, inputEndTimeName) {
    try {
        var startTimeStr = $("#" + externalDivId).find("input[name='" + inputStartTimeName + "']").val();
        var endTimeStr = $("#" + externalDivId).find("input[name='" + inputEndTimeName + "']").val();
        var startTime = new Date(startTimeStr);
        var endTime = new Date(endTimeStr);
        if (startTime.getTime() >= endTime.getTime()) {
            layer.msg("开始时间不能大于结束时间");
            return false;
        }
    } catch (e) {
        console.log(e);
        return false;
    }
    return true;
};
/**
 *  判断输入的字符是否为整数.
 *  @param obj 当前对象
 */

function checkInteger(obj) {
    var str = $.trim($(obj).val());
    if (str.length != 0) {
        var reg = /^[0-9]*[1-9][0-9]*$/;
        if (!reg.test(str)) {
            $(obj).val('');
        }
    }
}

function checkDiscountInput(obj) {
    var str = $.trim($(obj).val());
    if (str.length != 0) {
        var reg = /^([0-9]+)|([0-9]+\.[0-9]+)$/;
        if (!reg.test(str)) {
            $(obj).val('');
        }
    }
}
/**
 * 校验
 * @param item
 * @returns {boolean}
 */
function isNonnegativeDecimal(item) {
    var obj = $(item).val();
    var result = false;
    if (obj != null && typeof (obj) != "undefined" && obj.length > 0) {
        var reg = /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]+))$/;
        result = reg.test(obj)
    }
    return result
}
/**
 *  判断输入的字符是否为Decimal.
 *  @param item 当前对象
 */
function isDecimal(item) {
    var obj = $(item);
    if (obj.length > 0) {
        if ($(obj).val() != null && typeof ($(obj).val()) != "undefined") {
            var str = $(obj).val().toString();
            if (str != "") {
                var pattern = '^-?[1-9]\\d*$|^-?0\\.\\d*$|^-?[1-9]\\d*\\.\\d*$';
                var reg = new RegExp(pattern, 'g');
                if (reg.test(str)) {
                    return true;
                } else {
                    if (str.match(/[^0-9\.-]/g) != null) {
                        if (str.match(/[^0-9\.-]/g).length > 0) {
                            str = str.replace(/[^0-9\.-]/g, '');
                            $(item).val(str);
                        }
                    }
                }
            }
        }
    }
    return false;
}
