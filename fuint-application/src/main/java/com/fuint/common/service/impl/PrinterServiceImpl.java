package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.GoodsSpecValueDto;
import com.fuint.common.dto.OrderGoodsDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.HashSignUtil;
import com.fuint.common.util.NoteFormatter;
import com.fuint.common.util.PrinterUtil;
import com.fuint.common.vo.printer.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPrinter;
import com.fuint.common.service.PrinterService;
import com.fuint.repository.mapper.MtPrinterMapper;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 打印机服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class PrinterServiceImpl extends ServiceImpl<MtPrinterMapper, MtPrinter> implements PrinterService {

    private static final Logger logger = LoggerFactory.getLogger(PrinterServiceImpl.class);

    private MtPrinterMapper mtPrinterMapper;

    /**
     * 系统配置服务接口
     * */
    private SettingService settingService;

    /**
     * 环境变量
     * */
    private Environment env;

    /**
     * 分页查询数据列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtPrinter> queryPrinterListByPagination(PaginationRequest paginationRequest) {
        Page<MtPrinter> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtPrinter> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtPrinter::getStatus, StatusEnum.DISABLE.getKey());

        String status =  paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtPrinter::getStatus, status);
        }
        String merchantId =  paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtPrinter::getMerchantId, merchantId);
        }
        String storeId =  paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtPrinter::getStoreId, 0)
                    .or()
                    .eq(MtPrinter::getStoreId, storeId));
        }
        String sn =  paginationRequest.getSearchParams().get("sn") == null ? "" : paginationRequest.getSearchParams().get("sn").toString();
        if (StringUtils.isNotBlank(sn)) {
            lambdaQueryWrapper.eq(MtPrinter::getSn, sn);
        }
        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.eq(MtPrinter::getName, name);
        }
        String autoPrint = paginationRequest.getSearchParams().get("autoPrint") == null ? "" : paginationRequest.getSearchParams().get("autoPrint").toString();
        if (StringUtils.isNotBlank(autoPrint)) {
            lambdaQueryWrapper.eq(MtPrinter::getAutoPrint, autoPrint);
        }

        lambdaQueryWrapper.orderByAsc(MtPrinter::getId);
        List<MtPrinter> dataList = mtPrinterMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtPrinter> paginationResponse = new PaginationResponse(pageImpl, MtPrinter.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加打印机
     *
     * @param mtPrinter 打印机信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增打印机")
    public MtPrinter addPrinter(MtPrinter mtPrinter) throws BusinessCheckException {
        mtPrinter.setStatus(StatusEnum.ENABLED.getKey());
        mtPrinter.setUpdateTime(new Date());
        mtPrinter.setCreateTime(new Date());
        if (mtPrinter.getMerchantId() == null || mtPrinter.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        Integer printerId = mtPrinterMapper.insert(mtPrinter);
        if (printerId > 0) {
            // 添加云打印机
            if (mtPrinter.getSn() != null && mtPrinter.getName() != null) {
                AddPrinterRequest restRequest = new AddPrinterRequest();
                createRequestHeader(mtPrinter.getMerchantId(), restRequest);
                AddPrinterRequestItem item = new AddPrinterRequestItem();
                item.setName(mtPrinter.getName());
                item.setSn(mtPrinter.getSn());
                AddPrinterRequestItem[] items = { item };
                restRequest.setItems(items);
                PrinterUtil.addPrinters(restRequest);
            }
            return mtPrinter;
        } else {
            logger.error("新增打印机数据失败.");
            throw new BusinessCheckException("新增打印机数据失败");
        }
    }

    /**
     * 打印订单
     *
     * @param orderInfo 订单信息
     * @param autoPrint 自动打印
     * @return
     * */
    @Override
    public Boolean printOrder(UserOrderDto orderInfo, boolean autoPrint) throws Exception {
        PrintRequest printRequest = new PrintRequest();
        createRequestHeader(orderInfo.getMerchantId(), printRequest);
        if (orderInfo.getStoreInfo() == null) {
            throw new BusinessCheckException("打印失败：订单所属店铺信息为空！");
        }

        // 获取打印机列表
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", orderInfo.getStoreInfo().getId());
        params.put("status", StatusEnum.ENABLED.getKey());
        if (autoPrint) {
            params.put("autoPrint", YesOrNoEnum.YES.getKey());
        }
        List<MtPrinter> printers = queryPrinterListByParams(params);
        if (printers == null || printers.size() < 1) {
            throw new BusinessCheckException("打印失败：该店铺还没有添加云打印机！");
        }

        MtStore storeInfo = orderInfo.getStoreInfo();
        for (MtPrinter mtPrinter : printers) {
            printRequest.setSn(mtPrinter.getSn());

            StringBuilder printContent = new StringBuilder();
            printContent.append("<C>").append("<B>" + storeInfo.getName() + "</B>").append("<BR></C>");
            printContent.append("<BR>");

            // 分割线
            printContent.append(org.apache.commons.lang3.StringUtils.repeat("-", 32)).append("<BR>");

            // 订单号
            printContent.append("<L>订单号：").append(orderInfo.getOrderSn()).append("</L>");

            // 分割线
            printContent.append(org.apache.commons.lang3.StringUtils.repeat("-", 32)).append("<BR>");

            printContent.append("品名").append(org.apache.commons.lang3.StringUtils.repeat(" ", 16))
                        .append("数量").append(org.apache.commons.lang3.StringUtils.repeat(" ", 2))
                        .append("单价").append(org.apache.commons.lang3.StringUtils.repeat(" ", 2))
                        .append("<BR>");

            // 分割线
            printContent.append(org.apache.commons.lang3.StringUtils.repeat("-", 32)).append("<BR>");

            // 商品列表
            if (orderInfo.getGoods() != null && orderInfo.getGoods().size() > 0) {
                for (OrderGoodsDto goodsDto : orderInfo.getGoods()) {
                     List<GoodsSpecValueDto> specList = goodsDto.getSpecList();
                     String name = goodsDto.getName();
                     List<String> specValue = new ArrayList<>();
                     if (specList != null && specList.size() > 0) {
                         for (GoodsSpecValueDto spec : specList) {
                              if (StringUtil.isNotEmpty(spec.getSpecValue())) {
                                  specValue.add(spec.getSpecValue());
                              }
                         }
                         if (specValue.size() > 0) {
                             name = name + "(" + String.join(",", specValue) + ")";
                         }
                     }
                     printContent.append(NoteFormatter.formatPrintOrderItemForNewLine80(name, goodsDto.getNum(), Double.parseDouble(goodsDto.getPrice())));
                }
            }

            // 配送订单，打印配送信息
            if (orderInfo.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
                // 分割线
                printContent.append(org.apache.commons.lang3.StringUtils.repeat("-", 32)).append("<BR>");
                printContent.append("<L>")
                        .append("配送姓名：").append(orderInfo.getAddress().getName()).append("<BR>")
                        .append("联系电话：").append(orderInfo.getAddress().getMobile()).append("<BR>")
                        .append("详细地址：").append(orderInfo.getAddress().getProvinceName() + orderInfo.getAddress().getCityName() + orderInfo.getAddress().getRegionName() + orderInfo.getAddress().getDetail()).append("<BR>");
            }

            // 分割线
            printContent.append(org.apache.commons.lang3.StringUtils.repeat("-", 32)).append("<BR>");

            printContent.append("<R>").append("合计：").append(orderInfo.getPayAmount()).append("元").append("<BR></R>");

            printContent.append("<BR>");
            printContent.append("<L>")
                    .append("店铺地址：").append((orderInfo.getStoreInfo().getAddress() == null) ? "无" : orderInfo.getStoreInfo().getAddress()).append("<BR>")
                    .append("联系电话：").append((orderInfo.getStoreInfo().getPhone() == null) ? "无" : orderInfo.getStoreInfo().getPhone()).append("<BR>")
                    .append("下单时间：").append(orderInfo.getCreateTime()).append("<BR>")
                    .append("订单备注：").append(StringUtil.isEmpty(orderInfo.getRemark()) ? "无" : orderInfo.getRemark()).append("<BR>");

            // 网站二维码
            String webSite = env.getProperty("website.url");
            if (StringUtil.isNotEmpty(webSite)) {
                printContent.append("<C>").append("<QR>" + webSite + "</QR>").append("</C>");
            }

            printRequest.setContent(printContent.toString());
            printRequest.setCopies(1);
            printRequest.setVoice(2);
            printRequest.setMode(0);
            ObjectRestResponse<String> result = PrinterUtil.print(printRequest);
            if (result != null && result.getCode() != 0) {
                throw new BusinessCheckException("打印失败：" + result.getMsg());
            }
        }

        return true;
    }

    /**
     * 根据ID获打印机取息
     *
     * @param id 打印机ID
     * @return
     */
    @Override
    public MtPrinter queryPrinterById(Integer id) {
        return mtPrinterMapper.selectById(id);
    }

    /**
     * 根据ID删除打印机
     *
     * @param id 打印机ID
     * @param operator 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除打印机")
    public void deletePrinter(Integer id, String operator) throws BusinessCheckException {
        MtPrinter mtPrinter = queryPrinterById(id);
        if (null == mtPrinter) {
            return;
        }
        // 删除云打印机
        if (StringUtil.isNotEmpty(mtPrinter.getSn())) {
            DelPrinterRequest restRequest = new DelPrinterRequest();
            createRequestHeader(mtPrinter.getMerchantId(), restRequest);
            String[] snList = { mtPrinter.getSn() };
            restRequest.setSnlist(snList);
            PrinterUtil.delPrinters(restRequest);
        }
        mtPrinter.setStatus(StatusEnum.DISABLE.getKey());
        mtPrinter.setUpdateTime(new Date());
        mtPrinterMapper.updateById(mtPrinter);
    }

    /**
     * 修改打印机数据
     *
     * @param  mtPrinter 打印机参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新打印机")
    public MtPrinter updatePrinter(MtPrinter mtPrinter) throws BusinessCheckException {
        MtPrinter printer = queryPrinterById(mtPrinter.getId());
        BeanUtils.copyProperties(mtPrinter, printer);
        if (mtPrinter == null) {
            throw new BusinessCheckException("该打印机状态异常");
        }
        if (printer.getMerchantId() == null || printer.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        if (mtPrinter.getSn() != null && mtPrinter.getName() != null && !mtPrinter.getStatus().equals(StatusEnum.DISABLE.getKey())) {
            UpdPrinterRequest restRequest = new UpdPrinterRequest();
            createRequestHeader(mtPrinter.getMerchantId(), restRequest);
            restRequest.setName(mtPrinter.getName());
            restRequest.setSn(mtPrinter.getSn());
            PrinterUtil.updPrinter(restRequest);
        }
        if (mtPrinter.getStatus().equals(StatusEnum.DISABLE.getKey())) {
            deletePrinter(mtPrinter.getId(), mtPrinter.getOperator());
        }

        mtPrinter.setUpdateTime(new Date());
        mtPrinterMapper.updateById(printer);
        return printer;
    }

   /**
    * 根据条件搜索打印机
    *
    * @param params 查询参数
    * @throws BusinessCheckException
    * @return
    * */
    @Override
    public List<MtPrinter> queryPrinterListByParams(Map<String, Object> params) {
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId = params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId = params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        String sn = params.get("sn") == null ? "" : params.get("sn").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();
        String autoPrint = params.get("autoPrint") == null ? "" : params.get("autoPrint").toString();

        LambdaQueryWrapper<MtPrinter> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtPrinter::getStatus, StatusEnum.DISABLE.getKey());

        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtPrinter::getStatus, status);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtPrinter::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(sn)) {
            lambdaQueryWrapper.eq(MtPrinter::getSn, sn);
        }
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.eq(MtPrinter::getName, name);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtPrinter::getStoreId, 0)
                    .or()
                    .eq(MtPrinter::getStoreId, storeId));
        }
        if (StringUtils.isNotBlank(autoPrint)) {
            lambdaQueryWrapper.eq(MtPrinter::getAutoPrint, autoPrint);
        }
        lambdaQueryWrapper.orderByAsc(MtPrinter::getId);

        return mtPrinterMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 创建接口请求header
     *
     * @param merchantId 商户ID
     * @param request RestRequest
     * @return
     * */
    public void createRequestHeader(Integer merchantId, RestRequest request) throws BusinessCheckException {
        List<MtSetting> settings = settingService.getSettingList(merchantId, SettingTypeEnum.PRINTER.getKey());
        if (settings != null && settings.size() > 0) {
            String userName = "";
            String userKey = "";
            for (MtSetting mtSetting : settings) {
                if (mtSetting.getName().equals(PrinterSettingEnum.USER_NAME.getKey())) {
                    userName = mtSetting.getValue();
                }
                if (mtSetting.getName().equals(PrinterSettingEnum.USER_KEY.getKey())) {
                    userKey = mtSetting.getValue();
                }
            }
            if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(userKey)) {
                request.setUser(userName);
                request.setTimestamp(System.currentTimeMillis() + "");
                request.setSign(HashSignUtil.sign(request.getUser() + userKey + request.getTimestamp()));
                request.setDebug("0");
            } else {
                throw new BusinessCheckException("请先设置芯烨云打印账号！");
            }
        } else {
            throw new BusinessCheckException("请先设置芯烨云打印账号！");
        }
    }
}
