const serverBase = '/fuint-application';

let curGoodsInfo = null;

// 左侧点击过滤商品类型
const handleFilter = function() {
    "use strict";
    $(document).on('click', '.pos-menu [data-filter]', function(e) {
        e.preventDefault();
        var targetType = $(this).attr('data-filter');
        $(this).addClass('active');
        $('.pos-menu [data-filter]').not(this).removeClass('active');
        if (targetType == 'all') {
            $('.pos-content [data-type]').removeClass('d-none');
        } else {
            $('.pos-content [data-type="' + targetType + '"]').removeClass('d-none');
            $('.pos-content [data-type]').not('.pos-content [data-type="' + targetType + '"]').addClass('d-none');
        }
    });
};

// 获取购物车列表
const getCartList = function(isUpdateList) {
    "use strict";
    $.ajax({
        type: "POST",
        dataType: "json",
        async: false,
        data: JSON.stringify({}),
        contentType: 'application/json;charset=UTF-8',
        url: serverBase + "/rest/cart/list",
        success: function (data) {
            if (data.data) {
                if (data.data.list.length > 0 && isUpdateList) {
                    data.data.list.forEach(function(cartInfo) {
                        let goodsInfo = cartInfo.goodsInfo;
                        goodsInfo.goodsId = cartInfo.goodsId;
                        goodsInfo.skuId = cartInfo.skuId;
                        goodsInfo.cartId = cartInfo.id;
                        goodsInfo.buyNum = cartInfo.num;
                        doAddCart(goodsInfo, false);
                    })
                } else if (data.data.list.length == 0 && isUpdateList) {
                    $('.pos-table').find('.cart-item').remove();
                    $('.no-cart').removeClass("fade");
                }
                $("#totalPrice").text(parseFloat(data.data.totalPrice).toFixed(2));
                $("#totalNum").text(data.data.totalNum + "件");
                $("#payAmount").text(parseFloat(data.data.totalPrice).toFixed(2));
            }
        }
    })
}

// 点击商品弹框显示商品详情
const handleGoodsInfo = function() {
    "use strict";
    $(document).on('click', '.product-row [data-id]', function(e) {
        e.preventDefault();
        let goodsId = $(this).attr('data-id');

        $.ajax({
            type: "GET",
            dataType: "json",
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/rest/goodsApi/detail?goodsId=" + goodsId,
            success: function (data) {
                if (data.data) {
                    const goodsInfo = data.data;

                    // 单规格直接加购物车
                    if (goodsInfo.isSingleSpec == 'Y') {
                        goodsInfo.skuId = '0';
                        goodsInfo.buyNum = 1;
                        doAddCart(goodsInfo, true);
                        return false;
                    }

                    curGoodsInfo = goodsInfo;

                    $("#modalGoodsName").text(goodsInfo.name);
                    $("#modalGoodsLogo").css("background-image", "url("+ goodsInfo.logo +")");
                    $("#modalGoodsPrice").text("￥" + goodsInfo.price);
                    $("#modalGoodsTips").text(goodsInfo.salePoint);
                    $('#modalCartNum').val(1);
                    $("#modalGoodsOptions").html('');
                    var specHtml = '';
                    if (goodsInfo.specList.length > 0) {
                        for (var i = 0; i < goodsInfo.specList.length; i++) {
                            specHtml += "<div class='option-row spec-item'>" +
                                "<div class='option-title'>" + goodsInfo.specList[i].name + "</div>" +
                                "<div class='option-list'>";
                                var valueHtml = "";
                                for (var j = 0; j < goodsInfo.specList[i].valueList.length; j++) {
                                    var checked = "";
                                    if (j == 0) {
                                        checked = "checked='true'"
                                    }
                                    valueHtml += "<div class='option'>" +
                                    "<input type='radio' id='spec" + goodsInfo.specList[i].valueList[j].specValueId + "' name='spec" + goodsInfo.specList[i].specId + "' value='" + goodsInfo.specList[i].valueList[j].specValueId + "' class='option-input' " + checked + "/>" +
                                    "<label class='option-label' for='spec" + goodsInfo.specList[i].valueList[j].specValueId + "'>" +
                                    "<span class='option-text'>" + goodsInfo.specList[i].valueList[j].specValue + "</span>" +
                                    "</label>" +
                                    "</div>";
                                }
                                specHtml += valueHtml +
                                "</div>" +
                                "</div>"
                        }
                    }
                    $("#modalGoodsOptions").append(specHtml);
                    $('#modalPosItem').modal('show');
                } else {
                    return;
                }
            }
        })
    });
};

