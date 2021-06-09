/**
 * 检查 页面上带有 beginTimeClass 和 endTimeClass 样式的
 *
 * 日历空间, 时间是否有交集
 *
 * @returns {boolean}
 */
function checkDateIntersection() {
    var dateArray = new Array();
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    if (begins.length > ends.length) {
        $.error("请填写结束时间");
    }
    if (begins.length < ends.length) {
        $.error("请填写开始时间");
    }
    for (var i = 0; i < begins.length; i++) {
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
        dateArray.push(dateObj);
    }
    if (dateArray.length > 0) {
        for (var i = 0; i < dateArray.length; i++) {
            var item = dateArray[i];
            for (var k = 0; k < dateArray.length; k++) {
                if(i == k){
                    continue;
                }
                var next_item = dateArray[k];
                if (item.startTime >= next_item.startTime && item.startTime <= next_item.endTime) {
                    $.error("开始时间【" + item.startTimeStr + "】存在交集!");
                    return false;
                }
                if(item.endTime >= next_item.startTime && item.endTime <= next_item.endTime){
                    $.error("结束时间【" + item.endTimeStr + "】存在交集!");
                    return false;
                }
            }
        }
    }
    return true;
}


function checkDateArrayIntersection(begins,ends) {
    var dateArray = new Array();
    for (var i = 0; i < begins.length; i++) {
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
        dateArray.push(dateObj);
    }
    if (dateArray.length > 0) {
        for (var i = 0; i < dateArray.length; i++) {
            var item = dateArray[i];
            for (var k = 0; k < dateArray.length; k++) {
                if(i == k){
                    continue;
                }
                var next_item = dateArray[k];
                if (item.startTime >= next_item.startTime && item.startTime <= next_item.endTime) {
                    $.error("开始时间【" + item.startTimeStr + "】存在交集!");
                    return false;
                }
                if(item.endTime >= next_item.startTime && item.endTime <= next_item.endTime){
                    $.error("结束时间【" + item.endTimeStr + "】存在交集!");
                    return false;
                }
            }
        }
    }
    return true;
}


/**
 * 检查 页面上带有 beginTimeClass 和 endTimeClass 样式的
 *
 * 日历空间, 时间是否有交集
 *
 * @returns {boolean}
 */
function checkDateIntersectionByStatus() {
    var dateArray = new Array();
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    var status=$('.statusClass')
    if (begins.length > ends.length) {
        $.error("请填写结束时间");
    }
    if (begins.length < ends.length) {
        $.error("请填写开始时间");
    }
    for (var i = 0; i < begins.length; i++) {
        if($(status[i]).val()!='A'){
            continue;
        }
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
        dateArray.push(dateObj);
    }
    if (dateArray.length > 0) {
        for (var i = 0; i < dateArray.length; i++) {
            var item = dateArray[i];
            for (var k = 0; k < dateArray.length; k++) {
                if(i == k){
                    continue;
                }
                var next_item = dateArray[k];
                if (item.startTime >= next_item.startTime && item.startTime <= next_item.endTime) {
                    $.error("开始时间【" + item.startTimeStr + "】存在交集!");
                    return false;
                }
                if(item.endTime >= next_item.startTime && item.endTime <= next_item.endTime){
                    $.error("结束时间【" + item.endTimeStr + "】存在交集!");
                    return false;
                }
            }
        }
    }
    return true;
}


/**
 * 校验起始时间不能大于结束时间
 * @returns {boolean}
 */
function checkStartEndDate() {
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    for (var i = 0; i < begins.length; i++) {
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
    }
    return true;
}
/**
 * 起始时间和结束时间都填写时，校验起始时间不能大于结束时间
 * @returns {boolean}
 */
