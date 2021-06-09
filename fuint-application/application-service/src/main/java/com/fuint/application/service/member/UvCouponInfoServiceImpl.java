package com.fuint.application.service.member;

import com.fuint.application.util.Base64Util;
import com.fuint.application.util.QRCodeUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.application.dao.entities.UvCouponInfo;
import com.fuint.application.dao.repositories.MtCouponInfoRepository;
import com.fuint.application.dto.CouponTotalDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员卡券统计业务实现类
 * Created by zach 20190808
 */
@Service
public class UvCouponInfoServiceImpl implements UvCouponInfoService {

    private static final Logger log = LoggerFactory.getLogger(UvCouponInfoServiceImpl.class);

    @Autowired
    private MtCouponInfoRepository mtCouponInfoRepository;

    @Autowired
    private Environment env;

    @PersistenceContext(unitName = "defaultPersistenceUnit")
    private EntityManager entityManager;

    /**
     * 分页查询会员卡券消费列表 SQL
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<UvCouponInfo> queryCouponInfoListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Map<String, Object> params = paginationRequest.getSearchParams();

        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage() - 1, paginationRequest.getPageSize());
        CouponTotalDto couponTotalDto = this.getTotal(params);
        Long total=couponTotalDto.getCouponTotal();
        params.put("BeginRow",pageRequest.getOffset());
        params.put("PageSize",pageRequest.getPageSize());
        Query query = getQueryByParams(params);
        List<UvCouponInfo> content = convert(query);

        Page page = new PageImpl(content, pageRequest, total.longValue());
        PaginationResponse pageResponse = new PaginationResponse(page, UvCouponInfo.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);
        return pageResponse;
    }

    /**
     *根据创建时间参数查询会员用户信息
     *
     * @param  params
     * @throws BusinessCheckException
     */
    @Override
    public List<UvCouponInfo> queryCouponInfoByParams(Map<String, Object> params) throws BusinessCheckException {
        log.info("############ 根据参数查询卡券使用情况信息 #################.");
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }
        Query query = getQueryByParams(params);
        List<UvCouponInfo> result = convert(query);

