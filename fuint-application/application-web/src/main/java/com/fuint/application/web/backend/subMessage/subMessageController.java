package com.fuint.application.web.backend.subMessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dto.ParamDto;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.WxMessageEnum;
import com.fuint.application.service.setting.SettingService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.SubMessageDto;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.core.env.Environment;
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

        List<SubMessageDto> dataList = new ArrayList<>();
        for (WxMessageEnum wxMessageEnum : WxMessageEnum.values()) {
            SubMessageDto e = new SubMessageDto();
            MtSetting setting = settingService.querySettingByName(wxMessageEnum.getKey());
            e.setKey(wxMessageEnum.getKey());
            e.setTitle(wxMessageEnum.getValue());

            JSONObject jsonObject = null;
            if (setting != null) {
                try {
                    jsonObject = JSONObject.parseObject(setting.getValue());
                } catch (Exception ex) {
                    // empty
                }
                if (jsonObject != null) {
                    String templateId = jsonObject.get("templateId").toString();
                    String tid = jsonObject.get("tid").toString();
                    JSONArray paramArray = (JSONArray) JSONObject.parse(jsonObject.get("params").toString());
                    if (StringUtil.isEmpty(templateId) || StringUtil.isEmpty(tid) || paramArray.size() < 1) {
                        jsonObject = null;
                    }
                }
            }

            if (setting != null && jsonObject != null) {
                e.setStatus(setting.getStatus());
            } else {
                e.setStatus(StatusEnum.FORBIDDEN.getKey());
            }
            dataList.add(e);
        }

        model.addAttribute("dataList", dataList);

        return "subMessage/index";
    }

    /**
     * 编辑消息
     *
     * @param key  消息键值
     * @return
     */
    @RequiresPermissions("backend/subMessage/edit/{key}")
    @RequestMapping(value = "/edit/{key}")
    public String edit(@PathVariable("key") String key, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String name = WxMessageEnum.getValue(key);

        if (StringUtil.isNotEmpty(name)) {
            MtSetting mtSetting = settingService.querySettingByName(key);
            JSONObject jsonObject = null;
            try {
                if (mtSetting != null && mtSetting.getValue().indexOf('}') > 0) {
                    jsonObject = JSONObject.parseObject(mtSetting.getValue());
                }
                String templateId = "";
                String tid = "";
                JSONArray paramArray = null;

                if (jsonObject != null) {
                    templateId = jsonObject.get("templateId").toString();
                    tid = jsonObject.get("tid").toString();
                    paramArray = (JSONArray) JSONObject.parse(jsonObject.get("params").toString());
                }

                List<ParamDto> params = new ArrayList<>();
                String tplConfigJson = env.getProperty("weixin.subMessage." + key);
                tplConfigJson = new String(tplConfigJson.getBytes("ISO8859-1"), "UTF-8");

                if (StringUtil.isNotEmpty(tplConfigJson)) {
                    JSONArray jsonArray = (JSONArray)JSONObject.parse(tplConfigJson);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        ParamDto dto = new ParamDto();
                        dto.setKey(obj.get("key").toString());
                        dto.setName(obj.get("name").toString());
                        if (paramArray != null) {
                            dto.setValue("");
                            for (int j = 0; j < paramArray.size(); j++) {
                                 JSONObject paraObj = paramArray.getJSONObject(j);
                                 if (paraObj.get("key").toString().equals(obj.get("key").toString())) {
                                     dto.setValue(paraObj.get("value").toString());
                                 }
                            }
                        } else {
                            dto.setValue("");
                        }
                        params.add(dto);
                    }
                }

                model.addAttribute("params", params);
                model.addAttribute("name", name);
                model.addAttribute("key", key);
                model.addAttribute("templateId", templateId);
                model.addAttribute("tid", tid);
            } catch (Exception e) {
                //empty
            }
        }

        return "subMessage/edit";
    }

    /**
     * 保存消息设置
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/subMessage/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String key = request.getParameter("key");
        String templateId = request.getParameter("templateId");
        String tid = request.getParameter("tid");

        SubMessageDto dto = new SubMessageDto();
        dto.setKey(key);
        dto.setTemplateId(templateId);
        dto.setTid(tid);
        dto.setStatus(StatusEnum.ENABLED.getKey());

        try {
            String tplConfigJson = env.getProperty("weixin.subMessage." + key);
            JSONArray jsonArray = (JSONArray) JSONObject.parse(tplConfigJson);
            List<ParamDto> params = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String value = request.getParameter(obj.get("key").toString());
                ParamDto para = new ParamDto();

                String name = obj.get("name").toString();
                name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                para.setName(name);
                para.setKey(obj.get("key").toString());
                para.setValue(value);
                params.add(para);
            }
            dto.setParams(params);
            String json = JSONObject.toJSONString(dto);

            // 保存
            settingService.removeSetting(key);
            MtSetting info = new MtSetting();
            info.setType(SettingTypeEnum.SUB_MESSAGE.getKey());
            info.setName(key);
            info.setValue(json);

            String description = WxMessageEnum.getValue(key);
            info.setDescription(description);
            info.setOperator(shiroUser.getAcctName());
            info.setUpdateTime(new Date());
            settingService.saveSetting(info);
        }  catch (Exception e) {
            //empty
        }

        return "redirect:/backend/subMessage/index";
    }
}
