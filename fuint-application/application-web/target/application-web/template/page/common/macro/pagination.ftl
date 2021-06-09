<!-- 分页组件 -->
<!-- recordBean 分页查询结果Bean
     action 分页请求URL
     formName 分页需要的查询参数的表单名称 -->
<#macro pagination recordBean action formName area>
    <#if recordBean?has_content && recordBean.totalElements?exists && recordBean.totalElements gt 0>
        <#assign pageSize=recordBean.pageSize>
        <#assign pageIndex=recordBean.currentPage>
        <#assign pageCount=recordBean.totalPages>
        <#assign numberOfElements = recordBean.numberOfElements>
        <#assign totalElements = recordBean.totalElements>
        <#if (pageIndex>pageCount)>
            <#assign pageIndex=pageCount>
        </#if>
        <#if (pageIndex lt 0)>
            <#assign pageIndex=1>
        </#if>
        <#if area??>
            <#else>
            <#assign area="displayArea">
        </#if>
        <#assign currentNum = (pageIndex-1)*pageSize>
    <div class="dataTables_info" id="data-table_info" role="status" aria-live="polite">Showing ${currentNum+1}
        to ${currentNum + numberOfElements} of ${totalElements} rows
    </div>
    <div class="dataTables_paginate paging_simple_numbers" id="data-table_paginate">

        <#if pageIndex == 1>
            <a class="paginate_button previous disabled" title="上一页" aria-controls="data-table" data-dt-idx="0"
               tabindex="0" id="data-table_previous">Previous</a><span>
        </#if>
        <#if pageIndex gt 1>
            <a href="javascript:postDataByForm('${action}?current_page=${pageIndex-1}','${area}','${formName}')"
               title="上一页" class="paginate_button previous" aria-controls="data-table" data-dt-idx="0" tabindex="0"
               id="data-table_previous">Previous</a><span>
        </#if>
    <#--如果前面页数过多,显示"..."-->
        <#if (pageIndex>1)>
            <#assign prevPages=pageIndex-5>
            <#if prevPages <= 0>
                <#assign prevPages=1>
            </#if>
            <#assign start=pageIndex>
            <a href="javascript:postDataByForm('${action}?current_page=${prevPages}','${area}','${formName}')"
               title="第${prevPages}页">...</a></li>
        <#else>
            <#assign start=1>
        </#if>
    <#-- 显示当前页附近的页-->
        <#assign end=pageIndex+5>
        <#if (end>pageCount)>
            <#assign end=pageCount>
        </#if>
        <#list start..end as index>
            <#if pageIndex==index>
                <a href="javascript:void(0);" class="paginate_button current" aria-controls="data-table"
                   data-dt-idx="${index}"><span>${index}</span></a>
            <#else>
                <a href="javascript:postDataByForm('${action}?current_page=${index}','${area}','${formName}')"
                   class="paginate_button" aria-controls="data-table" data-dt-idx="${index}"><span>${index}</span></a>
            </#if>
        </#list>
    <#--如果后面页数过多,显示"...":-->
        <#if (end lt pageCount)>
            <#assign endend=end+pageSize>
            <#if (end>pageCount)>
                <#assign end=pageCount>
            </#if>
            <a href="javascript:postDataByForm('${action}?current_page=${end}','${area}','${formName}')"
               class="paginate_button" aria-controls="data-table"
               title="第${end}页">...</a>
        </#if>
    <#-- 显示"下一页":-->
        <#if (pageIndex lt pageCount)>
            <a href="javascript:postDataByForm('${action}?current_page=${pageIndex+1}','${area}','${formName}')"
               title="下一页" class="paginate_button next" aria-controls="data-table" data-dt-idx="7"><span>Next</span></a>
            <a href="javascript:postDataByForm('${action}?current_page=${pageCount}','${area}','${formName}')"
               class="paginate_button next" aria-controls="data-table"
               title="尾页"><span>Last</span></a>
        </#if>
        <#if (pageIndex == pageCount)>
            <a href="javascript:void(0);"
               title="下一页" class="paginate_button next disabled" aria-controls="data-table"
               data-dt-idx="7"><span>Next</span></a>
            <a href="javascript:void(0);"
               class="paginate_button next disabled" aria-controls="data-table"
               title="尾页"><span>Last</span></a>
        </#if>
    </#if>
</#macro>