        return result;
    }

    /**
     *查询会员卡券总计
     *
     * @param  params
     * @throws BusinessCheckException
     */
    @Override
    public CouponTotalDto queryCouponInfoTotalByParams(Map<String, Object> params) throws BusinessCheckException {
        log.info("############ 根据参数查询卡券统计情况信息 #################.");
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }
        CouponTotalDto couponTotalDto = this.getTotal(params);
        return couponTotalDto;
    }

    /**
     * 根据ID获取用户卡券信息
     *
     * @param id 用户卡券id
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public UvCouponInfo queryUvCouponInfoById(Integer id) throws BusinessCheckException {
        UvCouponInfo uvCouponInfo = mtCouponInfoRepository.findOne(id);
        if (null == uvCouponInfo || StatusEnum.DISABLE.getKey().equals(uvCouponInfo.getCouponInfoStatus())) {
            log.error("该卡券不存在或已被删除"+id.toString());
            throw new BusinessCheckException("该卡券不存在或已被删除");
        }
        return uvCouponInfo;
    }


    /**
     * 根据参数获取查询Query对象
     *
     * @param params
     * @return
     */
    private Query getQueryByParams(Map<String, Object> params) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT\n" +
                "\t`mt_user_coupon`.`ID` AS `ID`,\n" +
                "\t`mt_user_coupon`.`CODE` AS `CODE`,\n" +
                "\t`mt_user_coupon`.`COUPON_ID` AS `COUPON_ID`,\n" +
                "\t`mt_user_coupon`.`USER_ID` AS `USER_ID`,\n" +
                "\tif(NOW() BETWEEN `mt_coupon`.`BEGIN_TIME` and `mt_coupon`.`END_TIME` or NOW() < `mt_coupon`.`BEGIN_TIME`,`mt_user_coupon`.`STATUS`,if(`mt_user_coupon`.`STATUS`='B','B','C')) AS `COUPON_INFO_STATUS`,\n" +
                "  '' as COUPON_INFO_STATUS_DESC,\n" +
                "\t`mt_user_coupon`.`STORE_ID` AS `STORE_ID`,\n" +
                "\t`mt_user_coupon`.`USED_TIME` AS `USED_TIME`,\n" +
                "\t`mt_user_coupon`.`CREATE_TIME` AS `CREATE_TIME`,\n" +
                "\t`mt_user_coupon`.`UPDATE_TIME` AS `UPDATE_TIME`,\n" +
                "  `mt_user_coupon`.`UUID` AS `UUID`,\n" +
                "  `mt_user`.MOBILE,\n" +
                "\t`mt_user`.`REAL_NAME` AS `REAL_NAME`,\n" +
                "  CONCAT(',',`mt_coupon`.STORE_IDS,',') AS SUIT_STORE_IDS,\n" +
                "\t`mt_coupon`.`NAME` AS `coupon_name`,\n" +
                "\t`mt_coupon`.`IMAGE` AS `coupon_image`,\n" +
                "\t`mt_coupon`.`AMOUNT` AS `MONEY`,\n" +
                "\t`mt_coupon`.`GROUP_ID` AS `GROUP_ID`,\n" +
                "\t`mt_coupon`.`STATUS` AS `coupon_status`,\n" +
                "  `mt_coupon`.`BEGIN_TIME`,\n" +
                "  `mt_coupon`.`END_TIME`,\n" +
                "\t`mt_coupon_group`.`NAME` AS `coupon_group_name`,\n" +
                "\t`mt_store`.`NAME` AS `store_Name`\n" +
                "FROM\t`mt_user_coupon`\n" +
                "JOIN `mt_coupon` ON `mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID` \n" +
                "JOIN `mt_coupon_group` ON `mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`\n" +
                "JOIN `mt_user` ON `mt_user_coupon`.`USER_ID` = `mt_user`.`ID`\n" +
                "left JOIN `mt_store` ON `mt_user_coupon`.`STORE_ID` = `mt_store`.`ID`\n" +
                " where 1=1 ");

        if (params.get("LIKE_mobile") != null && StringUtils.isNotEmpty(params.get("LIKE_mobile").toString())) {
            queryStr.append(" and  `mt_user`.MOBILE like '%" + CommonUtil.filter(params.get("LIKE_mobile").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponGroupName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponGroupName").toString())) {
            queryStr.append(" and `mt_coupon_group`.`NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponGroupName").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponName").toString())) {
            queryStr.append(" and  `mt_coupon`.`NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponName").toString().trim()) + "%' ");
        }

        if (params.get("GTE_usedTime") != null && StringUtils.isNotEmpty(params.get("GTE_usedTime").toString())) {
            queryStr.append(" and `mt_user_coupon`.`USED_TIME` >= '" + CommonUtil.filter(params.get("GTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("LTE_usedTime") != null && StringUtils.isNotEmpty(params.get("LTE_usedTime").toString())) {
            queryStr.append(" and  `mt_user_coupon`.`USED_TIME` <= '" + CommonUtil.filter(params.get("LTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("EQ_couponInfoStatus") != null && StringUtils.isNotEmpty(params.get("EQ_couponInfoStatus").toString())) {
            String eq_couponInfoStatus = CommonUtil.filter(params.get("EQ_couponInfoStatus").toString());
            String now = DateUtil.format(new Date(), DateUtil.newFormat);
            //1、未开始 2进行中、3、已过期
            if (eq_couponInfoStatus.equals(UserCouponStatusEnum.EXPIRE.getKey())) {
                queryStr.append(" and (now()> `mt_coupon`.`END_TIME`)");
            } else {
                queryStr.append(" and `mt_user_coupon`.`STATUS`='" + eq_couponInfoStatus + "' ");
            }
        }

        if (params.get("EQ_groupId") != null && StringUtils.isNotEmpty(params.get("EQ_groupId").toString())) {
            queryStr.append(" and `mt_coupon`.`GROUP_ID` =  '" + CommonUtil.filter(params.get("EQ_groupId").toString().trim()) + "' ");
        }

        if (params.get("EQ_couponId") != null && StringUtils.isNotEmpty(params.get("EQ_couponId").toString())) {
            queryStr.append(" and `mt_user_coupon`.`COUPON_ID` =  '" + CommonUtil.filter(params.get("EQ_couponId").toString().trim()) + "' ");
        }

        if (params.get("EQ_storeId") != null && StringUtils.isNotEmpty(params.get("EQ_storeId").toString())) {
            queryStr.append(" and `mt_user_coupon`.`STORE_ID` =  '" + CommonUtil.filter(params.get("EQ_storeId").toString().trim()) + "' ");
        }

        if (params.get("LIKE_suitStoreIds") != null && StringUtils.isNotEmpty(params.get("LIKE_suitStoreIds").toString())) {
            queryStr.append(" and FIND_IN_SET('" + CommonUtil.filter(params.get("LIKE_suitStoreIds").toString().trim()) + "',`mt_coupon`.STORE_IDS)");
        }

        if (params.get("sort_type_custom") != null && StringUtils.isNotEmpty(params.get("sort_type_custom").toString())) {
            queryStr.append(" order by "+ CommonUtil.filter(params.get("sort_type_custom").toString().trim()));
        } else {
            queryStr.append(" order by `mt_user_coupon`.`UPDATE_TIME` desc");
        }

        if (params.get("limit_rownum_custom") != null && StringUtils.isNotEmpty(params.get("limit_rownum_custom").toString())) {
            queryStr.append(" limit "+ params.get("limit_rownum_custom").toString().trim());
        } else {
            if (params.get("BeginRow") != null && StringUtils.isNotEmpty(params.get("BeginRow").toString())) {
                queryStr.append(" limit " + params.get("BeginRow").toString().trim() + "," + params.get("PageSize").toString().trim());
            }
        }

        return entityManager.createNativeQuery(queryStr.toString());
    }

    /**
     * 根据参数获取查询Query对象
     *
     * @param params
     * @return
     */
    private CouponTotalDto getTotal(Map<String, Object> params) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT\n count(*) as rowTotal,\n" +
                " sum(if(mt_user_coupon.`STATUS`='A',1,0)) as unUsedTotal,\n" +
                "sum(if(mt_user_coupon.`STATUS`='B',1,0)) as usedTotal,\n" +
                "sum(if(if(NOW() BETWEEN `mt_coupon`.`BEGIN_TIME` and `mt_coupon`.`END_TIME` or NOW() < `mt_coupon`.`BEGIN_TIME`,`mt_user_coupon`.`STATUS`,if(`mt_user_coupon`.`STATUS`='B','B','C'))='C',1,0)) as expireTotal,\n" +
                "sum(if(mt_user_coupon.`STATUS`='D',1,0)) as disableTotal \n" +
                "FROM\t`mt_user_coupon`\n" +
                "JOIN `mt_coupon` ON `mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID` \n" +
                "JOIN `mt_coupon_group` ON `mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`\n" +
                "JOIN `mt_user` ON `mt_user_coupon`.`USER_ID` = `mt_user`.`ID`\n" +
                "left JOIN `mt_store` ON `mt_user_coupon`.`STORE_ID` = `mt_store`.`ID` \n" +
                " where 1=1 ");

        if (params.get("LIKE_mobile") != null && StringUtils.isNotEmpty(params.get("LIKE_mobile").toString())) {
            queryStr.append(" and  `mt_user`.MOBILE like '%" + CommonUtil.filter(params.get("LIKE_mobile").toString().trim()) + "%' ");
        }

        if (params.get("LIKE_couponGroupName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponGroupName").toString())) {
            queryStr.append(" and `mt_coupon_group`.`NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponGroupName").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponName").toString())) {
            queryStr.append(" and  `mt_coupon`.`NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponName").toString().trim()) + "%' ");
        }

        if (params.get("GTE_usedTime") != null && StringUtils.isNotEmpty(params.get("GTE_usedTime").toString())) {
            queryStr.append(" and `mt_user_coupon`.`USED_TIME` >= '" + CommonUtil.filter(params.get("GTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("LTE_usedTime") != null && StringUtils.isNotEmpty(params.get("LTE_usedTime").toString())) {
            queryStr.append(" and  `mt_user_coupon`.`USED_TIME` <= '" + CommonUtil.filter(params.get("LTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("EQ_couponInfoStatus") != null && StringUtils.isNotEmpty(params.get("EQ_couponInfoStatus").toString())) {
            String eq_couponInfoStatus = CommonUtil.filter(params.get("EQ_couponInfoStatus").toString());
            String now = DateUtil.format(new Date(), DateUtil.newFormat);
            //1、未开始 2进行中、3、已过期
            if (eq_couponInfoStatus.equals(UserCouponStatusEnum.EXPIRE.getKey())) {
                queryStr.append(" and (now()> `mt_coupon`.`END_TIME`)");
            } else {
                queryStr.append(" and `mt_user_coupon`.`STATUS`='" + eq_couponInfoStatus + "' ");
            }
        }

        if (params.get("EQ_groupId") != null && StringUtils.isNotEmpty(params.get("EQ_groupId").toString())) {
            queryStr.append(" and `mt_coupon`.`GROUP_ID` =  '" + CommonUtil.filter(params.get("EQ_groupId").toString().trim()) + "' ");
        }

        if (params.get("EQ_couponId") != null && StringUtils.isNotEmpty(params.get("EQ_couponId").toString())) {
            queryStr.append(" and `mt_user_coupon`.`COUPON_ID` =  '" + CommonUtil.filter(params.get("EQ_couponId").toString().trim()) + "' ");
        }

        if (params.get("EQ_storeId") != null && StringUtils.isNotEmpty(params.get("EQ_storeId").toString())) {
            queryStr.append(" and `mt_user_coupon`.`STORE_ID` =  '" + CommonUtil.filter(params.get("EQ_storeId").toString().trim()) + "' ");
        }

        if (params.get("LIKE_suitStoreIds") != null && StringUtils.isNotEmpty(params.get("LIKE_suitStoreIds").toString())) {
            queryStr.append(" and FIND_IN_SET('" + CommonUtil.filter(params.get("LIKE_suitStoreIds").toString().trim()) + "',`mt_coupon`.STORE_IDS)");
        }

        Query query = entityManager.createNativeQuery(queryStr.toString());
        List<Object[]> contentObj= query.getResultList();
        if (null != contentObj) {
            if (CollectionUtils.isNotEmpty(contentObj)) {
                CouponTotalDto couponTotalDto = new CouponTotalDto();
                for (Object[] objArray : contentObj) {
                    couponTotalDto.setCouponTotal(null != objArray[0] ? Long.parseLong(objArray[0].toString()) : null);
                    couponTotalDto.setUnUsedTotal(null != objArray[1] ? Long.parseLong(objArray[1].toString()) : null);
                    couponTotalDto.setUsedTotal(null != objArray[2] ? Long.parseLong(objArray[2].toString()) : null);
                    couponTotalDto.setExpireTotal(null != objArray[3] ? Long.parseLong(objArray[3].toString()) : null);
                    couponTotalDto.setDisableTotal(null != objArray[4] ? Long.parseLong(objArray[4].toString()) : null);
                }
                return couponTotalDto;
            }
        }
        return  new CouponTotalDto();
    }

    /**
     * 根据Query设置的参数查询并封装成实体对象
     *
     * @param query
     * @return
     */
    private List<UvCouponInfo> convert(Query query) {
        List<Object[]> contentObj = query.getResultList();
        List<UvCouponInfo> content = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contentObj)) {
            for (Object[] objArray : contentObj) {
                UvCouponInfo uvCouponInfo = new UvCouponInfo();
                uvCouponInfo.setId(null != objArray[0] ? Integer.parseInt(objArray[0].toString()) : null);
                uvCouponInfo.setCode(null != objArray[1] ? objArray[1].toString() : null);
                uvCouponInfo.setCouponId(null != objArray[2] ? Integer.parseInt(objArray[2].toString()) : null);
                uvCouponInfo.setUserId(null != objArray[3] ? Integer.parseInt(objArray[3].toString()) : null);
                uvCouponInfo.setCouponInfoStatus(null != objArray[4] ? objArray[4].toString() : "");
                uvCouponInfo.setCouponInfoStatusDesc(null != objArray[5] ? objArray[5].toString() : "");
                uvCouponInfo.setStoreId(null != objArray[6] ? Integer.parseInt(objArray[6].toString()) : null);
                uvCouponInfo.setUsedTime(null != objArray[7] ? (Date) objArray[7] : null);
                uvCouponInfo.setCreateTime(null != objArray[8] ? (Date) objArray[8] : null);
                uvCouponInfo.setUpdateTime(null != objArray[9] ? (Date) objArray[9] : null);
                uvCouponInfo.setUuid(null != objArray[10] ? objArray[10].toString() : "");
                uvCouponInfo.setMobile(null != objArray[11] ? objArray[11].toString() : "");
                uvCouponInfo.setRealName(null != objArray[12] ? objArray[12].toString() : "");
                uvCouponInfo.setSuitStoreIds(null != objArray[13] ? objArray[13].toString() : "");
                uvCouponInfo.setCouponName(null != objArray[14] ? objArray[14].toString() : "");
                uvCouponInfo.setCouponImage(null != objArray[15] ? objArray[15].toString() : "");
                uvCouponInfo.setMoney(null != objArray[16] ? new BigDecimal(objArray[16].toString()) : null);
                uvCouponInfo.setGroupId(null != objArray[17] ? Integer.parseInt(objArray[17].toString()) : null);
                uvCouponInfo.setCouponStatus(null != objArray[18] ? objArray[18].toString() : "");
                uvCouponInfo.setBeginTime(null != objArray[19] ? (Date) objArray[19] : null);
                uvCouponInfo.setEndTime(null != objArray[20] ? (Date) objArray[20] : null);
                uvCouponInfo.setCouponGroupName(null != objArray[21] ? objArray[21].toString() : "");
                uvCouponInfo.setStoreName(null != objArray[22] ? objArray[22].toString() : "");

                // 生成效果图 todo 多线程
                String website = env.getProperty("website.url");
                String code = uvCouponInfo.getCode();
                String codeContent  = website + "/index.html#/result?code=" + code +"&time=" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String source = uvCouponInfo.getCouponImage();
                QRCodeUtil.createQrCode(out, codeContent, 120, 120, "png", source);
                try {
                    String img = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
                    uvCouponInfo.setCouponImage(img);
                } catch (Exception e ) {
                    // empty
                }

                content.add(uvCouponInfo);
            }
        }
        return content;
    }
}
