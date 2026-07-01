package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.CateService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户端-商品分类管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-商品分类管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/cate")
public class MerchantCateController extends BaseController {

    /**
     * 商品分类服务接口
     */
    private CateService cateService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     */
    private StaffService staffService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 获取商品分类列表
     */
    @ApiOperation(value = "获取商品分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list() throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        List<MtGoodsCate> cateList = cateService.getCateList(
                staffInfo.getMerchantId(),
                staffInfo.getStoreId(),
                null,
                StatusEnum.ENABLED.getKey()
        );

        // 也获取已禁用的分类
        List<MtGoodsCate> allCateList = cateService.getCateList(
                staffInfo.getMerchantId(),
                staffInfo.getStoreId(),
                null,
                null
        );

        Map<String, Object> result = new HashMap<>();
        result.put("cateList", cateList);
        result.put("allCateList", allCateList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 保存商品分类（新增/编辑）
     */
    @ApiOperation(value = "保存商品分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String id = param.get("id") != null ? param.get("id").toString() : "";
        String name = param.get("name") != null ? param.get("name").toString() : "";
        String logo = param.get("logo") != null ? param.get("logo").toString() : "";
        String description = param.get("description") != null ? param.get("description").toString() : "";
        String sort = param.get("sort") != null ? param.get("sort").toString() : "0";

        // 去掉图片中的域名前缀，数据库只存储相对路径
        String basePath = settingService.getUploadBasePath();
        if (StringUtil.isNotEmpty(logo) && basePath != null && logo.startsWith(basePath)) {
            logo = logo.substring(basePath.length());
        }

        if (name == null || name.trim().isEmpty()) {
            return getFailureResult(201, "分类名称不能为空");
        }

        MtGoodsCate mtGoodsCate = new MtGoodsCate();
        mtGoodsCate.setName(name);
        mtGoodsCate.setLogo(logo);
        mtGoodsCate.setDescription(description);
        mtGoodsCate.setSort(Integer.parseInt(sort));
        mtGoodsCate.setMerchantId(staffInfo.getMerchantId());
        mtGoodsCate.setOperator(staffInfo.getRealName());

        AccountInfo accountInfo = buildAccountInfo(staffInfo);

        if (id != null && !id.isEmpty() && Integer.parseInt(id) > 0) {
            mtGoodsCate.setId(Integer.parseInt(id));
            cateService.updateCate(mtGoodsCate, accountInfo);
        } else {
            mtGoodsCate.setStoreId(staffInfo.getStoreId());
            mtGoodsCate.setStatus(StatusEnum.ENABLED.getKey());
            cateService.addCate(mtGoodsCate);
        }

        return getSuccessResult(true);
    }

    /**
     * 更新商品分类状态
     */
    @ApiOperation(value = "更新商品分类状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        MtGoodsCate mtCate = cateService.queryCateById(params.getId());
        if (mtCate == null) {
            return getFailureResult(201, "该类别不存在");
        }

        if (!staffInfo.getMerchantId().equals(mtCate.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        AccountInfo accountInfo = buildAccountInfo(staffInfo);

        MtGoodsCate cate = new MtGoodsCate();
        cate.setOperator(staffInfo.getRealName());
        cate.setId(params.getId());
        cate.setStatus(params.getStatus());
        cateService.updateCate(cate, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 获取分类详情
     */
    @ApiOperation(value = "获取分类详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        MtGoodsCate mtCate = cateService.queryCateById(id);
        if (mtCate == null) {
            return getFailureResult(201, "该分类不存在");
        }

        if (!staffInfo.getMerchantId().equals(mtCate.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cateInfo", mtCate);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 获取当前员工信息
     */
    private MtStaff getStaffInfo() throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staffInfo == null) {
            throw new BusinessCheckException("您不是商户员工，没有操作权限");
        }
        return staffInfo;
    }

    /**
     * 构造操作人信息
     */
    private AccountInfo buildAccountInfo(MtStaff staffInfo) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(staffInfo.getRealName());
        accountInfo.setMerchantId(staffInfo.getMerchantId());
        accountInfo.setStoreId(staffInfo.getStoreId());
        return accountInfo;
    }
}