// 购物车数量+1
const addCartNum = function() {
    "use strict";
    $(document).on('click', '#modalAddNum', function() {
        var num = $('#modalCartNum').val();
        var re = /^[1-9]+[0-9]*]*$/;
        if (!re.test(num)) {
            num = 1;
        } else {
            num = parseInt(num) + 1;
        }
        $('#modalCartNum').val(num);
    });
};

// 购物车数量-1
const minusCartNum = function() {
    "use strict";
    $(document).on('click', '#modalMinusNum', function() {
        var num = $('#modalCartNum').val();
        var re = /^[1-9]+[0-9]*]*$/;
        if (!re.test(num) || num <= 1) {
            num = 1;
        } else {
            num = parseInt(num) - 1;
        }
        $('#modalCartNum').val(num);
    });
};

// 多规格商品加入购物车操作
const addCart = function() {
    "use strict";
    $(document).on('click', '#addCart', function() {
        let len = $('.goods-options').find('.option-row').length;
        let specIds = "";
        if (len > 0) {
            for (var i = 0; i < len; i++) {
                let specItem = $('.goods-options').find('.spec-item').eq(i);
                let specLen = specItem.find('.option').length;
                for (var j = 0; j < specLen; j++) {
                    var checked = specItem.find('.option').eq(j).find('input', 0).attr("checked");
                    if (checked != undefined) {
                        let value = specItem.find('.option').eq(j).find('input', 0).attr("value");
                        if (specIds.length > 0) {
                            specIds += '-' + value
                        } else {
                            specIds = value
                        }
                    }
                }
            }
        }

        console.log("goodsInfo = ", curGoodsInfo);
        const skuList = curGoodsInfo.skuList || [];
        let skuId = 0;
        skuList.forEach(function(sku){
            if (specIds.length > 0 && specIds == sku.specIds) {
                skuId = sku.id;
            }
        })

        curGoodsInfo.skuId = skuId;
        curGoodsInfo.specIds = specIds;
        curGoodsInfo.buyNum = $('#modalCartNum').val();
        doAddCart(curGoodsInfo, true);
        $('#modalPosItem').modal('hide');
    });
};

// 点击选择属性
const doSelectSpec = function() {
    "use strict";
    $(document).on('click', '.option', function() {
        $(this).find("input").attr("checked", true);
        const len = $(this).siblings().length;
        for (var i = 0; i < len; i++) {
            $(this).siblings().eq(i).find("input").attr("checked", false);
        }
    });
};

// 把商品加入购物车
const doAddCart = function(goodsInfo, isRemote) {
    // 调接口加购物车
    if (isRemote) {
        const cartId = remoteCart(goodsInfo);
        if (!cartId) {
            layer.alert('购物车操作失败！');
            return false;
        }
        goodsInfo.cartId = cartId
    }

    // 检查是否已经存在
    const cartNum = $('.pos-table').find('.cart-item').length;
    let isExist = false;
    if (cartNum > 0) {
        $('.pos-table').find('.cart-item').each(function() {
            const goodsId = $(this).find("input[name='goodsId']").val();
            const skuId = $(this).find("input[name='skuId']").val();
            const num = $(this).find("input[name='buyNum']").val();
            if (goodsInfo.goodsId == goodsId && goodsInfo.skuId == skuId) {
                const totalNum = parseInt(goodsInfo.buyNum) + parseInt(num);
                $(this).find("input[name='buyNum']").attr("value", totalNum);
                $(this).find('.cartGoodsTotalPrice').text('￥' + (goodsInfo.price * totalNum));
                isExist = true;
            }
        })
    }

    if (isExist) {
        return false;
    }

    $('.no-cart').addClass("fade");
    $('#cartItem').find('#cartGoodsImg').css('background-image', 'url(' + goodsInfo.logo + ')');
    $('#cartItem').find('#cartGoodsName').text(goodsInfo.name);
    $('#cartItem').find('#cartGoodsPrice').text(goodsInfo.price);
    $('#cartItem').find('#cartGoodsTotalPrice').text(goodsInfo.price * goodsInfo.buyNum);
    $('#cartItem').find("input[name='goodsId']").val(goodsInfo.goodsId);
    $('#cartItem').find("input[name='skuId']").val(goodsInfo.skuId);
    $('#cartItem').find("input[name='cartId']").val(goodsInfo.cartId);
    $('#cartItem').find("input[name='buyNum']").attr("value", goodsInfo.buyNum);
    $('#cartItem').find('.pos-table-row').addClass('cart-item');

    let specHtml = "";
    if (goodsInfo.specIds) {
        let specList = [];
        goodsInfo.specList.forEach(function(spec){
            specList = specList.concat(spec.valueList);
        })

        let specIdArr = goodsInfo.specIds.split("-");
        specList.forEach(function(item){
            specIdArr.forEach(function(id){
                if (item.specValueId == id) {
                    if (specHtml.length > 0) {
                        specHtml += "<span class='spec'>" + item.specValue + "</span>";
                    } else {
                        specHtml = "<span class='spec'>" + item.specValue + "</span>";
                    }
                }
            })
        })
    }

    $('#cartItem').find('#cartGoodsSpec').html(specHtml);
    const html = $('#cartItem').html();
    $('.pos-table').prepend(html);

    $('#cartItem').find('.pos-table-row').removeClass('cart-item');
}

