package com.fuint.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 后台账号信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountInfo implements Serializable {
    private Integer id;
    private String accountKey;
    private String accountName;
    private int accountStatus;
    private String isActive;
    private Date createDate;
    private Date modifyDate;
    private String salt;
    private String roleIds;
    private int locked;
    private int ownerId;
    private String realName;
    private Integer storeId;
    private String storeName;
    private Integer staffId;
    private String token;
}
