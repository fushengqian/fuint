/**
 * Created by zach
 */

/**
 * POST 请求
 *
 * @param actionUrl
 * @param resultArea
 */
function postData(actionUrl, resultArea) {
    postDataByParams(actionUrl, resultArea, '');
}

/**
 * 表单 POST 请求
 *
 * @param actionUrl
 * @param formname
 * @param resultArea
 */
function postDataByForm(actionUrl, resultArea, formname) {
    if ($.isBlank(formname)) {
        $.error('请求参数错误.');
        return;
    }
    postDataByParams(actionUrl, resultArea, $("#" + formname).serialize());
}

function cleanContent() {
    var areas = $('.panel-collapse');
    if (areas != null && areas.length > 0) {
        for (var i = 0; i < areas.length; i++) {
            $(areas[i]).html("");
        }
    }
}

function setNoDisplay(){
    var aLinks = $('.accordion-toggle');
    var areas = $('.panel-collapse');
    if (areas != null && areas.length > 0) {
        for (var i = 0; i < areas.length; i++) {
            $(aLinks[i]).addClass("collapsed");
            $(aLinks[i]).attr("aria-expanded",'false');
            $(areas[i]).removeClass("in");
            $(areas[i]).css("height", "0px");
        }
    }
}

/**
 * 表单 POST 请求 不刷新div区域
 * @param actionUrl URL
 * @param formname Form表单名称
 * @param obj form 的Element
 */