// 清空购物车
const clearCart = function() {
    "use strict";
    $(document).on('click', '.clear-cart', function() {
         let result = false

         $.ajax({
            type: "POST",
            dataType: "json",
            async: false,
            data: JSON.stringify({}),
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/rest/cart/clear",
            success: function (data) {
                if (data.data) {
                    result = true
                    getCartList(false);
                }
            }
         })

         if (result) {
             $('.pos-table').find('.cart-item').remove();
             $('.no-cart').removeClass("fade");
         }
    });
}

// 点击全屏
const doFullScreen = function() {
    "use strict";
    $(document).on('click', '.full-screen', function() {
        $(document).toggleFullScreen();
    });
}

// 点击增加购物车中商品数量
const increaseCart = function() {
    "use strict";
    $(document).on('click', '.increase-cart', function() {
        const goodsId = $(this).parent().parent().find("input[name='goodsId']").val();
        const cartId = $(this).parent().parent().find("input[name='cartId']").val();
        const skuId = $(this).parent().parent().find("input[name='skuId']").val();
        const result = remoteCart( { "goodsId" : goodsId, "cartId": cartId, "skuId": skuId,  "buyNum": 1 }, "+" );
        if (result) {
            let buyNum = $(this).parent().find("input[name='buyNum']").attr("value");
            $(this).parent().find("input[name='buyNum']").attr("value", (parseInt(buyNum) + 1));
        }
    });
}

// 点击减少购物车中商品数量
const minusCart = function() {
    "use strict";
    $(document).on('click', '.minus-cart', function() {
        const goodsId = $(this).parent().parent().find("input[name='goodsId']").val();
        const cartId = $(this).parent().parent().find("input[name='cartId']").val();
        const skuId = $(this).parent().parent().find("input[name='skuId']").val();
        const buyNum = $(this).parent().find("input[name='buyNum']").attr("value");
        if (buyNum > 1) {
            const result = remoteCart({"goodsId": goodsId, "cartId": cartId, "skuId": skuId, "buyNum": 1}, "-");
            if (result) {
                $(this).parent().find("input[name='buyNum']").attr("value", (parseInt(buyNum) - 1));
            }
        }
    });
}

// 移出购物车
const removeCart = function() {
    "use strict";
    $(document).on('click', '.remove-cart', function() {
        $('.pos-remove-confirmation').css("display", "none");
        $(this).parent().parent().find(".pos-remove-confirmation").css("display", "block");
        const len = $('.cart-item').length;
        if (len == 1) {
            $('.no-cart').removeClass("fade");
        }
    });
}

// 取消移出购物车
const cancelRemoveCart = function() {
    "use strict";
    $(document).on('click', '.cancel-remove-cart', function() {
        $('.pos-remove-confirmation').css("display", "none");
    });
}

