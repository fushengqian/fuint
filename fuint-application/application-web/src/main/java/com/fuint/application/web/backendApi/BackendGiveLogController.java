package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.util.XlsUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtCouponGroupRepository;
import com.fuint.application.dao.repositories.MtCouponRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import static com.fuint.application.util.XlsUtil.objectConvertToString;
import com.fuint.application.dto.GiveDto;
import com.fuint.application.dto.GiveItemDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.give.GiveService;
import com.fuint.application.web.backend.util.ExcelUtil;
import com.fuint.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 转赠管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/give")
public class BackendGiveLogController extends BaseController {

    /**
     * 转赠服务接口
     */
    @Autowired
    private GiveService giveService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private MtCouponRepository couponRepository;

    /**
     * 查询列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        Map<String, Object> params = new HashMap<>();
        params.put("NQ_status", StatusEnum.DISABLE.getKey());

        if (storeId > 0) {
            params.put("EQ_storeId", storeId.toString());
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 快速查询详情
     * */
    @RequestMapping(value = "/giveItem", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject giveItem(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String giveId = request.getParameter("giveId");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        if (StringUtil.isEmpty(giveId)) {
            return getFailureResult(201, "参数有误");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(5000);

        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        params.put("EQ_giveId", giveId);

        List<MtGiveItem> itemList = giveService.queryItemByParams(params);

        List<GiveItemDto> dataList = new ArrayList<>();
        for (MtGiveItem item : itemList) {
            MtGive giveInfo = giveService.queryGiveById(Long.parseLong(giveId));
            MtUserCoupon userCouponInfo = userCouponRepository.findOne(item.getUserCouponId());
            if (userCouponInfo != null) {
                MtCouponGroup groupInfo = couponGroupRepository.findOne(userCouponInfo.getGroupId());
                MtCoupon couponInfo = couponRepository.findOne(userCouponInfo.getCouponId());
                if (groupInfo != null && couponInfo != null) {
                    GiveItemDto dto = new GiveItemDto();
                    dto.setId(item.getId());
                    dto.setMobile(giveInfo.getUserMobile());
                    dto.setUserMobile(giveInfo.getMobile());
                    dto.setGroupId(userCouponInfo.getGroupId());
                    dto.setGroupName(groupInfo.getName());
                    dto.setCouponId(userCouponInfo.getCouponId());
                    dto.setCouponName(couponInfo.getName());
                    dto.setMoney(userCouponInfo.getAmount());
                    dto.setCreateTime(item.getCreateTime());
                    dataList.add(dto);
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("itemList", dataList);

        return getSuccessResult(resultMap);
    }

    /**
     * 导出数据
     *
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setPageSize(50000);
        paginationRequest.setCurrentPage(1);

        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);
        List<GiveDto> list = paginationResponse.getContent();

        // excel标题
        String[] title = {"记录ID", "用户手机号", "转赠数量", "转赠总金额", "赠予对象手机号", "赠予时间"};
        String fileName;
        fileName = "转赠记录" + System.currentTimeMillis() + ".xls";

        String[][] content = null;
        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            GiveDto obj = list.get(i);
            content[i][0] = objectConvertToString(obj.getId());
            content[i][1] = objectConvertToString(obj.getUserMobile());
            content[i][2] = objectConvertToString(obj.getNum());
            content[i][3] = objectConvertToString(obj.getMoney());
            content[i][4] = objectConvertToString(obj.getMobile());
            content[i][5] = objectConvertToString(obj.getCreateTime());
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("转赠记录", title, content, null);

        // 响应到客户端
        try {
            XlsUtil.setXlsHeader(request, response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
