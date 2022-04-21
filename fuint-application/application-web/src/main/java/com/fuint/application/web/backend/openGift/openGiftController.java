package com.fuint.application.web.backend.openGift;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtOpenGift;
import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dto.OpenGiftDto;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.opengift.OpenGiftService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开卡礼管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/openGift")
public class openGiftController {

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 开卡赠礼服务接口
     */
    @Autowired
    private OpenGiftService openGiftService;

    /**
     * 开卡礼列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/openGift/list")
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        Map<String, Object> param = new HashMap<>();
        param.put("couponId", params.get("EQ_couponId"));
        param.put("gradeId", params.get("EQ_gradeId"));
        param.put("pageNumber", paginationRequest.getCurrentPage());
        param.put("pageSize", paginationRequest.getPageSize());

        ResponseObject response = openGiftService.getOpenGiftList(param);

        model.addAttribute("paginationResponse", response.getData());
        model.addAttribute("params", params);

        return "openGift/list";
    }

    /**
     * 新增
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/openGift/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, Model model) throws BusinessCheckException {
        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);
        model.addAttribute("userGradeMap", userGradeMap);

        return "openGift/add";
    }

    /**
     * 编辑
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/openGift/edit/{id}")
    @RequestMapping(value = "/edit/{id}")
    public String edit(HttpServletRequest request, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);
        model.addAttribute("userGradeMap", userGradeMap);

        OpenGiftDto openGiftInfo = openGiftService.getOpenGiftDetail(id);
        model.addAttribute("openGiftInfo", openGiftInfo);

        return "openGift/edit";
    }

    /**
     * 提交处理
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/openGift/handleSave")
    @RequestMapping(value = "/handleSave", method = RequestMethod.POST)
    public String handleSave(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String id = request.getParameter("id");
        String gradeId = request.getParameter("gradeId");
        String couponId = request.getParameter("couponId");
        String couponNum = request.getParameter("couponNum");
        String point = request.getParameter("point");
        String status = request.getParameter("status");

        if (StringUtils.isEmpty(couponId)) {
            throw new BusinessRuntimeException("卡券不能为空");
        }
        if (StringUtils.isEmpty(couponNum)) {
            throw new BusinessRuntimeException("卡券数量不能为空");
        }
        if (StringUtils.isEmpty(gradeId)) {
            throw new BusinessRuntimeException("会员等级不能为空");
        }

        MtOpenGift reqDto = new MtOpenGift();
        reqDto.setCouponId(Integer.parseInt(couponId));
        reqDto.setCouponNum(Integer.parseInt(couponNum));
        reqDto.setGradeId(Integer.parseInt(gradeId));
        reqDto.setPoint(Integer.parseInt(point));
        reqDto.setStoreId(0);
        reqDto.setStatus(status);
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqDto.setOperator(operator);

        if (StringUtils.isNotEmpty(id)) {
            reqDto.setId(Integer.parseInt(id));
            openGiftService.updateOpenGift(reqDto);
        } else {
            openGiftService.addOpenGift(reqDto);
        }

        return "redirect:/backend/openGift/list";
    }
}
