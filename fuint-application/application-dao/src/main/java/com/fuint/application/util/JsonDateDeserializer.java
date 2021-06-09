package com.fuint.application.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fuint.util.StringUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @Author: chenggang
 * @date: 2017/5/11
 * @Copyright:the Corporation of mianshui365
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public JsonDateDeserializer() {
    }

    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            String e = jsonParser.getText();
            return StringUtil.isBlank(e)?null:this.dateFormat.parse(e);
        } catch (Exception var5) {
            Calendar ca = Calendar.getInstance();
            ca.set(1970, 0, 1, 0, 0, 0);
            return ca.getTime();
        }
    }
}
