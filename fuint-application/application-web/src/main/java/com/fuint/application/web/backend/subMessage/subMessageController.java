package com.fuint.application.web.backend.subMessage;

import com.alibaba.fastjson.JSONObject;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.WxMessageEnum;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.SubMessageDto;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 订阅消息管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/subMessage")
public class subMessageController {

    /**
     * 微信相关接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    @Autowired
    private Environment env;

    /**
     * 订阅消息管理
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/subMessage/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        List<SubMessageDto> templateList = weixinService.getSubMessageTemplateList();

        List<SubMessageDto> dataList = new ArrayList<>();
        for (WxMessageEnum wxMessageEnum : WxMessageEnum.values()) {
             SubMessageDto e = new SubMessageDto();
             e.setKey(wxMessageEnum.getKey());
             e.setTitle(wxMessageEnum.getValue());
             e.setStatus(StatusEnum.DISABLE.getKey());
             // 读取模板ID
             MtSetting setting = settingService.querySettingByName(wxMessageEnum.getKey());
             if (setting != null) {
                 for (SubMessageDto tpl : templateList) {
                     if (tpl.getTemplateId().equals(setting.getValue())) {
                         e.setContent(tpl.getContent());
                         e.setStatus(StatusEnum.ENABLED.getKey());
                     }
                 }
             }
             dataList.add(e);
        }

        model.addAttribute("dataList", dataList);

        return "subMessage/index";
    }

    /**
     * 启用消息
     *
     * @param key  消息键值
     * @return
     */
    @RequiresPermissions("backend/subMessage/doActive/{key}")
    @RequestMapping(value = "/doActive/{key}")
    public String doActive(@PathVariable("key") String key) throws BusinessCheckException {
        String description = WxMessageEnum.getValue(key);
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();

        if (StringUtils.isNotEmpty(description)) {
            MtSetting data = settingService.querySettingByName(key);
            String tplConfigJson = env.getProperty("weixin.subMessage." + key);
            JSONObject tplConfigObject = (JSONObject) JSONObject.parse(tplConfigJson);
            String tid = tplConfigObject.get("tid").toString();
            String kidList[] = tplConfigObject.get("kidList").toString().split(",");
            if (data == null && StringUtils.isNotEmpty(tid)) {
                Map<String, Object> reqData = new HashMap<>();
                reqData.put("tid", tid);
                reqData.put("kidList", kidList);
                reqData.put("sceneDesc", description);
                String templateId = weixinService.addSubMessageTemplate(reqData);
                if (StringUtils.isNotEmpty(templateId)) {
                    // 先删除旧的
                    settingService.removeSetting(key);

                    MtSetting info = new MtSetting();
                    info.setType(SettingTypeEnum.SUB_MESSAGE.getKey());
                    info.setName(key);
                    info.setValue(templateId);
                    info.setDescription(description);
                    info.setOperator(operator);
                    info.setUpdateTime(new Date());
                    settingService.saveSetting(info);
                }
            }
        }

        return "redirect:/backend/subMessage/index";
    }

    /**
     * 禁用消息
     *
     * @param key  消息键值
     * @return
     */
    @RequiresPermissions("backend/subMessage/doRemove/{key}")
    @RequestMapping(value = "/doRemove/{key}")
    public String doRemove(@PathVariable("key") String key) throws BusinessCheckException {
        settingService.removeSetting(key);
        return "redirect:/backend/subMessage/index";
    }
}
