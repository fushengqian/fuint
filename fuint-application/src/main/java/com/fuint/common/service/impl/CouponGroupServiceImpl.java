package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.CouponCellDto;
import com.fuint.common.dto.GroupDataDto;
import com.fuint.common.dto.ReqCouponGroupDto;
import com.fuint.common.dto.ReqSendLogDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.XlsUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtCouponGroupMapper;
import com.fuint.repository.mapper.MtCouponMapper;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.lang.String;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 卡券分组业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CouponGroupServiceImpl extends ServiceImpl<MtCouponGroupMapper, MtCouponGroup> implements CouponGroupService {

    private static final Logger log = LoggerFactory.getLogger(CouponGroupServiceImpl.class);

    @Resource
    private MtCouponGroupMapper mtCouponGroupMapper;

    @Resource
    private MtCouponMapper mtCouponMapper;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Resource
    private CouponService couponService;

    @Resource
    private UserCouponService userCouponService;

    @Resource
    private MemberService memberService;

    @Resource
    private SendLogService sendLogService;

    @Resource
    private SendSmsService sendSmsService;

    @Autowired
    private Environment env;

    /**
     * 分页查询卡券分组列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(PaginationRequest paginationRequest) {
        Page<MtCouponGroup> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtCouponGroup> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtCouponGroup::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtCouponGroup::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtCouponGroup::getStatus, status);
        }
        String id = paginationRequest.getSearchParams().get("id") == null ? "" : paginationRequest.getSearchParams().get("id").toString();
        if (StringUtils.isNotBlank(id)) {
            lambdaQueryWrapper.eq(MtCouponGroup::getId, id);
        }

        lambdaQueryWrapper.orderByDesc(MtCouponGroup::getId);
        List<MtCouponGroup> dataList = mtCouponGroupMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtCouponGroup> paginationResponse = new PaginationResponse(pageImpl, MtCouponGroup.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加卡券分组
     *
     * @param  reqCouponGroupDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "新增卡券分组")
    public MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto) {
        MtCouponGroup couponGroup = new MtCouponGroup();

        couponGroup.setName(CommonUtil.replaceXSS(reqCouponGroupDto.getName()));
        couponGroup.setMoney(new BigDecimal("0"));
        couponGroup.setTotal(0);
        couponGroup.setDescription(CommonUtil.replaceXSS(reqCouponGroupDto.getDescription()));
        couponGroup.setStatus(StatusEnum.ENABLED.getKey());
        couponGroup.setCreateTime(new Date());
        couponGroup.setUpdateTime(new Date());
        couponGroup.setNum(0);
        couponGroup.setOperator(reqCouponGroupDto.getOperator());

        this.save(couponGroup);
        return couponGroup;
    }

    /**
     * 根据分组ID获取卡券分组信息
     *
     * @param id 卡券分组ID
     * @throws BusinessCheckException
     */
    @Override
    public MtCouponGroup queryCouponGroupById(Integer id) {
        return mtCouponGroupMapper.selectById(id);
    }

    /**
     * 根据ID删除卡券分组
     *
     * @param  id       分组ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除卡券分组")
    public void deleteCouponGroup(Integer id, String operator) {
        MtCouponGroup couponGroup = this.queryCouponGroupById(id);
        if (null == couponGroup) {
            return;
        }

        couponGroup.setStatus(StatusEnum.DISABLE.getKey());
        couponGroup.setUpdateTime(new Date());
        couponGroup.setOperator(operator);

        this.updateById(couponGroup);
    }

    /**
     * 修改卡券分组
     *
     * @param reqcouponGroupDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新卡券分组")
    public MtCouponGroup updateCouponGroup(ReqCouponGroupDto reqcouponGroupDto) throws BusinessCheckException {
        MtCouponGroup couponGroup = this.queryCouponGroupById(reqcouponGroupDto.getId());
        if (null == couponGroup || StatusEnum.DISABLE.getKey().equalsIgnoreCase(couponGroup.getStatus())) {
            log.error("该分组不存在或已被删除");
            throw new BusinessCheckException("该分组不存在或已被删除");
        }
        if (reqcouponGroupDto.getName() != null) {
            couponGroup.setName(CommonUtil.replaceXSS(reqcouponGroupDto.getName()));
        }
        if (reqcouponGroupDto.getDescription() != null) {
            couponGroup.setDescription(CommonUtil.replaceXSS(reqcouponGroupDto.getDescription()));
        }
        if (couponGroup.getTotal() == null) {
            couponGroup.setTotal(0);
        }
        if (reqcouponGroupDto.getStatus() != null) {
            couponGroup.setStatus(reqcouponGroupDto.getStatus());
        }

        couponGroup.setUpdateTime(new Date());
        couponGroup.setOperator(reqcouponGroupDto.getOperator());

        this.updateById(couponGroup);

        return couponGroup;
    }

    /**
     * 获取卡券种类数量
     *
     * @param id
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer getCouponNum(Integer id) {
        Long num = mtCouponMapper.queryNumByGroupId(id);
        return num.intValue();
    }

    /**
     * 获取卡券总价值
     *
     * @param groupId
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal getCouponMoney(Integer groupId) {
        List<MtCoupon> couponList = mtCouponMapper.queryByGroupId(groupId.intValue());
        MtCouponGroup groupInfo = this.queryCouponGroupById(groupId);
        BigDecimal money = BigDecimal.valueOf(0);
        if (couponList.size() > 0) {
            for (int i = 0; i<couponList.size(); i++) {
                 BigDecimal number = couponList.get(i).getAmount().multiply(BigDecimal.valueOf(couponList.get(i).getSendNum()));
                 number = number.multiply(BigDecimal.valueOf(groupInfo.getTotal()));
                 money = money.add(number);
            }
        }
        return money;
    }

    /**
     * 获取已发放套数
     *
     * @param  couponId  卡券ID
     * @throws BusinessCheckException
     * */
    @Override
    public Integer getSendNum(Integer couponId) {
        Long num = mtUserCouponMapper.getSendNum(couponId);
        return num.intValue();
    }

    /**
     * 导入发券列表
     *
     * @param file excel文件
     * @param operator 操作者
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "导入发券列表")
    public String importSendCoupon(MultipartFile file, String operator, String filePath) throws BusinessCheckException {
        String originalFileName = file.getOriginalFilename();
        boolean isExcel2003 = XlsUtil.isExcel2003(originalFileName);
        boolean isExcel2007 = XlsUtil.isExcel2007(originalFileName);

        if (!isExcel2003 && !isExcel2007) {
            log.error("importSendCouponController->uploadFile：{}", "文件类型不正确");
            throw new BusinessCheckException("文件类型不正确");
        }

        List<List<String>> content = new ArrayList<>();
        try {
            content = XlsUtil.readExcelContent(file.getInputStream(), isExcel2003, 1, null, null, null);
        } catch (IOException e) {
            log.error("CouponGroupServiceImpl->parseExcelContent{}", e);
            throw new BusinessCheckException("导入失败"+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuffer errorMsg = new StringBuffer();
        StringBuffer errorMsgNoGroup = new StringBuffer();
        StringBuffer errorMsgNoNum = new StringBuffer();
        StringBuffer errorMsgNoRegister = new StringBuffer();

        List<CouponCellDto> rows = new ArrayList<>();

        for (int i = 0; i < content.size(); i++) {
            List<Integer> groupIdArr = new ArrayList<>();
            List<Integer> numArr = new ArrayList<>();

            List<String> rowContent = content.get(i);
            String mobile = rowContent.get(0);

            if (StringUtil.isBlank(mobile) || mobile.length() < 11 || mobile.length() > 11) {
                errorMsg.append("第" + i + "行错误,手机号有误:"+mobile);
                continue;
            }

            for (int j = 1; j < rowContent.size(); j++) {
                Integer item = 0;
                String cellContent = rowContent.get(j);
                if (null == cellContent || cellContent.equals("")) {
                    continue;
                }

                if (j%2 != 0) {
                    Pattern pattern = Pattern.compile("^[1-9]\\d*$");
                    if (item == null || (!pattern.matcher(cellContent).matches())) {
                        throw new BusinessCheckException("第" + (i+1) + "行第"+ j +"列错误, 卡券ID异常");
                    }

                    item = Integer.parseInt(cellContent);
                    if (item < 0) {
                        errorMsg.append("第" + (i+1) + "行第"+ j +"列错误, 卡券ID异常");
                        continue;
                    }
                    groupIdArr.add(item);
                } else {
                    Pattern pattern = Pattern.compile("^[1-9]\\d*$");
                    if (item == null || (!pattern.matcher(rowContent.get(j)).matches())) {
                        throw new BusinessCheckException("第" + (i+1) + "行第"+ j +"列错误, 数量异常");
                    }

                    item = Integer.parseInt(rowContent.get(j));
                    if (item < 0) {
                        errorMsg.append("第" + (i+1) + "行第"+ j +"列错误, 数量异常");
                        continue;
                    }
                    numArr.add(item);
                }
            }

            if (groupIdArr.size() != numArr.size()) {
                throw new BusinessCheckException("表格数据有问题导致无法导入");
            }

            CouponCellDto item = new CouponCellDto();
            item.setMobile(mobile);
            item.setGroupId(groupIdArr);
            item.setNum(numArr);

            rows.add(item);
        }

        if (rows.size() < 1) {
            throw new BusinessCheckException("表格数据为空导致无法导入");
        }

        if (rows.size() > 1000) {
            throw new BusinessCheckException("每次导入最多不能超过1000人");
        }

        // 获取每个分组的总数
        Map<String, Integer> couponIdMap = new HashMap<>();
        for (CouponCellDto dto : rows) {
            MtUser userInfo = memberService.queryMemberByMobile(dto.getMobile());
            if (userInfo == null) {
                userInfo = memberService.addMemberByMobile(dto.getMobile());
            }

            if (null == userInfo || !userInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
                    errorMsgNoGroup.append("，" + dto.getMobile());
                } else {
                    errorMsgNoGroup.append("手机号没有注册或已禁用："+dto.getMobile());
                }
            }

            for (int k = 0; k < dto.getGroupId().size(); k++) {
                Integer num = dto.getNum().get(k);
                Integer total = couponIdMap.get(dto.getGroupId().get(k).toString()) == null ? 0 : couponIdMap.get(dto.getGroupId().get(k).toString());
                couponIdMap.put(dto.getGroupId().get(k).toString(), (total+num));
            }
        }

        if (StringUtil.isNotBlank(errorMsgNoRegister.toString())) {
            throw new BusinessCheckException(errorMsgNoRegister.toString());
        }

        for (String couponId : couponIdMap.keySet()) {
             MtCoupon couponInfo = couponService.queryCouponById(Integer.parseInt(couponId));
             if (null == couponInfo) {
                 if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
                     errorMsgNoGroup.append("," + couponId);
                 } else {
                     errorMsgNoGroup.append("卡券ID不存在："+couponId);
                 }
                 continue;
             }

             if (!couponInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                 throw new BusinessCheckException("卡券ID"+couponId+"可能已删除或禁用");
             }

             Integer totalNum = couponInfo.getTotal() == null ? 0 : couponInfo.getTotal();
             Integer sendNum = couponIdMap.get(couponId);
             Integer hasSendNum = this.getSendNum(Integer.parseInt(couponId));
             if (totalNum > 0 && ((totalNum - hasSendNum) < sendNum)) {
                 Integer needNum = sendNum - (totalNum - hasSendNum);
                 if (StringUtil.isNotBlank(errorMsgNoNum.toString())) {
                     errorMsgNoNum.append(";卡券ID:" + couponId + "存量不足,至少再添加" + needNum + "套");
                 } else {
                     errorMsgNoNum.append("卡券ID:" + couponId + "存量不足,至少再添加" + needNum + "套");
                 }
             }
        }

        if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
            throw new BusinessCheckException(errorMsgNoGroup.toString());
        }

        if (StringUtil.isNotBlank(errorMsgNoNum.toString())) {
            throw new BusinessCheckException(errorMsgNoNum.toString());
        }

        if (StringUtil.isNotBlank(errorMsg.toString())) {
            throw new BusinessCheckException(errorMsg.toString());
        }

        // 导入批次
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // 至此，验证都通过了，开始发券
        try {
            for (CouponCellDto cellDto : rows) {
                // 发送张数
                Integer totalNum = 0;
                // 发送总价值
                BigDecimal totalMoney = new BigDecimal("0.0");
                for (int gid = 0; gid < cellDto.getGroupId().size(); gid++) {
                    couponService.sendCoupon(cellDto.getGroupId().get(gid).intValue(), cellDto.getMobile(), cellDto.getNum().get(gid), uuid, operator);
                    List<MtCoupon> couponList = couponService.queryCouponListByGroupId(cellDto.getGroupId().get(gid).intValue());
                    // 累加总张数、总价值
                    for (MtCoupon coupon : couponList) {
                         totalNum = totalNum + (coupon.getSendNum()*cellDto.getNum().get(gid));
                         totalMoney = totalMoney.add((coupon.getAmount().multiply(new BigDecimal(cellDto.getNum().get(gid)).multiply(new BigDecimal(coupon.getSendNum())))));
                    }
                }

                MtUser mtUser = memberService.queryMemberByMobile(cellDto.getMobile());

                // 发放记录
                ReqSendLogDto dto = new ReqSendLogDto();
                dto.setType(2);
                dto.setMobile(cellDto.getMobile());
                dto.setUserId(mtUser.getId());
                dto.setFileName(originalFileName);
                dto.setFilePath(filePath);
                dto.setGroupId(0);
                dto.setCouponId(0);
                dto.setGroupName("");
                dto.setSendNum(0);
                dto.setOperator(operator);
                dto.setUuid(uuid);
                sendLogService.addSendLog(dto);

                // 发送短信
                try {
                    List<String> mobileList = new ArrayList<>();
                    mobileList.add(cellDto.getMobile());
                    Map<String, String> params = new HashMap<>();
                    params.put("totalNum", totalNum+"");
                    params.put("totalMoney", totalMoney+"");
                    sendSmsService.sendSms("received-coupon", mobileList, params);
                } catch (Exception e) {
                    //empty
                }
            }
        } catch (BusinessCheckException e) {
            throw new BusinessCheckException(e.getMessage());
        }
        return uuid;
    }

    /**
     * 保存文件
     *
     * @param file excel文件
     * @param request
     * */
    public String saveExcelFile(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();

        String imageName = fileName.substring(fileName.lastIndexOf("."));
        String pathRoot = env.getProperty("images.root");
        if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
            pathRoot = ResourceUtils.getURL("classpath:").getPath();
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        String filePath = "/static/uploadFiles/"+ DateUtil.formatDate(new Date(), "yyyyMMdd")+"/";
        String path = filePath + uuid + imageName;

        try {
            File tempFile = new File(pathRoot + path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            CommonUtil.saveMultipartFile(file, pathRoot + path);
        } catch (Exception e) {
            //empty
        }

        return path;
    }

    /**
     * 获取分组统计数据
     *
     * @param groupId 分组ID
     * */
    public GroupDataDto getGroupData(Integer groupId, HttpServletRequest request, Model model) throws BusinessCheckException {
        MtCouponGroup groupInfo = this.queryCouponGroupById(groupId);

        // 已发放套数
        Integer sendNum = 0;

        // 未发放套数
        Integer unSendNum = groupInfo.getTotal() - sendNum;

        // 已使用张数
        PaginationRequest requestUserCouponUse = new PaginationRequest();
        requestUserCouponUse.getSearchParams().put("groupId", groupId.toString());
        requestUserCouponUse.getSearchParams().put("status", UserCouponStatusEnum.USED.getKey());
        PaginationResponse<MtUserCoupon> dataUserCoupon = userCouponService.queryUserCouponListByPagination(requestUserCouponUse);
        Long useNum = dataUserCoupon.getTotalElements();

        // 已过期张数
        Date nowDate = new Date();
        Integer expireNum = 0;
        List<MtCoupon> couponList = couponService.queryCouponListByGroupId(groupId);
        List<MtUserCoupon> userCouponList = mtUserCouponMapper.queryExpireNumByGroupId(groupId);
        if (null != userCouponList) {
            for (MtUserCoupon userCoupon: userCouponList) {
                MtCoupon couponInfo = null;
                for (MtCoupon coupon: couponList) {
                    if (userCoupon.getCouponId().toString().equals(coupon.getId().toString())) {
                        couponInfo = coupon;
                        break;
                    }
                }
                if (null == couponInfo) {
                    continue;
                }
                if (nowDate.after(couponInfo.getEndTime())) {
                    expireNum++;
                }
            }
        }

        // 已作废张数
        PaginationRequest requestUserCouponCancel = new PaginationRequest();
        requestUserCouponCancel.getSearchParams().put("groupId", groupId.toString());
        requestUserCouponCancel.getSearchParams().put("status", UserCouponStatusEnum.DISABLE.getKey());
        PaginationResponse<MtUserCoupon> dataUserCouponCancel = userCouponService.queryUserCouponListByPagination(requestUserCouponCancel);
        Long cancelNum = dataUserCouponCancel.getTotalElements();

        GroupDataDto data = new GroupDataDto();
        data.setSendNum(sendNum);
        data.setUnSendNum(unSendNum);
        data.setUseNum(useNum.intValue());
        data.setExpireNum(expireNum.intValue());
        data.setCancelNum(cancelNum.intValue());

        return data;
    }
}