function postDataByFormAndCleanContent(actionUrl, formname) {
    if ($.isBlank(formname)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: $("#" + formname).serialize(),
            success: function (data) {
                cleanContent();
                $.close();
                $.success("操作成功");
                setNoDisplay();
            },
            error: function (msg) {
                $.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                //setNoDisplay();
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }

}


/**
 * 表单 POST 请求
 *
 * @param actionUrl
 * @param formname
 * @param resultArea
 */
function postDataByFormParams(actionUrl, resultArea, formname, params) {
    if ($.isBlank(formname)) {
        $.error('请求参数错误.');
        return;
    }
    var requestParams = $("#" + formname).serialize();
    if (!$.isBlank(params)) {
        requestParams = requestParams + "&params=" + params;
    }
    postDataByParams(actionUrl, resultArea, requestParams);
}


/**
 * 参数 POST 请求
 *
 * @param actionUrl 请求URL
 * @param params  请求参数
 * @param resultArea 结果显示区域
 */
function postDataByParams(actionUrl, resultArea, params) {
    if ($.isBlank(actionUrl) || $.isBlank(resultArea)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: params,
            success: function (data) {
                $('#' + resultArea).html("");
                $('#' + resultArea).html(data);
            },
            error: function (msg) {
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }
}

/**
 * GET 请求
 *
 * @param actionUrl
 * @param resultArea
 */
function getMenuData(actionUrl, resultArea,obj) {
    $(".sub-menu .item-menu").removeClass('a_menu');
    $(obj).addClass('a_menu');
    getDataByParams(actionUrl, resultArea, '');
}


/**
 * GET 请求
 *
 * @param actionUrl
 * @param resultArea
 */
function getData(actionUrl, resultArea) {
    getDataByParams(actionUrl, resultArea, '');
}

/**
 * GET 请求
 *
 * @param actionUrl
 * @param resultArea
 */
function getDataWithClean(actionUrl, resultArea) {
    if ($.isBlank(actionUrl) || $.isBlank(resultArea)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "GET",
            url: actionUrl,
            success: function (data) {
                cleanContent();
                $('#' + resultArea).html("");
                $('#' + resultArea).html(data);
                $.close();
            },
            error: function (msg) {
                cleanContent();
                $.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }
}

/**
 * 询问层:确定后发送get请求
 *
 * @param text 询问语
 * @param actionUrl
 * @param resultArea
 */
function getConfirmData(text, actionUrl, resultArea) {
    if ($.isBlank(text)) {
        $.error('参数错误.')
        return;
    }
    //询问框
    layer.confirm(text, {
        btn: ['确定', '关闭'] //按钮
    }, function () {
        getData(actionUrl, resultArea);
    }, function () {
        $.close();
    });
}

function getConfirmDataWithForm(text, actionUrl, resultArea, formname) {
    if ($.isBlank(text)) {
        $.error('参数错误.')
        return;
    }
    //询问框
    layer.confirm(text, {
        btn: ['确定', '关闭'] //按钮
    }, function () {
        getDataByForm(actionUrl, resultArea, formname);
    }, function () {
        $.close();
    });
}

/**
 * 询问层:确定后发送post请求
 *
 * @param text 询问语
 * @param actionUrl
 * @param resultArea
 */
function postConfirmData(text, actionUrl, resultArea, params) {
    if ($.isBlank(text)) {
        $.error('参数错误.')
        return;
    }
    //询问框
    layer.confirm(text, {
        btn: ['确定', '关闭'] //按钮
    }, function () {
        postDataByParams(actionUrl, resultArea, params);
    }, function () {
        $.close();
    });
}

/**
 * 表单 GET 请求
 *
 * @param actionUrl
 * @param formname
 * @param resultArea
 */
function getDataByForm(actionUrl, resultArea, formname) {
    if ($.isBlank(formname)) {
        $.error('请求参数错误.');
        return;
    }
    getDataByParams(actionUrl, resultArea, $("#" + formname).serialize());
}

/**
 * 参数 POST 请求
 *
 * @param actionUrl 请求URL
 * @param params  请求参数
 * @param resultArea 结果显示区域
 */
function getDataByParams(actionUrl, resultArea, params) {
    if ($.isBlank(actionUrl) || $.isBlank(resultArea)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "GET",
            url: actionUrl,
            data: params,
            success: function (data) {
                $('#' + resultArea).html("");
                $('#' + resultArea).html(data);
                //$.close();
            },
            error: function (msg) {
                //$.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }
}
/**
 * 表单 POST 请求 不刷新div区域
 * @param actionUrl URL
 * @param formname Form表单名称
 * @param obj form 的Element
 */
function postDataByParamsAndCleanContent(actionUrl, params) {
    if ($.isBlank(params)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: params,
            success: function (data) {
                cleanContent();
                $.close();
                $.success("操作成功");
                setNoDisplay();
            },
            error: function (msg) {
                $.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                //setNoDisplay();
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }

}

/**
 * 表单 POST 请求 不刷新div区域
 * @param actionUrl URL
 * @param formname Form表单名称
 * @param obj form 的Element
 */
function postDataByParamsAndCleanContentJson(actionUrl, params) {
    if ($.isBlank(params)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: params,
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                cleanContent();
                //$.close();
                $.success("操作成功");
                setNoDisplay();
            },
            error: function (msg) {
                //$.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                //setNoDisplay();
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }

}
/**
 * 表单 POST 请求 不刷新div区域
 * @param actionUrl URL
 * @param formname Form表单名称
 * @param obj form 的Element
 */
function postDataByParamsAndRedirect(actionUrl, params) {
    if ($.isBlank(params)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: params,
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                layer.alert("操作成功", function(){
                    var programName = location.pathname.substring(0,location.pathname.lastIndexOf("/"));
                    getMenuData(programName+data,'displayArea',this);
                });
            },
            error: function (msg) {
                $.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }

}
/**
 * 表单 POST 请求 不刷新div区域 关闭弹出窗口
 * @param actionUrl URL
 * @param formname Form表单名称
 * @param obj form 的Element
 */
function postDataByFormAndCloseLayer(actionUrl, formname) {
    if ($.isBlank(formname)) {
        $.error('请求参数错误.');
        return;
    }
    try {
        $.load();
        if (actionUrl.indexOf("?") >= 0) {
            actionUrl = actionUrl + "&s=" + Math.random();
        } else {
            actionUrl = actionUrl + "?s=" + Math.random();
        }
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: $("#" + formname).serialize(),
            success: function (data) {
                parent.$("#handle_status").val('1');
                parent.layer.closeAll('iframe');
                cleanContent();
                $.close();
                setNoDisplay();
            },
            error: function (msg) {
                parent.$("#handle_status").val('2');
                parent.layer.closeAll('iframe');
                $.close();
                if (msg.status == '555') {
                    $.error(msg.responseText + "(" + msg.statusText + ")");
                } else if (msg.status == '501') {
                    location.href = 'login';
                } else {
                    $.error("<h2>错误:</h2><p>" + msg.statusText + "(" + msg.status + ")</p>");
                }
                //setNoDisplay();
                return;
            }
        });
    } catch (e) {
        $.error('错误:' + e);
    }

}