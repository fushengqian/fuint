package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.param.PrinterPage;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPrinter;
import com.fuint.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 打印机业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface PrinterService extends IService<MtPrinter> {

    /**
     * 分页查询列表
     *
     * @param printerPage
     * @return
     */
    PaginationResponse<MtPrinter> queryPrinterListByPagination(PrinterPage printerPage) throws BusinessCheckException;

    /**
     * 添加打印机
     *
     * @param  mtPrinter
     * @throws BusinessCheckException
     * @return
     */
    MtPrinter addPrinter(MtPrinter mtPrinter) throws BusinessCheckException;

    /**
     * 打印订单
     *
     * @param orderInfo 订单信息
     * @param autoPrint 自动打印
     * @return
     * */
    Boolean printOrder(UserOrderDto orderInfo, boolean autoPrint) throws Exception;

    /**
     * 根据ID获取打印机信息
     *
     * @param id ID
     * @throws BusinessCheckException
     * @return
     */
    MtPrinter queryPrinterById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除打印机
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deletePrinter(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新打印机
     * @param  mtPrinter
     * @throws BusinessCheckException
     * @return
     * */
    MtPrinter updatePrinter(MtPrinter mtPrinter) throws BusinessCheckException;

    /**
     * 根据条件搜索打印机
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtPrinter> queryPrinterListByParams(Map<String, Object> params) throws BusinessCheckException;
}
