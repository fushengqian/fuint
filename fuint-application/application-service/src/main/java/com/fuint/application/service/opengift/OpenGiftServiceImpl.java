package com.fuint.application.service.opengift;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtOpenGiftRepository;
import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.dto.*;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.message.MessageService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 开卡赠礼接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class OpenGiftServiceImpl extends BaseService implements OpenGiftService {

    private static final Logger log = LoggerFactory.getLogger(OpenGiftServiceImpl.class);

    @Autowired
    private MtOpenGiftRepository openGiftRepository;

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

    @Autowired
    private MtUserRepository userRepository;

    /**
     * 获取开卡赠礼列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getOpenGiftList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String couponId = paramMap.get("couponId") == null ? "" : paramMap.get("couponId").toString();
        String gradeId = paramMap.get("gradeId") == null ? "" : paramMap.get("gradeId").toString();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtils.isNotEmpty(couponId)) {
            searchParams.put("EQ_couponId", couponId);
        }
        if (StringUtils.isNotEmpty(gradeId)) {
            searchParams.put("EQ_gradeId", gradeId);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"status asc", "createTime desc"});
        PaginationResponse<MtOpenGift> paginationResponse = openGiftRepository.findResultsByPagination(paginationRequest);

        List<OpenGiftDto> dataList = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (MtOpenGift item : paginationResponse.getContent()) {
                OpenGiftDto dto = this._dealDetail(item);
                dataList.add(dto);
            }
        }

        Long total = paginationResponse.getTotalElements();
        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(dataList, pageRequest, total.longValue());
        PaginationResponse<OpenGiftDto> pageResponse = new PaginationResponse(page, OpenGiftDto.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);
        pageResponse.setTotalPages(paginationResponse.getTotalPages());

        return getSuccessResult(pageResponse);
    }

    /**
     * 新增开卡赠礼
     *
     * @param mtOpenGift
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "新增开卡赠礼")
    public MtOpenGift addOpenGift(MtOpenGift mtOpenGift) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            mtOpenGift.setUpdateTime(addTime);
            mtOpenGift.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常 " + e.getMessage());
        }

        return openGiftRepository.save(mtOpenGift);
    }

    /**
     * 根据ID获取开卡赠礼详情
     *
     * @param id 开卡赠礼ID
     * @throws BusinessCheckException
     */
    @Override
    public OpenGiftDto getOpenGiftDetail(Integer id) throws BusinessCheckException {
        MtOpenGift openGift = openGiftRepository.findOne(id);
        return this._dealDetail(openGift);
    }

    /**
     * 根据ID删除数据
     *
     * @param id       开卡赠礼ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除开卡赠礼")
    public void deleteOpenGift(Integer id, String operator) {
        MtOpenGift MtOpenGift = openGiftRepository.findOne(id);
        if (null == MtOpenGift) {
            return;
        }

        MtOpenGift.setStatus(StatusEnum.DISABLE.getKey());
        MtOpenGift.setUpdateTime(new Date());

        openGiftRepository.save(MtOpenGift);
    }

    /**
     * 更新开卡赠礼
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新开卡赠礼")
    public MtOpenGift updateOpenGift(MtOpenGift reqDto) throws BusinessCheckException {
        MtOpenGift MtOpenGift = openGiftRepository.findOne(reqDto.getId());
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

        return openGiftRepository.save(MtOpenGift);
    }

    /**
     * 开卡赠礼
     * @param userId
     * @param gradeId
     * @return
     * */
    @Override
    @Transactional
    @OperationServiceLog(description = "开卡赠礼")
    public void openGift(Integer userId, Integer gradeId) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_gradeId", gradeId.toString());
        params.put("EQ_status", StatusEnum.ENABLED.getKey());

        MtUser user = userRepository.findOne(userId);
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
            userRepository.save(user);
        }

        Specification<MtOpenGift> specification = openGiftRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<MtOpenGift> openGiftList = openGiftRepository.findAll(specification, sort);

        if (openGiftList.size() > 0) {
           Integer totalPoint = 0;
            BigDecimal totalAmount = new BigDecimal("0");
           for(MtOpenGift item : openGiftList) {
               // 叠加积分
               if (item.getPoint() > 0) {
                   MtPoint reqPointDto = new MtPoint();
                   reqPointDto.setUserId(userId);
                   reqPointDto.setAmount(item.getPoint());
                   reqPointDto.setDescription("开卡赠送"+ item.getPoint() +"积分");
                   pointService.addPoint(reqPointDto);
                   totalPoint = totalPoint + item.getPoint();
               }
               // 返卡券
               if (item.getCouponId() > 0) {
                   try {
                       Map<String, Object> param = new HashMap<>();
                       param.put("couponId", item.getCouponId());
                       param.put("userId", userId);
                       param.put("num", item.getCouponNum());
                       userCouponService.receiveCoupon(param);
                       MtCoupon mtCoupon = couponService.queryCouponById(item.getCouponId());
                       totalAmount = totalAmount.add(mtCoupon.getAmount());
                   } catch (BusinessCheckException e) {
                       // empty
                   }
               }
           }

           // 弹框消息
           MtMessage msg = new MtMessage();
           msg.setType("pop");
           msg.setUserId(userId);
           msg.setTitle("温馨提示");
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
     * @param openGiftInfo
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
