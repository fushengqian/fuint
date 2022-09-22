package com.fuint.application;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import com.fuint.util.StringUtil;

/**
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public class PropertiesUtil {

    public static final ResourceBundle messageResource = ResourceBundle.getBundle("international.message", Locale.getDefault());

    /**
     * 获取请求返回Code对应的Message
     * @param code
     * @param params
     * @return
     */
    public static String getResponseErrorMessageByCode(int code, String...params) {
        String pStr = messageResource.getString("response.error." + code);
        if(StringUtil.isEmpty(pStr)) return "";
        if(params == null || params.length == 0) return pStr;
        MessageFormat format = new MessageFormat(pStr, Locale.getDefault());
        return format.format(params);
    }

    /**
     * 根据Key值获取Value
     * @param key
     * @param params
     * @return
     */
    public static String getValueByKey(String key, String...params) {
        String pStr = messageResource.getString(key);
        if(StringUtil.isEmpty(pStr)) return "";
        if(params == null || params.length == 0) return pStr;
        MessageFormat format = new MessageFormat(pStr, Locale.getDefault());
        return format.format(params);
    }

}
