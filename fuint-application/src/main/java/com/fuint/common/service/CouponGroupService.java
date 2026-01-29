package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ReqCouponGroupDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtCouponGroup;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

/**
 * 卡券分组业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CouponGroupService extends IService<MtCouponGroup> {

    /**
     * 分页查询分组列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(PaginationRequest paginationRequest);

    /**
     * 添加卡券分组
     *
     * @param reqCouponGroupDto
     * @return
     */
    MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto);

    /**
     * 修改卡券分组
     *
     * @param reqCouponGroupDto
     * @throws BusinessCheckException
     * @return
     */
    MtCouponGroup updateCouponGroup(ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param id 分组ID
     * @return
     */
    MtCouponGroup queryCouponGroupById(Integer id);

    /**
     * 根据分组ID 删除分组信息
     *
     * @param id       分组ID
     * @param operator 操作人
     * @return
     */
    void deleteCouponGroup(Integer id, String operator);

    /**
     * 根据分组ID 获取券种类数量
     *
     * @param id       分组ID
     * @return
     */
    Integer getCouponNum(Integer id);

    /**
     * 根据分组ID 获取券总价值
     *
     * @param id 分组ID
     * @return
     */
    BigDecimal getCouponMoney(Integer id);

    /**
     * 获取已发放套数
     *
     * @param  id  分组ID
     * @return
     * */
    Integer getSendNum(Integer id);

    /**
     * 导入发券列表
     *
     * @param file excel文件
     * @param operator 操作者
     * */
    String importSendCoupon(MultipartFile file, String operator, String filePath) throws BusinessCheckException;

}
