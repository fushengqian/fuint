package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.PrinterSettingEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.HashSignUtil;
import com.fuint.common.util.PrinterUtil;
import com.fuint.common.vo.printer.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPrinter;
import com.fuint.common.service.PrinterService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtPrinterMapper;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
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
        String name =  paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.eq(MtPrinter::getName, name);
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
                createRequestHeader(0, restRequest);
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
     * 执行打印
     *
     * @param
     * */
    @Override
    public void doPrint() throws BusinessCheckException {
        PrintRequest printRequest = new PrintRequest();
        createRequestHeader(0, printRequest);
        printRequest.setSn("74BLW3L8C2FE448");

        StringBuilder printContent = new StringBuilder();
        printContent.append("不加标签：").append("默认字体大小<BR>");
        printContent.append("<BR>");
        printContent.append("L标签：").append("<L>左对齐<BR></L>");
        printContent.append("<BR>");
        printContent.append("R标签：").append("<R>右对齐<BR></R>");
        printContent.append("<BR>");
        printContent.append("C标签：").append("<C>居中对齐<BR></C>");

        printContent.append("<BR>");
        printContent.append("N标签：").append("<N>字体正常大小<BR></N>");
        printContent.append("<BR>");
        printContent.append("HB标签：").append("<HB>字体变高一倍<BR></HB>");
        printContent.append("<BR>");
        printContent.append("WB标签：").append("<WB>字体变宽一倍<BR></WB>");
        printContent.append("<BR>");
        printContent.append("B标签：").append("<B>字体放大一倍<BR></B>");
        printContent.append("<BR>");
        printContent.append("HB2标签：").append("<HB2>字体变高二倍<BR></HB2>");
        printContent.append("<BR>");
        printContent.append("WB2标签：").append("<WB2>字体变宽二倍<BR></WB2>");
        printContent.append("<BR>");
        printContent.append("B2标签：").append("<B2>字体放大二倍<BR></B2>");
        printContent.append("<BR>");
        printContent.append("BOLD标签：").append("<BOLD>字体加粗<BR></BOLD>");

        // 嵌套使用对齐和字体
        printContent.append("<BR>");
        printContent.append("<C>嵌套使用：").append("<BOLD>居中加粗</BOLD>").append("<BR></C>");

        // 打印条形码和二维码
        printContent.append("<BR>");
        printContent.append("<C><BARCODE>9884822189</BARCODE></C>");
        printContent.append("<C><QR>https://www.xpyun.net</QR></C>");

        printRequest.setContent(printContent.toString());
        printRequest.setCopies(1);
        printRequest.setVoice(2);
        printRequest.setMode(0);
        ObjectRestResponse<String> resp = PrinterUtil.print(printRequest);
        String a = "abc";
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
            createRequestHeader(0, restRequest);
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
     * @param mtPrinter 打印机参数
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
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        String sn =  params.get("sn") == null ? "" : params.get("sn").toString();
        String name =  params.get("name") == null ? "" : params.get("name").toString();

        LambdaQueryWrapper<MtPrinter> lambdaQueryWrapper = Wrappers.lambdaQuery();
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

        lambdaQueryWrapper.orderByAsc(MtPrinter::getId);
        List<MtPrinter> dataList = mtPrinterMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }

    /**
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
                //*必填*：芯烨云平台注册用户名（开发者ID）
                request.setUser(userName);
                //*必填*：当前UNIX时间戳
                request.setTimestamp(System.currentTimeMillis() + "");
                //*必填*：对参数 user + UserKEY + timestamp 拼接后（+号表示连接符）进行SHA1加密得到签名，值为40位小写字符串，其中 UserKEY 为用户开发者密钥
                request.setSign(HashSignUtil.sign(request.getUser() + userKey + request.getTimestamp()));
                //debug=1返回非json格式的数据，仅测试时候使用
                request.setDebug("0");
            }
        }
    }
}
