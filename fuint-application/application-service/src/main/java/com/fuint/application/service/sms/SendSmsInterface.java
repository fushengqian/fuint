package com.fuint.application.service.sms;

import com.fuint.application.dao.entities.MtSmsSendedLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 发送短信接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface SendSmsInterface {

    /**
     * 发送短信方法
     * @param template_uname    短信模板英文名称
     * @param phones            手机号码集合
     * @return Map<Boolean,List<String>>    TRUE:推送成功的手机号码集合；
     *                                      FALSE:推送失败的手机号码集合
     * @throws Exception
     */
    Map<Boolean,List<String>> sendSms(String template_uname, List<String> phones, Map<String, String> contentParams) throws Exception;

    /**
     * 分页已发短信列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsSendedLog> querySmsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;
}
