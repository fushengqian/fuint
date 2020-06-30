package com.fuint.coupon.service.coupongroup;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtCouponGroup;
import com.fuint.coupon.dto.GroupDataDto;
import com.fuint.coupon.dto.ReqCouponGroupDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import java.math.BigDecimal;

/**
 * 优惠分组业务接口
 * Created by zach on 2019/8/22.
 */
public interface CouponGroupService {

    /**
     * 分页查询分组列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加优惠分组
     *
     * @param reqCouponGroupDto
     * @throws BusinessCheckException
     */
    MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException;

    /**
     * 修改优惠分组
     *
     * @param reqCouponGroupDto
     * @throws BusinessCheckException
     */
    MtCouponGroup updateCouponGroup(ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param id 分组ID
     * @throws BusinessCheckException
     */
    MtCouponGroup queryCouponGroupById(Long id) throws BusinessCheckException;

    /**
     * 根据分组ID 删除分组信息
     *
     * @param id       分组ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCouponGroup(Long id, String operator) throws BusinessCheckException;

    /**
     * 根据分组ID 获取券种类数量
     *
     * @param id       分组ID
     * @throws BusinessCheckException
     */
    Integer getCouponNum(Integer id) throws BusinessCheckException;

    /**
     * 根据分组ID 获取券总价值
     *
     * @param id       分组ID
     * @throws BusinessCheckException
     */
    BigDecimal getCouponMoney(Long id) throws BusinessCheckException;

    /**
     * 获取已发放套数
     *
     * @param  id  分组ID
     * @throws BusinessCheckException
     * */
    Integer getSendedNum(Integer id) throws BusinessCheckException;

    /**
     * 导入发券列表
     *
     * @param file excel文件
     * @param operator 操作者
     * */
    String importSendCoupon(MultipartFile file, String operator, String filePath) throws BusinessCheckException;

    /**
     * 保存文件
     *
     * @param file excel文件
     * @param request 操作者
     * */
     String saveExcelFile(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * 获取分组统计数据
     *
     * @param groupId 分组ID
     * */
    GroupDataDto getGroupData(Integer groupId, HttpServletRequest request, Model model) throws BusinessCheckException;
}
