package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.CouponDto;
import com.fuint.common.dto.ReqCouponDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.bean.CouponNumBean;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 卡券业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CouponServiceImpl extends ServiceImpl<MtCouponMapper, MtCoupon> implements CouponService {

    @Resource
    private MtCouponMapper mtCouponMapper;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Resource
    private MtConfirmLogMapper mtConfirmLogMapper;

    @Resource
    private MtSendLogMapper mtSendLogMapper;

    @Resource
    private MtStoreMapper mtStoreMapper;

    @Resource
    private MtCouponGoodsMapper mtCouponGoodsMapper;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendSmsService sendSmsService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private WeixinService weixinService;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) {
        Page<MtCoupon> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCoupon> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCoupon::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtCoupon::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCoupon::getStatus, status);
        }
        String groupId = paginationRequest.getSearchParams().get("groupId") == null ? "" : paginationRequest.getSearchParams().get("groupId").toString();
        if (StringUtils.isNotBlank(groupId)) {
            lambdaQueryWrapper.eq(MtCoupon::getGroupId, groupId);
        }
        String type = paginationRequest.getSearchParams().get("type") == null ? "" : paginationRequest.getSearchParams().get("type").toString();
        if (StringUtils.isNotBlank(type)) {
            lambdaQueryWrapper.eq(MtCoupon::getType, type);
        }

        lambdaQueryWrapper.orderByDesc(MtCoupon::getId);
        List<MtCoupon> dataList = mtCouponMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtCoupon> paginationResponse = new PaginationResponse(pageImpl, MtCoupon.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存卡券信息
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存卡券信息")
    public MtCoupon saveCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException, ParseException {
        MtCoupon mtCoupon;

        if (reqCouponDto.getId() != null) {
            mtCoupon = mtCouponMapper.selectById(reqCouponDto.getId());
        } else {
            mtCoupon = new MtCoupon();
        }

        Date startTime = DateUtil.parseDate(reqCouponDto.getBeginTime());
        Date endTime = DateUtil.parseDate(reqCouponDto.getEndTime());
        if (endTime.before(startTime)) {
            throw new BusinessCheckException("生效期结束时间不能早于开始时间");
        }

        mtCoupon.setGroupId(reqCouponDto.getGroupId());
        if (reqCouponDto.getType() != null) {
            mtCoupon.setType(reqCouponDto.getType());
        }
        if (reqCouponDto.getName() != null) {
            mtCoupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        }
        if (reqCouponDto.getIsGive() != null) {
            mtCoupon.setIsGive(reqCouponDto.getIsGive().equals(1) ? true : false);
        }
        if (reqCouponDto.getPoint() != null) {
            mtCoupon.setPoint(reqCouponDto.getPoint());
        }
        if (mtCoupon.getPoint() == null) {
            mtCoupon.setPoint(0);
        }
        if (reqCouponDto.getLimitNum() != null) {
            mtCoupon.setLimitNum(reqCouponDto.getLimitNum());
        }
        if (mtCoupon.getLimitNum() == null) {
            mtCoupon.setLimitNum(1);
        }
        if (reqCouponDto.getReceiveCode() != null) {
            mtCoupon.setReceiveCode(reqCouponDto.getReceiveCode());
        }
        if (mtCoupon.getReceiveCode() == null) {
            mtCoupon.setReceiveCode("");
        }

        if (mtCoupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            if (reqCouponDto.getTimerPoint() != null) {
                mtCoupon.setPoint(reqCouponDto.getTimerPoint());
            }
            if (reqCouponDto.getTimerReceiveCode() != null) {
                mtCoupon.setReceiveCode(reqCouponDto.getTimerReceiveCode());
            }
        }

        mtCoupon.setStoreIds(reqCouponDto.getStoreIds());
        mtCoupon.setGradeIds(reqCouponDto.getGradeIds());

        if (reqCouponDto.getSendNum() == null) {
            reqCouponDto.setSendNum(1);
        }

        if (mtCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) || mtCoupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            mtCoupon.setSendWay(SendWayEnum.FRONT.getKey());
        } else {
            mtCoupon.setSendWay(reqCouponDto.getSendWay());
        }

        mtCoupon.setSendNum(reqCouponDto.getSendNum());

        if (reqCouponDto.getTotal() == null) {
            reqCouponDto.setTotal(0);
        }
        mtCoupon.setTotal(reqCouponDto.getTotal());

        mtCoupon.setBeginTime(DateUtil.parseDate(reqCouponDto.getBeginTime()));
        mtCoupon.setEndTime(DateUtil.parseDate(reqCouponDto.getEndTime()));
        mtCoupon.setExceptTime(CommonUtil.replaceXSS(reqCouponDto.getExceptTime()));
        mtCoupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        mtCoupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));
        mtCoupon.setInRule(CommonUtil.replaceXSS(reqCouponDto.getInRule()));
        mtCoupon.setOutRule(CommonUtil.replaceXSS(reqCouponDto.getOutRule()));
        mtCoupon.setApplyGoods(reqCouponDto.getApplyGoods());
        mtCoupon.setUseFor(reqCouponDto.getUseFor());

        if (null == reqCouponDto.getAmount()) {
            reqCouponDto.setAmount(new BigDecimal(0));
        }
        mtCoupon.setAmount(reqCouponDto.getAmount());
        String image = reqCouponDto.getImage();
        if (null == image || image.equals("")) {
            image = "";
        }

        mtCoupon.setImage(image);
        mtCoupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));

        if (reqCouponDto.getStatus() == null || StringUtil.isEmpty(reqCouponDto.getStatus())) {
            mtCoupon.setStatus(StatusEnum.ENABLED.getKey());
        } else {
            mtCoupon.setStatus(reqCouponDto.getStatus());
        }

        // 创建时间
        if (reqCouponDto.getId() == null) {
            mtCoupon.setCreateTime(new Date());
        }

        // 更新时间
        mtCoupon.setUpdateTime(new Date());

        // 操作人
        mtCoupon.setOperator(reqCouponDto.getOperator());

        if (mtCoupon.getId() == null) {
            this.save(mtCoupon);
        } else {
            mtCouponMapper.updateById(mtCoupon);
        }

        MtCoupon couponInfo = mtCouponMapper.selectById(mtCoupon.getId());

        // 更新已下发的会员卡券有效期
        if (couponInfo.getId() != null && reqCouponDto.getEndTime() != null && StringUtil.isNotEmpty(reqCouponDto.getEndTime())) {
            mtUserCouponMapper.updateExpireTime(couponInfo.getId(), reqCouponDto.getEndTime());
        }

        // 适用商品
        if (reqCouponDto.getGoodsIds() != null) {
           String[] goodsIds = reqCouponDto.getGoodsIds().split(",");
           if (goodsIds.length > 0) {
               // 1.先删除
               List<MtCouponGoods> couponGoodsList = mtCouponGoodsMapper.getCouponGoods(couponInfo.getId());
               for (MtCouponGoods cg : couponGoodsList) {
                   mtCouponGoodsMapper.deleteById(cg.getId());
               }
               // 2.再添加
               for (int n = 0; n < goodsIds.length; n++) {
                   if (StringUtil.isNotEmpty(goodsIds[n])) {
                       MtCouponGoods mtCouponGoods = new MtCouponGoods();
                       mtCouponGoods.setCouponId(couponInfo.getId());
                       mtCouponGoods.setGoodsId(Integer.parseInt(goodsIds[n]));
                       mtCouponGoods.setStatus(StatusEnum.ENABLED.getKey());
                       mtCouponGoods.setCreateTime(new Date());
                       mtCouponGoods.setUpdateTime(new Date());
                       mtCouponGoodsMapper.insert(mtCouponGoods);
                   }
               }
           }
        }

        // 如果是优惠券，并且是线下发放，生成会员卡券
        if (reqCouponDto.getId() == null && mtCoupon.getType().equals(CouponTypeEnum.COUPON.getKey()) && mtCoupon.getSendWay().equals(SendWayEnum.OFFLINE.getKey())) {
            Integer total = mtCoupon.getTotal() * mtCoupon.getSendNum();
            if (total > 0) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");

                for (int i = 1; i <= total; i++) {
                    MtUserCoupon userCoupon = new MtUserCoupon();
                    userCoupon.setCouponId(couponInfo.getId());
                    userCoupon.setGroupId(mtCoupon.getGroupId());
                    userCoupon.setMobile("");
                    userCoupon.setUserId(0);
                    userCoupon.setStatus(UserCouponStatusEnum.UNSEND.getKey());
                    userCoupon.setCreateTime(new Date());
                    userCoupon.setUpdateTime(new Date());
                    userCoupon.setExpireTime(couponInfo.getEndTime());
                    userCoupon.setUuid(uuid);
                    userCoupon.setType(CouponTypeEnum.COUPON.getKey());
                    userCoupon.setAmount(couponInfo.getAmount());
                    userCoupon.setBalance(couponInfo.getAmount());
                    userCoupon.setStoreId(0);
                    userCoupon.setOperator(reqCouponDto.getOperator());
                    userCoupon.setImage(couponInfo.getImage());

                    // 12位随机数
                    StringBuffer code = new StringBuffer();
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    userCoupon.setCode(code.toString());

                    mtUserCouponMapper.insert(userCoupon);
                }
            }
        }

        return mtCoupon;
    }

    /**
     * 根据ID获取券信息
     *
     * @param id 券ID
     * @throws BusinessCheckException
     */
    @Override
    public MtCoupon queryCouponById(Integer id) {
        return mtCouponMapper.selectById(id);
    }

    /**
     * 删除卡券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除卡券")
    public void deleteCoupon(Long id, String operator) {
        MtCoupon couponInfo = this.queryCouponById(id.intValue());
        if (null == couponInfo) {
            return;
        }

        couponInfo.setStatus(StatusEnum.DISABLE.getKey());

        // 修改时间
        couponInfo.setUpdateTime(new Date());

        // 操作人
        couponInfo.setOperator(operator);

        mtCouponMapper.updateById(couponInfo);

        return;
    }

    /**
     * 获取卡券列表
     * @param paramMap
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject findCouponList(Map<String, Object> paramMap) {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String status =  paramMap.get("status") == null ? StatusEnum.ENABLED.getKey(): paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "" : paramMap.get("type").toString();
        Integer userId =  paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        String needPoint =  paramMap.get("needPoint") == null ? "0" : paramMap.get("needPoint").toString();
        String sendWay =  paramMap.get("sendWay") == null ? "front" : paramMap.get("sendWay").toString();
        String sortType = paramMap.get("sortType") == null ? "createTime" : paramMap.get("sortType").toString();

        Page<MtCoupon> pageHelper = PageHelper.startPage(pageNumber, pageSize);
        LambdaQueryWrapper<MtCoupon> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if (StringUtil.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtCoupon::getStatus, status);
        }
        if (StringUtil.isNotEmpty(sendWay)) {
            lambdaQueryWrapper.eq(MtCoupon::getSendWay, sendWay);
        }
        if (StringUtil.isNotEmpty(type)) {
            lambdaQueryWrapper.eq(MtCoupon::getType, type);
        }
        if (Integer.parseInt(needPoint) > 0) {
            lambdaQueryWrapper.eq(MtCoupon::getPoint, 0);
        }

        lambdaQueryWrapper.orderByDesc(MtCoupon::getId);
        List<MtCoupon> dataList = mtCouponMapper.selectList(lambdaQueryWrapper);

        // 处理已过期
        int expireNum = 0;
        for (MtCoupon coupon : dataList) {
            if (coupon.getEndTime().before(new Date())) {
                coupon.setStatus(StatusEnum.EXPIRED.getKey());
                coupon.setUpdateTime(new Date());
                mtCouponMapper.updateById(coupon);
                expireNum++;
            }
        }

        List<CouponDto> content = new ArrayList<>();
        String baseImage = settingService.getUploadBasePath();
        for (int i = 0; i < dataList.size(); i++) {
            CouponDto item = new CouponDto();
            BeanUtils.copyProperties(dataList.get(i), item);
            item.setIsReceive(false);
            item.setImage(baseImage + item.getImage());

            // 是否领取，且领取量大于限制数
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey(), UserCouponStatusEnum.USED.getKey(), UserCouponStatusEnum.EXPIRE.getKey());
            List<MtUserCoupon> userCoupon = mtUserCouponMapper.getUserCouponListByCouponId(userId, item.getId(), statusList);
            if ((userCoupon.size() >= dataList.get(i).getLimitNum()) && (dataList.get(i).getLimitNum() > 0)) {
                item.setIsReceive(true);
                item.setUserCouponId(userCoupon.get(0).getId());
            }

            // 领取或预存数量
            CouponNumBean numData = mtUserCouponMapper.getPeopleNumByCouponId(item.getId());
            Long num;
            if (null == numData) {
                num = 0l;
            } else {
                num = numData.getNum();
            }
            item.setGotNum(num.intValue());

            // 剩余数量
            Integer leftNum = dataList.get(i).getTotal() - item.getGotNum();
            item.setLeftNum(leftNum >= 0 ? leftNum : 0);

            String sellingPoint = "";

            // 优惠券卖点
            if (item.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                if (StringUtil.isNotEmpty(item.getOutRule()) && Integer.parseInt(item.getOutRule()) > 0) {
                    sellingPoint = "满" + item.getOutRule() + "可用";
                } else {
                    sellingPoint = "无门槛券";
                }
            }

            // 预存券卖点
            if (item.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                if (StringUtil.isNotEmpty(item.getInRule())) {
                    String inRuleArr[] = item.getInRule().split(",");
                    if (inRuleArr.length > 0) {
                        for (int n = 0; n < inRuleArr.length; n++) {
                            String store[] = inRuleArr[n].split("_");
                            sellingPoint = "预存" + store[0] + "到账" + store[1];
                        }
                    }
                }
            }

            // 计次卡卖点
            if (item.getType().equals(CouponTypeEnum.TIMER.getKey()) && StringUtil.isNotEmpty(item.getOutRule())) {
                sellingPoint = "集满" + item.getOutRule() + "次即可";
            }

            item.setSellingPoint(sellingPoint);
            content.add(item);
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<CouponDto> paginationResponse = new PaginationResponse(pageImpl, CouponDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(content);

        return new ResponseObject(200, "查询成功", paginationResponse);
    }

    /**
     * 根据分组ID获取卡券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    public List<MtCoupon> queryCouponListByGroupId(Integer groupId) {
        List<MtCoupon> couponList = mtCouponMapper.queryByGroupId(groupId.intValue());
        return couponList;
    }

    /**
     * 发放卡券
     *
     * @param couponId 卡券ID
     * @param mobile   手机号
     * @param num      发放套数
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "发放卡券")
    public void sendCoupon(Integer couponId, String mobile, Integer num, String uuid, String operator) throws BusinessCheckException {
        MtUser userInfo = memberService.queryMemberByMobile(mobile);

        if (null == userInfo || !userInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该会员不存在或已禁用，请先注册会员");
        }

        MtCoupon couponInfo = this.queryCouponById(couponId);

        // 判断券是否有效
        if (!couponInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("卡券“"+couponInfo.getName()+"”已停用,不能发放");
        }

        // 发放的是储值卡
        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            if (StringUtil.isNotEmpty(couponInfo.getInRule())) {
                String storeParams = "";
                String[] paramArr = couponInfo.getInRule().split(",");
                for (int i = 0; i < paramArr.length; i++) {
                     if (StringUtil.isNotEmpty(storeParams)) {
                         storeParams = storeParams + "," + paramArr[i] + "_" + num;
                     } else {
                         storeParams = paramArr[i] + "_" + num;
                     }
                }

                Map<String, Object> param = new HashMap<>();
                param.put("couponId", couponInfo.getId());
                param.put("userId", userInfo.getId());
                param.put("param", storeParams);
                param.put("orderId", 0);

                userCouponService.preStore(param);
            }
            return;
        }

        // 优惠券或计次卡，发放num套
        for (int k = 1; k <= num; k++) {
            for (int j = 1; j <= couponInfo.getSendNum(); j++) {
                MtUserCoupon userCoupon = new MtUserCoupon();
                userCoupon.setCouponId(couponInfo.getId());
                userCoupon.setType(couponInfo.getType());
                userCoupon.setImage(couponInfo.getImage());
                userCoupon.setStoreId(userInfo.getStoreId());
                userCoupon.setAmount(couponInfo.getAmount());
                userCoupon.setBalance(couponInfo.getAmount());
                userCoupon.setOperator(operator);
                userCoupon.setGroupId(couponInfo.getGroupId());
                userCoupon.setMobile(mobile);
                userCoupon.setUserId(userInfo.getId());
                userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                userCoupon.setCreateTime(new Date());
                userCoupon.setUpdateTime(new Date());
                userCoupon.setExpireTime(couponInfo.getEndTime());

                // 12位随机数
                StringBuffer code = new StringBuffer();
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                userCoupon.setCode(code.toString());
                userCoupon.setUuid(uuid);
                mtUserCouponMapper.insert(userCoupon);
            }
        }

        // 发送小程序订阅消息
        if (userInfo != null && couponInfo != null && couponInfo.getAmount().compareTo(new BigDecimal("0")) > 0) {
            Date nowTime = new Date();
            Date sendTime = new Date(nowTime.getTime());
            Map<String, Object> params = new HashMap<>();
            params.put("name", couponInfo.getName());
            params.put("amount", couponInfo.getAmount());
            params.put("tips", "您的卡券已到账，请查收~");
            weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.COUPON_ARRIVAL.getKey(), "pages/user/index", params, sendTime);
        }
    }

    /**
     * 核销卡券
     *
     * @param userCouponId 用户卡券ID
     * @param userId  员工会员ID
     * @param storeId 店铺ID
     * @param orderId 订单ID
     * @param amount 核销金额
     * @param remark 核销备注
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "核销卡券")
    public String useCoupon(Integer userCouponId, Integer userId, Integer storeId, Integer orderId, BigDecimal amount, String remark) throws BusinessCheckException {
        MtUserCoupon userCoupon = mtUserCouponMapper.selectById(userCouponId.intValue());

        if (userCoupon == null) {
            throw new BusinessCheckException("该卡券不存在！");
        } else if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey()) && !userCoupon.getStatus().equals(UserCouponStatusEnum.UNSEND.getKey())) {
            throw new BusinessCheckException("该卡券状态有误，可能已使用或已过期！");
        }

        MtStore mtStore = null;
        if (storeId > 0) {
            mtStore = mtStoreMapper.selectById(storeId);
            if (null == mtStore) {
                throw new BusinessCheckException("该店铺不存在！");
            } else if (!mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                throw new BusinessCheckException("该店铺状态有误，可能已禁用");
            }
        }

        // 是否处于有效期
        MtCoupon couponInfo = this.queryCouponById(userCoupon.getCouponId());
        Date begin = couponInfo.getBeginTime();
        Date end = couponInfo.getEndTime();
        Date now = new Date();
        if (now.before(begin)) {
            throw new BusinessCheckException("该卡券还没到使用日期");
        }
        if (end.before(now)) {
            throw new BusinessCheckException("该卡券已过期");
        }

        // 是否在例外日期
        Calendar cal = Calendar.getInstance();
        Boolean isWeekend = false;
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            isWeekend = true;
        }

        String exceptTime = couponInfo.getExceptTime();
        if (null != exceptTime && !exceptTime.equals("")) {
            String[] exceptTimeList = exceptTime.split(",");
            if (exceptTimeList.length > 0) {
                for (String timeStr : exceptTimeList) {
                     if (timeStr.equals("weekend")) {
                         if (isWeekend) {
                             throw new BusinessCheckException("该卡券在当前日期不可用");
                         }
                     } else {
                         String[] timeItem = exceptTime.split("_");
                         if (timeItem.length == 2) {
                             try {
                                 Date startTime = DateUtil.parseDate(timeItem[0], "yyyy-MM-dd HH:mm");
                                 Date endTime = DateUtil.parseDate(timeItem[1], "yyyy-MM-dd HH:mm");
                                 if (now.before(endTime) && now.after(startTime)) {
                                     throw new BusinessCheckException("该卡券在当前日期不可用");
                                 }
                             } catch (ParseException pe) {
                                 throw new BusinessCheckException("该卡券在当前日期不可用.");
                             }
                         }
                     }
                }
            }
        }

        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            // 优惠券核销直接修改状态
            userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
        } else if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            // 储值卡核销，修改余额
            BigDecimal balance = userCoupon.getBalance();
            BigDecimal newBalance = balance.subtract(amount);

            if (newBalance.compareTo(new BigDecimal("0")) == -1) {
                throw new BusinessCheckException("余额不足，无法核销");
            }

            if (newBalance.compareTo(new BigDecimal("0")) == 0) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }

            userCoupon.setBalance(newBalance);
        } else if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            // 计次卡核销，增加核销次数至满
            Long confirmCount = confirmLogService.getConfirmNum(userCouponId);
            if ((confirmCount.intValue() + 1) >= Integer.parseInt(couponInfo.getOutRule())) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }
        }

        userCoupon.setUpdateTime(new Date());
        userCoupon.setUsedTime(new Date());
        userCoupon.setStoreId(storeId);
        mtUserCouponMapper.updateById(userCoupon);

        // 生成核销流水
        MtConfirmLog confirmLog = new MtConfirmLog();
        StringBuilder code = new StringBuilder();
        String sStoreId="00000"+storeId.toString();
        code.append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        code.append(sStoreId.substring(sStoreId.length()-4));
        code.append(SeqUtil.getRandomNumber(6));
        confirmLog.setCode(code.toString());
        confirmLog.setCouponId(couponInfo.getId());
        confirmLog.setUserCouponId(userCouponId.intValue());
        confirmLog.setOrderId(orderId);
        confirmLog.setCreateTime(new Date());
        confirmLog.setUpdateTime(new Date());
        confirmLog.setUserId(userCoupon.getUserId());
        confirmLog.setOperatorUserId(userId);
        MtUser userInfo = null;
        if (userId > 0) {
            userInfo = memberService.queryMemberById(userId);
            if (userInfo != null) {
                confirmLog.setOperator(userInfo.getName());
            }
        }
        confirmLog.setStoreId(storeId);
        confirmLog.setStatus(StatusEnum.ENABLED.getKey());

        // 优惠券核销金额
        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            amount = userCoupon.getAmount();
        }

        confirmLog.setAmount(amount);
        confirmLog.setRemark(remark);
        mtConfirmLogMapper.insert(confirmLog);

        try {
            // 发送核销短信
            List<String> mobileList = new ArrayList<>();
            mobileList.add(userCoupon.getMobile());
            Map<String, String> params = new HashMap<>();
            params.put("couponName", couponInfo.getName());
            if (mtStore != null){
                params.put("storeName", mtStore.getName());
            }
            params.put("sn", code.toString());
            sendSmsService.sendSms("confirm-coupon", mobileList, params);

            // 发送小程序订阅消息
            Date nowTime = new Date();
            Date sendTime = new Date(nowTime.getTime());
            Map<String, Object> param = new HashMap<>();
            String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
            params.put("name", couponInfo.getName());
            params.put("time", dateTime);
            weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.COUPON_CONFIRM.getKey(), "pages/user/index", param, sendTime);
        } catch (Exception e) {
            //empty
        }

        return confirmLog.getCode();
    }

    /**
     * 根据券ID删除会员卡券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除会员卡券")
    public void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException {
        MtUserCoupon userCoupon = this.mtUserCouponMapper.selectById(id);
        if (null == userCoupon) {
            return;
        }

        // 未使用状态才能作废删除
        if(!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            throw new BusinessCheckException("不能作废，该劵状态异常");
        }
        userCoupon.setStatus(UserCouponStatusEnum.DISABLE.getKey());

        // 修改时间
        userCoupon.setUpdateTime(new Date());

        // 操作人
        userCoupon.setOperator(operator);

        // 更新发券日志为部分作废状态
        this.mtSendLogMapper.updateSingleForRemove(userCoupon.getUuid(),UserCouponStatusEnum.USED.getKey());

        mtUserCouponMapper.updateById(userCoupon);
    }

    /**
     * 根据券ID 撤销卡券核销
     *
     * @param id             核销流水ID
     * @param userCouponId   用户卡券ID
     * @param operator       操作人
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "撤销卡券核销")
    public void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException {
        MtConfirmLog mtConfirmLog = this.mtConfirmLogMapper.selectById(id);
        MtUserCoupon userCoupon = this.mtUserCouponMapper.selectById(userCouponId);

        if (null == mtConfirmLog || !mtConfirmLog.getUserCouponId().equals(userCouponId)) {
            throw new BusinessCheckException("卡券核销流水不存在！");
        }

        if (null == userCoupon) {
            throw new BusinessCheckException("用户卡券不存在");
        }

        // 卡券未过期才能撤销,当前时间小于过期日期才能删除,48小时
        Calendar endTimecal = Calendar.getInstance();
        endTimecal.setTime(mtConfirmLog.getCreateTime());
        endTimecal.add(Calendar.DAY_OF_MONTH, 2);

        if (endTimecal.getTime().before(new Date())) {
            throw new BusinessCheckException("卡券核销已经超过48小时，无法撤销");
        }

        MtCoupon mtCoupon = mtCouponMapper.selectById(userCoupon.getCouponId());

        // 卡券未过期才能撤销,当前时间小于过期日期才能删除
        if (mtCoupon.getEndTime().before(new Date())) {
            throw new BusinessCheckException("卡券未过期才能撤销");
        }

        // 优惠券只有是使用状态且核销流水正常状态才能撤销
        if(userCoupon.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            if ((!userCoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey())) || (!mtConfirmLog.getStatus().equals(StatusEnum.ENABLED.getKey()))) {
                throw new BusinessCheckException("该劵状态异常，请稍后重试");
            }
        }

        // 回退至可用状态
        userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
        userCoupon.setStoreId(null);
        userCoupon.setUsedTime(null);
        userCoupon.setUpdateTime(new Date());

        // 如果是储值卡则返回余额
        if (userCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            BigDecimal balance = userCoupon.getBalance();
            BigDecimal amount = mtConfirmLog.getAmount();
            if (amount.compareTo(new BigDecimal("0")) > 0) {
                BigDecimal newBalance = balance.add(amount);
                userCoupon.setBalance(newBalance);
            }
        }

        // 更新用户卡券
        mtUserCouponMapper.updateById(userCoupon);

        // 更新流水
        mtConfirmLog.setOperator(operator);
        mtConfirmLog.setStatus(StatusEnum.DISABLE.getKey());
        mtConfirmLog.setUpdateTime(new Date());
        mtConfirmLog.setCancelTime(new Date());

        mtConfirmLogMapper.updateById(mtConfirmLog);
    }

    /**
     * 根据ID获取用户卡券信息
     * @param userCouponId 查询参数
     * @throws BusinessCheckException
     * */
    @Override
    public MtUserCoupon queryUserCouponById(Integer userCouponId) {
        MtUserCoupon userCoupon = this.mtUserCouponMapper.selectById(userCouponId);
        return userCoupon;
    }

    /**
     * 根据批次撤销卡券
     *
     * @param uuid       批次ID
     * @param operator   操作人
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "根据批次撤销卡券")
    public void removeUserCoupon(Long id, String uuid, String operator) {
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("uuid", uuid);
        List<MtUserCoupon> paginationResponse = mtUserCouponMapper.selectByMap(searchParams);

        Integer total = paginationResponse.size();

        List<Integer> coupondIdList = mtUserCouponMapper.getCouponIdsByUuid(uuid);
        List<Integer> couponIds = new ArrayList<>();
        couponIds.add(0);

        Date nowDate = new Date();

        for (int i = 0; i < coupondIdList.size(); i++) {
            Integer couponId = coupondIdList.get(i);
            MtCoupon couponInfo = this.queryCouponById(couponId);
            if (couponInfo.getStatus().equals("A") && couponInfo.getEndTime().after(nowDate)) {
                couponIds.add(couponId);
            }
        }

        Integer row = this.mtUserCouponMapper.removeUserCoupon(uuid, couponIds, operator);
        if (row.compareTo( total.intValue()) != -1) {
            this.mtSendLogMapper.updateForRemove(uuid, UserCouponStatusEnum.DISABLE.getKey(), total.intValue(), 0);
        } else {
            this.mtSendLogMapper.updateForRemove(uuid, UserCouponStatusEnum.USED.getKey(), row, (total.intValue()-row));
        }

        return;
    }

    /**
     * 判断卡券码是否过期
     * @param code 12位券码
     * @throws BusinessCheckException
     * */
    @Override
    public boolean codeExpired(String code) {
        if (StringUtil.isEmpty(code)) {
            return true;
        }
        try {
            Date dateTime = DateUtil.parseDate(code.substring(0, 14), "yyyyMMddHHmmss");

            Long time = dateTime.getTime();
            Long nowTime = System.currentTimeMillis();

            Long seconds = (nowTime - time) / 1000;
            // 超过1小时
            if (seconds > 3600) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    /**
     * 判断卡券是否过期
     * @param coupon
     * @return
     * */
    @Override
    public boolean isCouponEffective(MtCoupon coupon) {
        Date begin = coupon.getBeginTime();
        Date end = coupon.getEndTime();
        Date now = new Date();

        // 未生效
        if (begin != null) {
            if (now.before(begin)) {
                return false;
            }
        }

        // 已过期
        if (end != null) {
            if (now.after(end)) {
                return false;
            }
        }

        if (coupon.getStatus() == null) {
            return false;
        }

        // 状态异常
        if (!coupon.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return false;
        }

        return true;
    }
}
