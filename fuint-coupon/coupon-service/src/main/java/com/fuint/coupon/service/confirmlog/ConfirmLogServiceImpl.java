package com.fuint.coupon.service.confirmlog;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.coupon.dao.entities.UvCouponInfo;
import com.fuint.coupon.dao.repositories.MtConfirmLogRepository;
import com.fuint.coupon.dao.repositories.MtCouponInfoRepository;
import com.fuint.coupon.dto.ConfirmLogDto;
import com.fuint.coupon.enums.StatusEnum;
import com.fuint.coupon.enums.UserCouponStatusEnum;
import com.fuint.coupon.service.member.UvCouponInfoService;
import com.fuint.coupon.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * 核销优惠券视图
 * Created by zach 20191012
 */
@Service
public class ConfirmLogServiceImpl implements ConfirmLogService{

    private static final Logger log = LoggerFactory.getLogger(ConfirmLogServiceImpl.class);

    @Autowired
    private MtConfirmLogRepository mtConfirmLogRepository;

    @PersistenceContext(unitName = "defaultPersistenceUnit")
    private EntityManager entityManager;

    /**
     * 分页查询会员优惠券消费列表 SQL
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Map<String, Object> params = paginationRequest.getSearchParams();
        Query query = getQueryByParams(params);
        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage() - 1, paginationRequest.getPageSize());
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<ConfirmLogDto> content = convertConfirmLog(query);
        Long total = this.getTotal(params);
        Page page = new PageImpl(content, pageRequest, total.longValue());
        PaginationResponse pageResponse = new PaginationResponse(page, UvCouponInfo.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);
        return pageResponse;
    }


    /**
     *根据间参数查询会创建时员用户信息
     *
     * @param  params
     * @throws BusinessCheckException
     */
    @Override
    public List<ConfirmLogDto> queryConfirmLogListByParams(Map<String, Object> params) throws BusinessCheckException {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }
        Query query = getQueryByParams(params);
        List<ConfirmLogDto> result = convertConfirmLog(query);
        return result;
    }


    /**
     * 根据ID获取用户优惠券信息
     *
     * @param id 用户优惠券id
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public ConfirmLogDto queryConfirmLogById(Integer id) throws BusinessCheckException {
        Map<String, Object> params=new HashMap<>();
        params.put("EQ_Id",id);
        Query query = getQueryByParams(params);
        List<ConfirmLogDto> result = convertConfirmLog(query);
        ConfirmLogDto ConfirmLogDto = result.get(0);
        return ConfirmLogDto;
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
                "   `mt_confirm_log`.`ID` AS `ID`,\n" +
                "   `mt_confirm_log`.`CODE` AS `CODE`,\n" +
                "   `mt_confirm_log`.`STATUS` AS `ConfirmStatus`,\n" +
                "   `mt_confirm_log`.`USER_COUPON_ID` AS `UserCouponId`,\n" +
                "\t`mt_confirm_log`.`CREATE_TIME` AS `ConfirmTime`,\n" +
                "\t`mt_user_coupon`.`COUPON_ID` AS `COUPON_ID`,\n" +
                "\t`mt_user_coupon`.`USER_ID` AS `USER_ID`,\n" +
                "\tif(NOW() BETWEEN `mt_coupon`.`BEGIN_TIME` and `mt_coupon`.`END_TIME`,`mt_user_coupon`.`STATUS`,if(`mt_user_coupon`.`STATUS`='B','B','C')) AS `COUPON_INFO_STATUS`,\n" +
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
                "\t`mt_coupon`.`MONEY` AS `MONEY`,\n" +
                "\t`mt_coupon`.`GROUP_ID` AS `GROUP_ID`,\n" +
                "\t`mt_coupon`.`STATUS` AS `coupon_status`,\n" +
                "  `mt_coupon`.`BEGIN_TIME`,\n" +
                "  `mt_coupon`.`END_TIME`,\n" +
                "\t`mt_coupon_group`.`NAME` AS `coupon_group_name`,\n" +
                "\t`mt_store`.`NAME` AS `store_Name`,\n" +
                "   `mt_confirm_log`.`CANCEL_TIME` AS `CancelTime`,\n" +
                "   DATE_ADD(`mt_confirm_log`.`CREATE_TIME`,INTERVAL 48 HOUR) as EndCancelTime\n" +
                "FROM\t`mt_confirm_log`\n" +
                "JOIN `mt_user_coupon` ON `mt_confirm_log`.`USER_COUPON_ID` = `mt_user_coupon`.`ID` \n" +
                "JOIN `mt_coupon` ON `mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID` \n" +
                "JOIN `mt_coupon_group` ON `mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`\n" +
                "JOIN `mt_user` ON `mt_confirm_log`.`USER_ID` = `mt_user`.`ID`\n" +
                "Inner JOIN `mt_store` ON `mt_confirm_log`.`STORE_ID` = `mt_store`.`ID`\n" +
                " where 1=1 ");

        if (params.get("EQ_Code") != null && StringUtils.isNotEmpty(params.get("EQ_Code").toString())) {
            queryStr.append(" and `mt_confirm_log`.CODE = '" + CommonUtil.filter(params.get("EQ_Code").toString().trim()) + "' ");
        }
        if (params.get("LIKE_mobile") != null && StringUtils.isNotEmpty(params.get("LIKE_mobile").toString())) {
            queryStr.append(" and `mt_user`.MOBILE like '%" + CommonUtil.filter(params.get("LIKE_mobile").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponGroupName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponGroupName").toString())) {
            queryStr.append(" and `mt_coupon_group`.`NAME_BACKEND` like '%" + CommonUtil.filter(params.get("LIKE_couponGroupName").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponName").toString())) {
            queryStr.append(" and `mt_coupon`.`BACKEND_NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponName").toString().trim()) + "%' ");
        }

        if (params.get("GTE_usedTime") != null && StringUtils.isNotEmpty(params.get("GTE_usedTime").toString())) {
            queryStr.append(" and `mt_confirm_log`.`CREATE_TIME` >= '" + CommonUtil.filter(params.get("GTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("LTE_usedTime") != null && StringUtils.isNotEmpty(params.get("LTE_usedTime").toString())) {
            queryStr.append(" and `mt_confirm_log`.`CREATE_TIME` <= '" + CommonUtil.filter(params.get("LTE_usedTime").toString().trim()) + "' ");
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
            queryStr.append(" and `mt_confirm_log`.`STORE_ID` =  '" + CommonUtil.filter(params.get("EQ_storeId").toString().trim()) + "' ");
        }

        if (params.get("sort_type_custom") != null && StringUtils.isNotEmpty(params.get("sort_type_custom").toString())) {
            queryStr.append(" order by "+ CommonUtil.filter(params.get("sort_type_custom").toString().trim()));
        }
        else
        {
            queryStr.append(" order by `mt_confirm_log`.`ID` desc");
        }

        if (params.get("limit_rownum_custom") != null && StringUtils.isNotEmpty(params.get("limit_rownum_custom").toString())) {
            queryStr.append(" limit "+ params.get("limit_rownum_custom").toString().trim());
        }

        return entityManager.createNativeQuery(queryStr.toString());
    }



    /**
     * 根据参数获取查询Query对象
     *
     * @param params
     * @return
     */
    private Long getTotal(Map<String, Object> params) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT\n count(*) as rowTotal \n" +
                "FROM\t`mt_confirm_log`\n" +
                "JOIN `mt_user_coupon` ON `mt_confirm_log`.`USER_COUPON_ID` = `mt_user_coupon`.`ID` \n" +
                "JOIN `mt_coupon` ON `mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID` \n" +
                "JOIN `mt_coupon_group` ON `mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`\n" +
                "JOIN `mt_user` ON `mt_confirm_log`.`USER_ID` = `mt_user`.`ID`\n" +
                "inner JOIN `mt_store` ON `mt_confirm_log`.`STORE_ID` = `mt_store`.`ID`\n" +
                "where 1=1 ");

        if (params.get("EQ_Code") != null && StringUtils.isNotEmpty(params.get("EQ_Code").toString())) {
            queryStr.append(" and `mt_confirm_log`.CODE = '" + CommonUtil.filter(params.get("EQ_Code").toString().trim()) + "' ");
        }
        if (params.get("LIKE_mobile") != null && StringUtils.isNotEmpty(params.get("LIKE_mobile").toString())) {
            queryStr.append(" and  `mt_user`.MOBILE like '%" +CommonUtil.filter( params.get("LIKE_mobile").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponGroupName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponGroupName").toString())) {
            queryStr.append(" and `mt_coupon_group`.`NAME_BACKEND` like '%" + CommonUtil.filter(params.get("LIKE_couponGroupName").toString().trim()) + "%' ");
        }
        if (params.get("LIKE_couponName") != null && StringUtils.isNotEmpty(params.get("LIKE_couponName").toString())) {
            queryStr.append(" and  `mt_coupon`.`BACKEND_NAME` like '%" + CommonUtil.filter(params.get("LIKE_couponName").toString().trim()) + "%' ");
        }

        if (params.get("GTE_usedTime") != null && StringUtils.isNotEmpty(params.get("GTE_usedTime").toString())) {
            queryStr.append(" and `mt_confirm_log`.`CREATE_TIME` >= '" +CommonUtil.filter(params.get("GTE_usedTime").toString().trim()) + "' ");
        }
        if (params.get("LTE_usedTime") != null && StringUtils.isNotEmpty(params.get("LTE_usedTime").toString())) {
            queryStr.append(" and  `mt_confirm_log`.`CREATE_TIME` <= '" + CommonUtil.filter(params.get("LTE_usedTime").toString().trim()) + "' ");
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
            queryStr.append(" and `mt_confirm_log`.`STORE_ID` =  '" + CommonUtil.filter(params.get("EQ_storeId").toString().trim()) + "' ");
        }


        Query query = entityManager.createNativeQuery(queryStr.toString());
        Object object = query.getSingleResult();
        if (null != object) {
            return new Long(object.toString());
        }
        return 0L;
    }



    /**
     * 根据Query设置的参数查询并封装成实体核销优惠券流水对象
     *
     * @param query
     * @return
     */
    private List<ConfirmLogDto> convertConfirmLog(Query query) {
        List<Object[]> contentObj = query.getResultList();
        List<ConfirmLogDto> content = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contentObj)) {
            for (Object[] objArray : contentObj) {
                ConfirmLogDto ConfirmLogDto = new ConfirmLogDto();
                ConfirmLogDto.setId(null != objArray[0] ? Integer.parseInt(objArray[0].toString()) : null);
                ConfirmLogDto.setCode(null != objArray[1] ? objArray[1].toString() : null);
                ConfirmLogDto.setConfirmStatus(null != objArray[2] ? objArray[2].toString() : null);
                ConfirmLogDto.setUserCouponId(null != objArray[3] ? Integer.parseInt(objArray[3].toString()) : null);
                ConfirmLogDto.setConfirmTime(null != objArray[4] ? (Date) objArray[4] : null);
                ConfirmLogDto.setCouponId(null != objArray[5] ? Integer.parseInt(objArray[5].toString()) : null);
                ConfirmLogDto.setUserId(null != objArray[6] ? Integer.parseInt(objArray[6].toString()) : null);
                ConfirmLogDto.setCouponInfoStatus(null != objArray[7] ? objArray[7].toString() : "");
                ConfirmLogDto.setCouponInfoStatusDesc(null != objArray[8] ? objArray[8].toString() : "");
                ConfirmLogDto.setStoreId(null != objArray[9] ? Integer.parseInt(objArray[9].toString()) : null);
                ConfirmLogDto.setUsedTime(null != objArray[10] ? (Date) objArray[10] : null);
                ConfirmLogDto.setCreateTime(null != objArray[11] ? (Date) objArray[11] : null);
                ConfirmLogDto.setUpdateTime(null != objArray[12] ? (Date) objArray[12] : null);
                ConfirmLogDto.setUuid(null != objArray[13] ? objArray[13].toString() : "");
                ConfirmLogDto.setMobile(null != objArray[14] ? objArray[14].toString() : "");
                ConfirmLogDto.setRealName(null != objArray[15] ? objArray[15].toString() : "");
                ConfirmLogDto.setSuitStoreIds(null != objArray[16] ? objArray[16].toString() : "");
                ConfirmLogDto.setCouponName(null != objArray[17] ? objArray[17].toString() : "");
                ConfirmLogDto.setMoney(null != objArray[18] ? new BigDecimal(objArray[18].toString()) : null);
                ConfirmLogDto.setGroupId(null != objArray[19] ? Integer.parseInt(objArray[19].toString()) : null);
                ConfirmLogDto.setCouponStatus(null != objArray[20] ? objArray[20].toString() : "");
                ConfirmLogDto.setBeginTime(null != objArray[21] ? (Date) objArray[21] : null);
                ConfirmLogDto.setEndTime(null != objArray[22] ? (Date) objArray[22] : null);
                ConfirmLogDto.setCouponGroupName(null != objArray[23] ? objArray[23].toString() : "");
                ConfirmLogDto.setStoreName(null != objArray[24] ? objArray[24].toString() : "");
                ConfirmLogDto.setCancelTime(null != objArray[25] ? (Date) objArray[25] : null);
                ConfirmLogDto.setEndCancelTime(null != objArray[26] ? (Date) objArray[26] : null);
                content.add(ConfirmLogDto);
            }
        }
        return content;
    }

}
