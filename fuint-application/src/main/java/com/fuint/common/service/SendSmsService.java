package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtSmsSendedLog;

import java.util.List;
import java.util.Map;

/**
 * 发送短信接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SendSmsService {

    /**
     * 发送短信方法
     * @param tUname    短信模板英文名称
     * @param phones    手机号码集合
     * @return Map<Boolean,List<String>>    TRUE:推送成功的手机号码集合；
     *                                      FALSE:推送失败的手机号码集合
     * @throws Exception
     */
    Map<Boolean, List<String>> sendSms(String tUname, List<String> phones, Map<String, String> contentParams) throws BusinessCheckException;

    /**
     * 分页已发短信列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsSendedLog> querySmsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;
}
