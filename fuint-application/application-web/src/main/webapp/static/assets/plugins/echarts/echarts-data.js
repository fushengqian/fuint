$(function () {
    // 填入对应日期
    var dateArr = getDate();

    /**
     * 禁止日历框输入
     */

    $('.date-end,.date-start').bind("keydown",function(){
        return false
    })

    $('.aw-content-wrap').find('.date-start').val(dateArr[1]);
    $('.aw-content-wrap').find('.date-end').val(dateArr[0]);

    // 图表数据接入
    var echart = new Echarts('#main', 'line', '/fuint-application/backend/home/statistic?tag=order,user_active&start_date=' + dateArr[3] + '&end_date=' + dateArr[2]);
    var echart2 = new Echarts('#main1', 'line', '/fuint-application/backend/home/statistic?tag=payment&start_date=' + dateArr[3] + '&end_date=' + dateArr[2]);

    // 左侧菜单收缩重新渲染图表
    $('.aw-header .mod-head-btn').click(function ()
    {
        echart.render();
        echart2.render();
    });

    window.addEventListener("orientationchange", function ()
    {
        echart.render();
        echart2.render();
    }, false);

    var oEchart = $('.echart-date');

    for (var i = 0, j = oEchart.length; i < j; i++)
    {
        (function (i) {
            oEchart[i].onclick = function (ev) {
                var ev = ev || window.event;
                var target = ev.targe || ev.srcElement;
                if (ev.target.nodeName.toLocaleLowerCase() == "a") {
                    var start_date = $(this).find('.date-start').val(),
                        end_date = $(this).find('.date-end').val(),
                        aEchart = '';
                    i > 0 ? aEchart = eval('echart' + (i + 1)) : aEchart = eval(aEchart = 'echart');

                    var url = aEchart.url.substring(0, aEchart.url.search(/&/)) + '&start_date=' + start_date + '&end_date=' + end_date;

                    aEchart.initChart(url);
                }
            };
        })(i);
    }
});

function getDate() {
    var date = new Date(),
        Year = 0,
        beforeYear = 0,
        Month = 0,
        beforeSixMonth = 0,
        Day = 0,
        stratDate = "",
        endDate = "",
        stratDateM = "",
        endDateM = "",
        arr = [];

    Year = date.getFullYear();
    Month = date.getMonth() + 1;
    Day = date.getDate();

    if (Month > 6) {
        beforeSixMonth = Month - 6;
        beforeYear = date.getFullYear();
    } else {
        beforeYear = (date.getFullYear()) - 1;
        beforeSixMonth = 6 + Month;
    }


    stratDate += Year + "-";
    endDate += beforeYear + "-";

    if (Month >= 10) {
        stratDate += Month + "-";
        endDate += "0" + beforeSixMonth + "-";
    } else {
        stratDate += "0" + Month + "-";
        endDate += beforeSixMonth + "-";
    }

    if (Day >= 10) {
        stratDate += Day;
        endDate += Day;
    } else {
        stratDate += "0" + Day;
        endDate += "0" + Day;
    }

    stratDateM = stratDate;
    endDateM = endDate;

    arr.push(stratDate, endDate, stratDateM, endDateM);
    return arr;
}


function Echarts(element, type, url, options) {
    this.element = element;
    this.type = type;
    this.url = url;

    this.options = {
        animation: false,
        addDataAnimation: false,
        grid: {
            x: 45,
            y: 65,
            x2: 15,
            y2: 35,
            backgroundColor: '#fff',
            borderColor: '#fff'
        },
        calculable: false,
        yAxis: [{
            type: 'value',
            splitLine: {
                show: false,
            },
            axisLine: {
                show: false
            },
            splitLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(0,0,0,0.1)',
                    type: 'dashed',
                    width: 1
                }
            }
        }],

    };

    this.options = $.extend(this.options, options);
    this.initChart(url);
}

Echarts.prototype = {
    // 图表初始化
    initChart: function (url) {
        this.getData(url);
    },

    // 初始化x轴数据
    initxAxis: function (data) {
        var options = {
            xAxis: [{
                type: 'category',
                splitLine: {
                    show: false,
                },
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false,
                },
                data: data
            }]
        };
        return options;
    },

    // 获取数据
    getData: function (url) {
        var _this = this;
        if (url) {
            $.get(url, {
                'async': false
            }, function (result) {
                if (parseInt(result.code) == 200) {
                    var xAxis = _this.initxAxis(result.data.labels),
                        legend = _this.getUrlParam('tag'),
                        series = _this.initSeries(_this.type, result.data.data);
                    _this.options = $.extend(_this.options, xAxis, series, legend);
                    _this.render(_this.element);
                }
            }, 'json');
        }
    },

    // 初始化线的模板数据
    initSeries: function (type, data) {
        if (data) {
            var arr = [];

            for (var i = 0; i < data.length; i++) {
                var j = {
                    name: this.legend_data[i],
                    type: 'line',
                    symbol: 'none',
                    itemStyle: {
                        normal: {
                            lineStyle: {
                                width: 4,
                            }
                        },
                    },
                    data: data[i]
                }
                arr.push(j);
            }

            var options = {
                series: arr
            }

            return options;

        }
    },

    // 渲染
    render: function () {
        var chart = echarts.init($(this.element)[0]);
        chart.setOption(this.options);
    },

    // 获取url参数
    getUrlParam: function (name) {
        var star_flag = this.url.search(name) + name.length + 1,
            end_flag = this.url.search(/&/),
            param = this.url.substring(star_flag, end_flag).split(',');
        arr = [];

        for (var i = 0; i < param.length; i++) {
            switch (param[i]) {
                case 'user_active':
                    arr.push('活跃会员数');
                    break;
                case 'order':
                    arr.push('订单数');
                    break;
                case 'payment':
                    arr.push('交易金额');
                    break;
            }
        }

        var options = {
            legend: {
                data: arr,
                padding: 8,
                x: 'right',
            }
        }

        this.legend_data = arr;

        return options;
    }
}