// 确定移出购物车
const doRemoveCart = function() {
    "use strict";
    $(document).on('click', '.do-remove-cart', function() {
        $('.pos-remove-confirmation').css("display", "none");
        const goodsId = $(this).parent().parent().find("input[name='goodsId']").val();
        const cartId = $(this).parent().parent().find("input[name='cartId']").val();
        const skuId = $(this).parent().parent().find("input[name='skuId']").val();
        const result = remoteCart( { "goodsId" : goodsId, "cartId": cartId, "skuId": skuId,  "buyNum": 0 }, "-" );
        if (result) {
            $(this).parent().parent().remove();
        }
    });
}

// 远程操作购物车，成功返回购物车ID
const remoteCart = function(goodsInfo, action = '+') {
    let cartId = false
    $.ajax({
        type: "POST",
        dataType: "json",
        async: false,
        data: JSON.stringify({ "goodsId": goodsInfo.goodsId, "cartId": goodsInfo.cartId, "skuId": goodsInfo.skuId, "skuNo": goodsInfo.skuNo, "buyNum": goodsInfo.buyNum, "action": action }),
        contentType: 'application/json;charset=UTF-8',
        url: serverBase + "/rest/cart/save",
        success: function (data) {
           if (data.data) {
               cartId = data.data.cartId
               getCartList(false);
           }
        }
    })

    return cartId;
}

// 查找会员
const queryMember = function() {
    "use strict";
    $(document).on('click', '#queryMember', function() {
        const mobile = $("#queryMobile").val();
        if (mobile.length < 1) {
            layer.alert('请先输入会员手机号！');
            return false;
        }
        $.ajax({
            type: "GET",
            dataType: "json",
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/rest/cashier/memberInfo?mobile=" + mobile,
            success: function (data) {
                if (data.data.memberInfo) {
                    const member = data.data.memberInfo;
                    $("#memberInfo").css("display", "block");
                    $("#noMember").css("display", "none");
                    $("#memberId").val(member.id);
                    if (member.avatar) {
                        $("#memberAvatar").attr("src", member.avatar);
                    } else {
                        $("#memberAvatar").attr("src", serverBase + "/static/assets/img/avatar/user-1.png");
                    }
                    $("#memberName").text(member.name);
                    $("#memberNo").text(member.userNo);
                    $("#memberMobile").text(member.mobile);
                    $("#memberPoint").text(member.point);
                    $("#memberBalance").text(member.balance);
                } else {
                    layer.alert('会员信息查询失败！');
                    $("#memberId").val(0);
                    $("#memberInfo").css("display", "none");
                    $("#noMember").css("display", "block");
                }
            }
        })
    });
}

// 取消会员
const cancelMember = function() {
    "use strict";
    $(document).on('click', '#cancelMember', function() {
        $("#memberInfo").css("display", "none");
        $("#noMember").css("display", "block");
        $("#queryMobile").val("");
        $("#memberId").val("");
    })
}

// 提交结算
const submitCheck = function() {
    "use strict";
    $(document).on('click', '#submitCheck', function() {
        $('#modalPosCheck').modal('show');
        $("#memberInfo").css("display", "none");
        $("#noMember").css("display", "block");
        $("#memberId").val(0);
        $("#queryMobile").val("");
        $("#checkType").val("goods");
        calculatePayAmount();
    });
}