function compareStartEndDate() {
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    for (var i = 0; i < begins.length; i++) {
        var startTimeStr = $.trim($(begins[i]).val());
        var endTimeStr = $.trim($(ends[i]).val());
        if (startTimeStr.length == 0 || endTimeStr.length == 0) {
            continue;
        }
        var startTime = getDateForStringDate(startTimeStr);
        var endTime = getDateForStringDate(endTimeStr);
        if (startTime.getTime() >= endTime.getTime()) {
            $.error("【" + startTimeStr + "-" + endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
    }
    return true;
}

/**
 * 校验起始时间不能大于结束时间(只有年月日，不带时间)
 * @returns {boolean}
 */
function checkStartEndDateWithoutTime() {
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    for (var i = 0; i < begins.length; i++) {
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() > dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
    }
    return true;
}

/**
 * 校验起始时间不能大于结束时间
 * @returns {boolean}
 */
function checkStartEndDateByStatus() {
    var begins = $('.beginTimeClass');
    var ends = $('.endTimeClass');
    var status=$('.statusClass')
    for (var i = 0; i < begins.length; i++) {
        if($(status[i]).val()!='A'){
            continue;
        }
        var dateObj = {};
        dateObj.startTime = getDateForStringDate($(begins[i]).val());
        dateObj.startTimeStr = $(begins[i]).val();
        dateObj.endTime = getDateForStringDate($(ends[i]).val());
        dateObj.endTimeStr = $(ends[i]).val();
        if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
            $.error("【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!");
            return false;
        }
    }
    return true;
}


/**
 * 指定区域的日历控件, 时间是否有交集
 *
 * 用法：时间控件的分组的父容器的class加上 externalClass
 *     时间控件的class加上  beginTimeClass 和 endTimeClass
 * add by chenggang 2016-09-23
 *
 * @returns {boolean}
 */
function checkAreaDateIntersection() {
    try {
        $(".externalClass").each(function () {
            var dateArray = new Array();
                var begins = $(this).find('.beginTimeClass');
                var ends = $(this).find('.endTimeClass');
                if (begins.length > ends.length) {
                    throw "缺少结束时间控件";
                }
                if (begins.length < ends.length) {
                    throw "缺少开始时间控件";
                }
                for (var i = 0; i < begins.length; i++) {
                    var dateObj = {};
                    if($.isBlank($(begins[i]).val())|| $.isBlank($(ends[i]).val())){
                       return true;//continue
                    }
                    dateObj.startTime = getDateForStringDate($(begins[i]).val());
                    dateObj.startTimeStr = $(begins[i]).val();
                    dateObj.endTime = getDateForStringDate($(ends[i]).val());
                    dateObj.endTimeStr = $(ends[i]).val();
                    if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
                        throw "【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!";
                    }
                    dateArray.push(dateObj);
                }
                if (dateArray.length > 0) {
                    for (var i = 0; i < dateArray.length; i++) {
                        var item = dateArray[i];
                        for (var k = 0; k < dateArray.length; k++) {
                            console.log(i);
                            console.log(k);
                            if (i == k) {
                                continue;
                            }
                            var next_item = dateArray[k];
                            if ((item.startTime.getTime() >= next_item.startTime.getTime() && item.startTime.getTime() <= next_item.endTime.getTime()) ||
                                item.endTime.getTime() >= next_item.startTime.getTime() && item.endTime.getTime() <= next_item.endTime.getTime()) {
                                console.log(item.startTime);
                                console.log(item.endTime);
                                console.log(next_item.startTime);
                                console.log(next_item.endTime);
                                throw "【" + item.startTimeStr + "-" + item.endTimeStr + "】时间存在交集!";
                            }
                        }
                    }
                }
        });
    } catch (e) {
        console.log(e);
        $.error(e);
        return false;
    }

    return true;
}


function checkAreaDateIntersectionByStatus() {
    try {
        $(".externalClass").each(function () {
            var dateArray = new Array();
            var status = $(this).find('.statusClass').val();
            if(!$.isBlank(status) && status == 'A'){
                var begins = $(this).find('.beginTimeClass');
                var ends = $(this).find('.endTimeClass');
                if (begins.length > ends.length) {
                    throw "缺少结束时间控件";
                }
                if (begins.length < ends.length) {
                    throw "缺少开始时间控件";
                }
                for (var i = 0; i < begins.length; i++) {
                    var dateObj = {};
                    if($.isBlank($(begins[i]).val())|| $.isBlank($(ends[i]).val())){
                        return true;//continue
                    }
                    dateObj.startTime = getDateForStringDate($(begins[i]).val());
                    dateObj.startTimeStr = $(begins[i]).val();
                    dateObj.endTime = getDateForStringDate($(ends[i]).val());
                    dateObj.endTimeStr = $(ends[i]).val();
                    if (dateObj.startTime.getTime() >= dateObj.endTime.getTime()) {
                        throw "【" + dateObj.startTimeStr + "-" + dateObj.endTimeStr + "】开始时间不能大于结束时间!";
                    }
                    dateArray.push(dateObj);
                }
                if (dateArray.length > 0) {
                    for (var i = 0; i < dateArray.length; i++) {
                        var item = dateArray[i];
                        for (var k = 0; k < dateArray.length; k++) {
                            console.log(i);
                            console.log(k);
                            if (i == k) {
                                continue;
                            }
                            var next_item = dateArray[k];
                            if ((item.startTime.getTime() >= next_item.startTime.getTime() && item.startTime.getTime() <= next_item.endTime.getTime()) ||
                                item.endTime.getTime() >= next_item.startTime.getTime() && item.endTime.getTime() <= next_item.endTime.getTime()) {
                                console.log(item.startTime);
                                console.log(item.endTime);
                                console.log(next_item.startTime);
                                console.log(next_item.endTime);
                                throw "【" + item.startTimeStr + "-" + item.endTimeStr + "】时间存在交集!";
                            }
                        }
                    }
                }
            }
        });
    } catch (e) {
        console.log(e);
        $.error(e);
        return false;
    }

    return true;
}

