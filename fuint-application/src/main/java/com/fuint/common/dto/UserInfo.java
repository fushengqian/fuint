package com.fuint.common.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 会员登录信息实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserInfo implements Serializable {
    private Integer id;
    private String token;
}