// 点击确认收款按钮
const doCash = function() {
    "use strict";
    $(document).on('click', '#doCash', function() {
        const checkType = $("#checkType").val();
        if (checkType === 'noGoods') {
            noGoodsDoCash();
            return false;
        }

        let cartIds = [];
        $('.pos-table').find('.cart-item').each(function() {
            const cartId = $(this).find("input[name='cartId']").val();
            if (cartId.length > 0) {
                cartIds.push(cartId);
            }
        })

        if (cartIds.length < 1) {
            layer.alert("请先添加购物车！");
            return false
        }

        // 收银备注
        let remark = $("#remark").val();
        if (remark.length < 1) {
            remark = "收银台订单";
        }

        const payAmount = $("#payAmount").text();
        const totalDiscount = $("#totalDiscount").text();

        // 支付方式
        let payTool = 'wechat';
        $(".pay-tool").each(function() {
            if ($(this).hasClass("active")) {
               payTool = $(this).attr("attr");
            }
        })

        $("#doCash").attr("disabled", "disabled");

        $.ajax({
            type: "POST",
            dataType: "json",
            async: false,
            data: JSON.stringify({ "type": "goods", "cartId": cartIds.join(','), "payType": payTool, "cashierPayAmount": payAmount, "cashierDiscountAmount": totalDiscount, "userId": $("#memberId").val(), "orderMode": "oneself", "remark": remark }),
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/rest/settlement/submit",
            success: function (data) {
                if (data.data) {
                    $("#payAmount").text(data.data.orderInfo.amount.toFixed(2));
                    $("#doPayAmount").text(data.data.orderInfo.payAmount.toFixed(2));
                    $("#orderId").val(data.data.orderInfo.id);
                    $('#modalPosCheck').modal('hide');
                    if (parseFloat(data.data.orderInfo.payAmount) > 0 && data.data.orderInfo.payType !== 'CASH') {
                        $('#cashCodeModal').modal('show');
                    }

                    // 现金支付成功
                    if (data.data.orderInfo.payType === 'CASH') {
                        $("#completePay").click();
                        $('#paySuccessModal').modal('show');
                    }

                    $("#doCash").removeAttr("disabled");
                }
            }
        })
    })
}

// 无商品收款确认
const noGoodsDoCash = function() {
    const payAmount = $("#payAmount").text();
    const totalDiscount = $("#totalDiscount").text();

    // 支付方式
    let payTool = 'wechat';
    $(".pay-tool").each(function() {
        if ($(this).hasClass("active")) {
            payTool = $(this).attr("attr");
        }
    })

    // 收银备注
    let remark = $("#remark").val();
    if (remark.length < 1) {
        remark = "收银台收款";
    }

    if (payAmount == "" || parseFloat(payAmount) < 0.01) {
        return false;
    }

    $("#doCash").attr("disabled", "disabled");

    $.ajax({
        type: "POST",
        dataType: "json",
        contentType: 'application/json;charset=UTF-8',
        data: JSON.stringify({"payAmount": payAmount, "orderId": $("#orderId").val(), "cashierPayAmount": payAmount, "cashierDiscountAmount": totalDiscount, "type": "payment", "userId": $("#memberId").val(), "payType": payTool, "remark": remark}),
        url: serverBase + "/rest/settlement/submit",
        success: function (data) {
            if (data.data) {
                $("#checkType").val("goods");

                $('#modalPosCheck').modal('hide');

                $("#payAmount").text(data.data.orderInfo.amount.toFixed(2));
                $("#doPayAmount").text(data.data.orderInfo.payAmount.toFixed(2));
                $("#orderId").val(data.data.orderInfo.id);

                if (parseFloat(data.data.orderInfo.payAmount) > 0 && data.data.orderInfo.payType !== 'CASH') {
                    $('#cashCodeModal').modal('show');
                }

                // 现金支付成功
                if (data.data.orderInfo.payType === 'CASH') {
                    $("#completePay").click();
                    $('#paySuccessModal').modal('show');
                }

                $("#doCash").removeAttr("disabled");

                return true;
            } else {
                layer.alert(data.message);
                return false;
            }
        }
    })
}

// 点击选择支付方式
const selectPayTool = function() {
    "use strict";
    $(document).on('click', '.pay-tool', function() {
        $('.pay-tool').removeClass("active");
        $(this).addClass("active");
    });
}

// 点击支付完成按钮
const completePay = function() {
    "use strict";
    $(document).on('click', '#completePay', function() {
        getCartList(true);
        $("#remark").val("");
        $("#discount").val("");
        $("#reduce").val("");
        $("#payAmount").text("0.00");
        $("#totalDiscount").text("0.00");
    });
}

// 输入折扣
const takeDiscount = function() {
    "use strict";
    $(document).on('change', '#discount', function() {
        const regex = /^(0|[1-9]\d*)(\s|$|.\d{1,2}\b)/;
        let discount = $("#discount").val();
        if (discount == "") {
            discount = "10";
        }
        if (discount.length > 0) {
            const verify = regex.test(discount);
            if (verify && parseFloat(discount) <= 10) {
                calculatePayAmount();
            } else {
                layer.alert('输入折扣格式有误，正数且最多保留两位小数！');
                $("#discount").val("");
            }
        }
    });
}