/**
 * 对没有状态选择字段的页面上的input框进行校验 ，参数item
 * @param item 为json数组，key=inputName代表input框的name值，key=msg代表提示信息,key=eleType代表元素类型
 * 例如：[{inputName:"startTime",msg:"请选择生效时间"}，{inputName:"sku",msg:"请选择sku",eleType:"textarea"}]
 *
 * add by chenggang 2016-09-28
 * @returns {boolean}
 */
function checkDataItems(items) {
    try {
        $.each(items, function (i, content) {
            var inputName = content.inputName;
            var msg = content.msg;
            var eleType=content.eleType;
            if($.isBlank(eleType)){
                eleType="input";
            }
            $(""+eleType+"[name='" + inputName + "']").each(function () {
                if ($.isBlank($(this).val())) {
                    throw msg;
                }
            });
        });
    } catch (e) {
        $.error(e);
        return false;
    }
    return true;
}

/**
 * 对页面上的每一个项都有状态字段的input框进行校验
 * @param statusName 状态选择字段的name值
 * @param items 为json数组，key=inputName代表input框的name值，key=msg代表提示信息，key=eleType代表元素类型，如input，textarea,select等选填，默认为input
 * 例如：[{inputName:"startTime",msg:"请选择生效时间"}，{inputName:"sku",msg:"请选择sku",eleType:"textarea"}]
 *
 * add by chenggang 2016-09-28
 * @returns {boolean}
 */
function checkDataItemsByStatus(statusName, items) {
    try {
        $("select[name='" + statusName + "']").each(function (i) {
            var status = $(this).val();
            if (status == "A") {
                $.each(items, function (j, content) {
                    var inputName = content.inputName;
                    var msg = content.msg;
                    var eleType=content.eleType;
                    if($.isBlank(eleType)){
                        eleType="input";
                    }
                    var value = $(""+eleType+"[name='" + inputName + "']")[i].value;
                    if ($.isBlank(value)) {
                        throw msg;
                    }

                });
            }
        });
    } catch (e) {
        console.log(e);
        $.error(e);
        return false;
    }
    return true;
}


/**
 * 对页面上指定区域下的输入框进行校验，需要在父容器外的class加  externalClass
 * @param statusName 状态选择字段的name值
 * @param items 为json数组，key=inputName代表input框的name值，key=msg代表提示信息，key=eleType代表元素类型，如input，textarea,select等选填，默认为input
 * 例如：[{inputName:"startTime",msg:"请选择生效时间"}，{inputName:"sku",msg:"请选择sku",eleType:"textarea"}]
 *
 * add by chenggang 2016-09-28
 * @returns {boolean}
 */
function checkAreaDataItemsByStatus(statusName, items) {
    try {
        $(".externalClass").each(function () {
            var status=$(this).find("select[name='" + statusName + "']")[0].value;
            var $this=$(this);
            if(status=='A'){
                $.each(items, function (j, content) {
                    var inputName = content.inputName;
                    var msg = content.msg;
                    var eleType=content.eleType;
                    if($.isBlank(eleType)){
                        eleType="input";
                    }
                    $this.find(""+eleType+"[name='" + inputName + "']").each(function(){
                        var value=$(this).val();
                        if ($.isBlank(value)) {
                            throw msg;
                        }
                    });
                });
            }
        });
    } catch (e) {
        console.log(e);
        $.error(e);
        return false;
    }
    return true;
}


