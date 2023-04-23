package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.OpenGiftDto;
import com.fuint.common.enums.MessageEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtOpenGiftMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 开卡赠礼接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class OpenGiftServiceImpl extends ServiceImpl<MtOpenGiftMapper, MtOpenGift> implements OpenGiftService {

    @Resource
    private MtOpenGiftMapper mtOpenGiftMapper;

    @Resource
    private MtUserMapper mtUserMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserGradeService userGradeService;

    @Autowired
    private PointService pointService;

    @Autowired
    private MessageService messageService;

    /**
     * 获取开卡赠礼列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject getOpenGiftList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());

        Page<MtOpenGift> pageHelper = PageHelper.startPage(pageNumber, pageSize);
        LambdaQueryWrapper<MtOpenGift> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtOpenGift::getStatus, StatusEnum.DISABLE.getKey());

        String couponId = paramMap.get("couponId") == null ? "" : paramMap.get("couponId").toString();
        if (StringUtils.isNotBlank(couponId)) {
            lambdaQueryWrapper.eq(MtOpenGift::getCouponId, couponId);
        }
        String gradeId = paramMap.get("gradeId") == null ? "" : paramMap.get("gradeId").toString();
        if (StringUtils.isNotBlank(gradeId)) {
            lambdaQueryWrapper.eq(MtOpenGift::getGradeId, Integer.parseInt(gradeId));
        }
        String status = paramMap.get("status") == null ? "" : paramMap.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtOpenGift::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtOpenGift::getId);
        List<MtOpenGift> openGiftList = mtOpenGiftMapper.selectList(lambdaQueryWrapper);
        List<OpenGiftDto> dataList = new ArrayList<>();
        for (MtOpenGift item : openGiftList) {
            OpenGiftDto dto = this._dealDetail(item);
            dataList.add(dto);
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<OpenGiftDto> paginationResponse = new PaginationResponse(pageImpl, OpenGiftDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return new ResponseObject(200, "", paginationResponse);
    }

    /**
     * 新增开卡赠礼
     *
     * @param  mtOpenGift
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增开卡赠礼")
    public MtOpenGift addOpenGift(MtOpenGift mtOpenGift) {
        mtOpenGift.setUpdateTime(new Date());
        mtOpenGift.setCreateTime(new Date());
        this.save(mtOpenGift);
        return mtOpenGift;
    }

    /**
     * 根据ID获取开卡赠礼详情
     *
     * @param  id 开卡赠礼ID
     * @throws BusinessCheckException
     */
    @Override
    public OpenGiftDto getOpenGiftDetail(Integer id) throws BusinessCheckException {
        MtOpenGift openGift = mtOpenGiftMapper.selectById(id);
        return this._dealDetail(openGift);
    }

    /**
     * 根据ID删除数据
     *
     * @param  id       开卡赠礼ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除开卡赠礼")
    public void deleteOpenGift(Integer id, String operator) {
        MtOpenGift MtOpenGift = mtOpenGiftMapper.selectById(id);
        if (null == MtOpenGift) {
            return;
        }

        MtOpenGift.setStatus(StatusEnum.DISABLE.getKey());
        MtOpenGift.setUpdateTime(new Date());

        mtOpenGiftMapper.updateById(MtOpenGift);
    }

    /**
     * 更新开卡赠礼
     *
     * @param  reqDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新开卡赠礼")
    public MtOpenGift updateOpenGift(MtOpenGift reqDto) throws BusinessCheckException {
        MtOpenGift MtOpenGift = mtOpenGiftMapper.selectById(reqDto.getId());
        if (MtOpenGift == null) {
            throw new BusinessCheckException("该数据状态异常");
        }

        MtOpenGift.setId(reqDto.getId());
        MtOpenGift.setUpdateTime(new Date());

        if (null != reqDto.getOperator()) {
            MtOpenGift.setOperator(reqDto.getOperator());
        }

        if (null != reqDto.getStatus()) {
            MtOpenGift.setStatus(reqDto.getStatus());
        }

        if (null != reqDto.getCouponId()) {
            MtOpenGift.setCouponId(reqDto.getCouponId());
        }

        if (null != reqDto.getGradeId()) {
            MtOpenGift.setGradeId(reqDto.getGradeId());
        }

        if (null != reqDto.getPoint()) {
            MtOpenGift.setPoint(reqDto.getPoint());
        }

        if (null != reqDto.getCouponNum()) {
            MtOpenGift.setCouponNum(reqDto.getCouponNum());
        }

        mtOpenGiftMapper.updateById(MtOpenGift);
        return MtOpenGift;
    }

    /**
     * 开卡赠礼
     * @param userId
     * @param gradeId
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openGift(Integer userId, Integer gradeId) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("grade_id", gradeId.toString());
        params.put("status", StatusEnum.ENABLED.getKey());

        MtUser user = mtUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessCheckException("会员状态异常");
        }

        // 保存会员等级
        if (Integer.parseInt(user.getGradeId()) != gradeId) {
            user.setGradeId(gradeId.toString());
            // 设置有效期
            MtUserGrade gradeInfo = userGradeService.queryUserGradeById(gradeId);
            if (gradeInfo.getValidDay() > 0) {
                user.setStartTime(new Date());
                Date endDate = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(endDate);
                calendar.add(calendar.DATE, gradeInfo.getValidDay());
                endDate = calendar.getTime();
                user.setEndTime(endDate);
            }
            user.setUpdateTime(new Date());
            mtUserMapper.updateById(user);
        }

        List<MtOpenGift> openGiftList = mtOpenGiftMapper.selectByMap(params);

        if (openGiftList.size() > 0) {
            Integer totalPoint = 0;
            BigDecimal totalAmount = new BigDecimal("0");
           for(MtOpenGift item : openGiftList) {
               // 加积分
               if (item.getPoint() > 0) {
                   MtPoint reqPointDto = new MtPoint();
                   reqPointDto.setUserId(userId);
                   reqPointDto.setAmount(item.getPoint());
                   reqPointDto.setDescription("开卡赠送"+ item.getPoint() +"积分");
                   reqPointDto.setOperator("系统");
                   pointService.addPoint(reqPointDto);
                   totalPoint = totalPoint + item.getPoint();
               }
               // 返卡券
               if (item.getCouponId() > 0) {
                   try {
                       MtCoupon mtCoupon = couponService.queryCouponById(item.getCouponId());
                       if (mtCoupon != null && mtCoupon.getStatus() == StatusEnum.ENABLED.getKey()) {
                           Map<String, Object> param = new HashMap<>();
                           param.put("couponId", item.getCouponId());
                           param.put("userId", userId);
                           param.put("num", item.getCouponNum());
                           userCouponService.receiveCoupon(param);
                           totalAmount = totalAmount.add(mtCoupon.getAmount());
                       }
                   } catch (BusinessCheckException e) {
                       // empty
                   }
               }
           }

           // 弹框消息
           MtMessage msg = new MtMessage();
           msg.setType(MessageEnum.POP_MSG.getKey());
           msg.setUserId(userId);
           msg.setTitle("温馨提示");
           msg.setSendTime(new Date());
           msg.setIsSend(YesOrNoEnum.YES.getKey());
           msg.setParams("");
           if (totalAmount.compareTo(new BigDecimal("0")) > 0 && totalPoint > 0) {
               msg.setContent("系统赠送您价值￥" + totalAmount + "卡券和" + totalPoint + "积分，请注意查收！");
               messageService.addMessage(msg);
           } else if(totalAmount.compareTo(new BigDecimal("0")) > 0) {
               msg.setContent("系统赠送您价值" + totalAmount + "卡券，请注意查收！");
               messageService.addMessage(msg);
           } else if(totalPoint > 0) {
               msg.setContent("系统赠送您" + totalPoint + "积分，请注意查收！");
               messageService.addMessage(msg);
           }
        }

        return;
    }

    /**
     * 处理详情
     * @param  openGiftInfo
     * @return OpenGiftDto
     * */
    private OpenGiftDto _dealDetail(MtOpenGift openGiftInfo) throws BusinessCheckException {
        OpenGiftDto dto = new OpenGiftDto();

        dto.setId(openGiftInfo.getId());
        dto.setCreateTime(DateUtil.formatDate(openGiftInfo.getCreateTime(), "yyyy.MM.dd HH:mm"));
        dto.setUpdateTime(DateUtil.formatDate(openGiftInfo.getUpdateTime(), "yyyy.MM.dd HH:mm"));
        dto.setStatus(openGiftInfo.getStatus());
        dto.setCouponNum(openGiftInfo.getCouponNum());
        dto.setPoint(openGiftInfo.getPoint());
        dto.setOperator(openGiftInfo.getOperator());

        MtCoupon couponInfo = couponService.queryCouponById(openGiftInfo.getCouponId());
        dto.setCouponInfo(couponInfo);

        MtUserGrade gradeInfo = userGradeService.queryUserGradeById(openGiftInfo.getGradeId());
        dto.setGradeInfo(gradeInfo);

        return dto;
    }
}
