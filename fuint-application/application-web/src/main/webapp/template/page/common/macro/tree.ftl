<!--
 actionUrl 树结构加载的数据URL
 title 显示的标题
 selected 树结构需要选中的ID字符串,逗号分割. 例如:1,2,3,4
 -->
<#macro tree actionUrl title selected>
<link href="<@sp.static/>/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" type="text/css"/>

<script src="<@sp.static/>/js/jquery/ztree/jquery.ztree.core.js" type="text/javascript" charset="UTF-8"></script>
<script src="<@sp.static/>/js/jquery/ztree/jquery.ztree.excheck.js" type="text/javascript" charset="UTF-8"></script>
<script language="JavaScript">
    var setting = {
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true
            }
        }
    };

    $(document).ready(function () {
        //加载数据
        $.load();
        $.ajax({
            type: "GET",
            url: "${actionUrl}",
            success: function (data) {
                var sources = "${selected}";
                if(!$.isBlank(sources)){
                    //遍历结果,设置选中项
                    var s= sources.split(",");
                    for(var i = 0 ; i < data.length ; i ++){
                        var id = data[i].id;
                        for(var k = 0 ; k < s.length ; k ++){
                            if(id == s[k]){
                                data[i].checked = true;
                                data[i].open = true;
                            }
                        }
                    }
                }
                $.fn.zTree.init($("#treeArea"), setting, data);
                $.close();
            },
            error: function (msg) {
                $.close();
                $.error("<h2>错误:</h2><p>加载错误(" + msg.status + ")</p>");
                return;
            }
        });
    });
</script>

<h4>${title}</h4>
<div>
    <ul id="treeArea" class="ztree">

    </ul>
</div>
</#macro>