/**
 * 根据一个状态选择框对页面上的指定name值的输入框进行校验，会对整个界面的指定name进行校验
 * @param statusName 状态选择字段的name值
 * @param items 为json数组，key=inputName代表input框的name值，key=msg代表提示信息，key=eleType代表元素类型，如input，textarea,select等选填，默认为input
 * 例如：[{inputName:"startTime",msg:"请选择生效时间"}，{inputName:"sku",msg:"请选择sku",eleType:"textarea"}]
 *
 * @param statusName
 * @param items
 * @returns {boolean}
 */
function checkAllNameItemsByStatus(statusName, items) {
    try {
        $("select[name='" + statusName + "']").each(function (i) {
            var status = $(this).val();
            if (status == "A") {
                $.each(items, function (j, content) {
                    var inputName = content.inputName;
                    var msg = content.msg;
                    var eleType=content.eleType;
                    if($.isBlank(eleType)){
                        eleType="input";
                    }
                    $(""+eleType+"[name='" + inputName + "']").each(function(){
                        var value =$(this).val();
                        if ($.isBlank(value)) {
                            throw msg;
                        }
                    });
                });
            }
        });
    } catch (e) {
        console.log(e);
        $.error(e);
        return false;
    }
    return true;
}


/**
 * 解决 ie，火狐浏览器不兼容new Date(s)
 * @param strDate
 * 返回 date对象
 */
function getDateForStringDate(strDate){
    //切割年月日与时分秒称为数组
    if($.isBlank(strDate)){
        $.error("时间为空！")
        return null;
    }
    var s = strDate.split(" ");
    var s1 = s[0].split("-");
    //日期格式为: yyyy-MM-dd HH:mm:ss的情况
    if(s.length > 1){
        var s2 = s[1].split(":");
        if(s2.length==2){
            s2.push("00");
        }
        return new Date(s1[0],s1[1]-1,s1[2],s2[0],s2[1],s2[2]);
    }
    //日期格式为: yyyy-MM-dd的情况，不包含时分秒
    return new Date(s1[0],s1[1]-1,s1[2]);
}

function alterpage(url, title,width,height,callback){
    if (width == "" || width == undefined || width == null){
        width = '1000px';
    }
    if (height == "" || height == undefined || height == null){
        height = '400px';
    }
    var layerIndex = layer.open({
        type: 2,
        title: title,
        area: [width, height],
        fix: true,
        content: [url, 'no'],
        success:function(layero,index){
            layer.iframeAuto(layerIndex);
        },
        end:function(){
            if($.isFunction(callback)){
                callback();
            }
        }
    });
    layer.iframeAuto(layerIndex);
}


/**
 * 改变图片路径响应事件
 * @param domain 图片服务器地址
 * @param id 图片控件ID后缀
 */
function changeImagePath(domain,id){
    var imagePath = $("#filePath"+id).val();
    $("#image" + id).attr("src",domain+imagePath);
}

/**
 * 检查规则价格区间
 * @returns {boolean}
 */
function checkRulePrice() {
    var minPriceInput = $('input[name="minPrice"]');
    var maxPriceInput = $('input[name="maxPrice"]');
    for (var i = 0; i < minPriceInput.length; i++) {
        var minPrice = $(minPriceInput[i]).val().trim();
        var maxPrice = $(maxPriceInput[i]).val().trim();
        if (minPrice.length > 0 && !isNonnegativeDecimal(minPriceInput[i])) {
            $.error("价格下限格式不正确");
            return false;
        }
        if (maxPrice.length > 0 && !isNonnegativeDecimal(maxPriceInput[i])) {
            $.error("价格上限限格式不正确");
            return false;
        }
        if (minPrice.length > 0 && maxPrice.length > 0 && Number(minPrice) > Number(maxPrice)) {
            $.error("价格下限不能大于价格上限");
            return false;
        }
    }
    return true;
}
function checkRuleDefined() {
    var definedKeyInput = $('input[name="definedKey"]');
    var definedValueInput = $('input[name="definedValue"]');
    for (var i = 0; i < definedKeyInput.length; i++) {
        var definedKey = $(definedKeyInput[i]).val().trim();
        var definedValue = $(definedValueInput[i]).val().trim();
        if ((definedKey.length > 0 ^ definedValue.length > 0) > 0) {
            $.error("高级规则中的属性和值不能只填写一个哦");
            return false;
        }
    }
    return true;
}


function checkOne(itemName) {
    var itemCheckBox = $("[name = '"+itemName+"']:checkbox");
    $("#checkAll").prop("checked", itemCheckBox.length == itemCheckBox.filter(":checked").length);
}
function checkAll(itemName) {
    $("[name='"+itemName+"']").prop("checked",$("#checkAll").prop("checked"));
}