// 输入减免金额
const takeReduce = function() {
    "use strict";
    $(document).on('change', '#reduce', function() {
        const regex = /^(0|[1-9]\d*)(\s|$|.\d{1,2}\b)/;
        let reduce = $("#reduce").val();
        if (reduce == "") {
            reduce = "0";
        }
        if (reduce.length > 0) {
            const verify = regex.test(reduce);
            if (verify) {
                calculatePayAmount();
            } else {
                layer.alert('输入立减金额格式有误！');
                $("#reduce").val("");
            }
        }
    });
}

// 计算支付金额
const calculatePayAmount = function() {
    const checkType = $("#checkType").val();
    const discount = $("#discount").val();
    const reduce = $("#reduce").val();
    let totalPrice = $("#totalPrice").text();

    // 无商品结算
    if (checkType === 'noGoods') {
        totalPrice = $("#noGoodsPayAmount").val();
    }

    let payAmount = totalPrice
    if (payAmount == "") {
        payAmount = 0;
        totalPrice = 0;
    }

    // 折扣
    if (discount.length > 0) {
        payAmount = payAmount * (discount / 10);
    }

    // 直接减免
    if (reduce.length > 0) {
        payAmount = payAmount - reduce;
    }

    if (parseFloat(payAmount) < 0) {
        payAmount = 0;
    }

    // 实付金额
    $("#payAmount").text(parseFloat(payAmount).toFixed(2));

    // 总优惠金额
    const totalDiscount = parseFloat(totalPrice) - parseFloat(payAmount);

    $("#totalDiscount").text(totalDiscount.toFixed(2))
}

// 点击无商品收款
const noGoodsPay = function() {
    "use strict";
    $(document).on('click', '#noGoodsPay', function() {
        $("#noGoodsPayAmount").val("");
        $("#noGoodsPayRemark").val("");
        $("#discount").val("");
        $("#reduce").val("");
        $('#noGoodsModal').modal('show');
    });
}

// 点击确认无商品收款
const doNoGoodsPay = function() {
    "use strict";
    $(document).on('click', '#doNoGoodsPay', function() {
        // 结算类型为无商品结算
        $("#checkType").val("noGoods");

        // 实付金额
        const payAmount = $("#noGoodsPayAmount").val();
        if (payAmount.length < 1) {
            layer.alert("请先输入收款金额！");
            return false;
        }

        const regex = /^(0|[1-9]\d*)(\s|$|.\d{1,2}\b)/;
        const verify = regex.test(payAmount);
        if (!verify) {
            layer.alert("输入收款金额有误！");
            return false;
        }

        $("#payAmount").text(parseFloat(payAmount).toFixed(2));

        const noGoodsPayRemark = $("#noGoodsPayRemark").val();
        $("#remark").val(noGoodsPayRemark);

        $('#noGoodsModal').modal('hide');
        $('#modalPosCheck').modal('show');
    });
}

// 扫码添加购物车
const addCartByCode = function() {
    "use strict";
    $(document).on('click', '#addCartByCode', function() {
        const goodsNo = $("#goodsNo").val();
        if (goodsNo.length < 1) {
            layer.alert("请先输入商品条码！");
            return false;
        }

        $.ajax({
            type: "GET",
            dataType: "json",
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/rest/goodsApi/getGoodsInfoBySkuNo?skuNo=" + goodsNo,
            success: function (data) {
                if (data.data) {
                    let goodsInfo = data.data.goodsInfo;
                    goodsInfo.skuId = data.data.skuId;
                    goodsInfo.buyNum = 1;
                    goodsInfo.goodsId = goodsInfo.id;
                    doAddCart(goodsInfo, true);
                } else {
                    layer.alert("加入购物车失败：" + data.message);
                    return false;
                }
            }
        })
    });
}

// 发起支付
const doSubmitToPay = function(qrCode) {
    $("#payNotice").text("请使用扫码枪，扫码收款！");

    if (qrCode.length < 1) {
        layer.alert("支付失败，请重新扫码！");
        return false;
    }

    const orderId = $("#orderId").val();
    if (orderId.length < 1) {
        layer.alert("抱歉，支付出错啦！");
        return false;
    }

    $.ajax({
        type: "GET",
        dataType: "json",
        contentType: 'application/json;charset=UTF-8',
        url: serverBase + "/rest/pay/doPay?orderId=" + orderId + '&authCode=' + qrCode,
        success: function (data) {
            if (data.data) {
                $("#payNotice").text("会员支付中，请等待...");
                return true;
            } else {
                layer.alert(data.message);
                return false;
            }
        }
    })
}

