package com.fuint.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 *
 * Created by: FSQ
 * CopyRight https://www.fuint.cn
 */
public class IpUtil {

    public static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    /**
     * 校验IP是否在指定的段
     *
     * @param ipSection IP网段，如： 10.167.7.1-10.167.7.255
     * @param ip        Ip地址，如：10.167.7.56
     * @return boolean
     */
    public static boolean ipIsValid(String ipSection, String ip) {
        if (ipSection == null) {
            throw new NullPointerException("IP段不能为空！");
        }

        if (ip == null) {
            throw new NullPointerException("IP不能为空！");
        }

        ipSection = ipSection.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 校验IP是否在指定的段列表
     *
     * @param ip
     * @param ipSections
     * @return boolean
     */
    public static boolean ipIsValid(String ip, String... ipSections) {
        for (String ipSection : ipSections) {
            if (ipIsValid(ipSection, ip)) {
                return true;
            }
        }
        return false;
    }
}
