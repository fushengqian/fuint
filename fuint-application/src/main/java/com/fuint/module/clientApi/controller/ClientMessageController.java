package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.service.MessageService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtMessage;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 消息相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-消息相关接口")
@RestController
@RequestMapping(value = "/clientApi/message")
public class ClientMessageController extends BaseController {

    /**
     * 消息服务接口
     */
    @Autowired
    private MessageService messageService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 查询最新一条未读消息
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getOne(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getSuccessResult(false);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getSuccessResult(false);
        }

        MtMessage messageInfo = messageService.getOne(mtUser.getId());
        Map<String, Object> outParams = new HashMap();
        if (messageInfo != null) {
            outParams.put("msgId", messageInfo.getId());
            outParams.put("title", messageInfo.getTitle());
            outParams.put("content", messageInfo.getContent());
        }

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 将消息置为已读
     */
    @RequestMapping(value = "/readed", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject readed(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer msgId =  request.getParameter("msgId") == null ? 0 :Integer.parseInt(request.getParameter("msgId"));

        if (null == mtUser) {
            return getSuccessResult(false);
        }

        messageService.readMessage(msgId);

        ResponseObject responseObject = getSuccessResult(true);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 微信推送消息
     */
    @RequestMapping(value = "/wxPush", method = RequestMethod.GET)
    @CrossOrigin
    public String wxPush(HttpServletRequest request) {
        String echostr =  request.getParameter("echostr") == null ? "" : request.getParameter("echostr");

        if (StringUtil.isNotEmpty(echostr)) {
            return echostr;
        }

        return "";
    }

    /**
     * 微信订阅消息模板
     */
    @RequestMapping(value = "/getSubTemplate", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getSubTemplate(HttpServletRequest request) throws BusinessCheckException {
        String keys =  request.getParameter("keys") == null ? "" :request.getParameter("keys");

        List<String> dataList = new ArrayList<>();

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.SUB_MESSAGE.getKey());
        for (MtSetting mtSetting : settingList) {
            if (keys.indexOf(mtSetting.getName()) >= 0) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(mtSetting.getValue());
                    if (jsonObject != null) {
                        String templateId = jsonObject.get("templateId").toString();
                        if (StringUtil.isNotEmpty(templateId)) {
                            dataList.add(templateId);
                        }
                    }
                } catch (Exception e) {
                    // empty
                }
            }
        }

        ResponseObject responseObject = getSuccessResult(dataList);
        return getSuccessResult(responseObject.getData());
    }
}