// 点击切换tab选项
const switchTabs = function() {
    "use strict";
    $(document).on('click', '.switchTab', function() {
        let tab = $(this).attr("attr");
        if (tab === 'order') {
            // 获取最近的订单列表
            $.ajax({
                type: "GET",
                dataType: "json",
                contentType: 'application/json;charset=UTF-8',
                url: serverBase + "/backend/order/latest",
                success: function (data) {
                    if (data.data) {
                        if (data.data.goodsList) {
                            $(".no-order").css("display", "none");
                            $(".order-list").css("display", "block");
                            $(".real-order-item").remove();
                            data.data.goodsList.content.forEach(function(item) {
                                $('#orderItem').find('.order-item').addClass('real-order-item');
                                $('#orderItem').find('.no').text(item.orderSn);
                                $('#orderItem').find('.amount').text(parseFloat(item.amount).toFixed(2));
                                $('#orderItem').find('.member').text(item.userInfo.name + ' '+ item.userInfo.mobile);
                                $('#orderItem').find('.order-time').text(item.updateTime.substr(10, 8));
                                $('#orderItem').find('.order-status').text(item.statusText).attr("attr", item.id);
                                if (item.payStatus == 'B') {
                                    $('#orderItem').find('.order-status').removeClass("btn-danger").addClass("btn-success");
                                } else {
                                    $('#orderItem').find('.order-status').removeClass("btn-success").addClass("btn-danger");
                                }
                                let html = $("#orderItem").html();
                                $('.order-list').append(html);
                                $('#orderItem').find('.order-item').removeClass('real-order-item');
                            })
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            })
        }
    });
}

// 点击订单支付
const orderPay = function() {
    "use strict";
    $(document).on('click', '.order-status', function () {
        $("#checkType").val("noGoods");
        const orderId = $(this).attr("attr");
        $.ajax({
            type: "GET",
            dataType: "json",
            contentType: 'application/json;charset=UTF-8',
            url: serverBase + "/backend/order/info/" + orderId,
            success: function (data) {
                if (data.data.orderInfo) {
                    const orderInfo = data.data.orderInfo;
                    if (orderInfo.status == 'A') {
                        $("#payAmount").text(parseFloat(orderInfo.amount).toFixed(2));
                        $("#doPayAmount").text(parseFloat(orderInfo.amount).toFixed(2));
                        $("#orderId").val(orderId);
                        $("#noGoodsPayAmount").val(parseFloat(orderInfo.amount).toFixed(2));
                        $('#modalPosCheck').modal('show');
                    }
                }
            }
        });
    })
}

$(document).ready(function() {
    handleFilter();
    getCartList(true);
    handleGoodsInfo();
    addCartNum();
    minusCartNum();
    addCart();
    doSelectSpec();
    clearCart();
    doFullScreen();
    increaseCart();
    minusCart();
    removeCart();
    cancelRemoveCart();
    doRemoveCart();
    selectPayTool();
    submitCheck();
    queryMember();
    cancelMember();
    doCash();
    takeDiscount();
    takeReduce();
    completePay();
    noGoodsPay();
    doNoGoodsPay();
    addCartByCode();
    switchTabs();
    orderPay();
});

// 监听扫码枪的输入
window.onload = function (e) {
    let qrCode = "";
    let lastTime, nextTime;
    let lastCode, nextCode;
    document.onkeypress = function (e) {
        nextCode = e.which;
        nextTime = new Date().getTime();
        if (lastCode != null && lastTime != null && nextTime - lastTime <= 30) {
            qrCode += String.fromCharCode(lastCode);
        } else if (lastCode != null && lastTime != null && nextTime - lastTime > 100) {
            qrCode = '';
        }
        lastCode = nextCode;
        lastTime = nextTime;
    }
    this.onkeypress = function (e) {
        // 监听回车
        if (e.which == 13) {
            doSubmitToPay(qrCode);
            qrCode = "";
        }
    }
}