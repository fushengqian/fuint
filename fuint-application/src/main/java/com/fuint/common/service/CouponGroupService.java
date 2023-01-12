package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.GroupDataDto;
import com.fuint.common.dto.ReqCouponGroupDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtCouponGroup;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
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
    PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加卡券分组
     *
     * @param reqCouponGroupDto
     * @throws BusinessCheckException
     */
    MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException;

    /**
     * 修改卡券分组
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
    MtCouponGroup queryCouponGroupById(Integer id) throws BusinessCheckException;

    /**
     * 根据分组ID 删除分组信息
     *
     * @param id       分组ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCouponGroup(Integer id, String operator) throws BusinessCheckException;

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
     * @param id 分组ID
     * @throws BusinessCheckException
     */
    BigDecimal getCouponMoney(Integer id) throws BusinessCheckException;

    /**
     * 获取已发放套数
     *
     * @param  id  分组ID
     * @throws BusinessCheckException
     * */
    Integer getSendNum(Integer id) throws BusinessCheckException;

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
