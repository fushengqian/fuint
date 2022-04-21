package com.fuint.application.web.backend.cashierManager;

import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtGoodsCate;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.store.StoreService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收银管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/cashier")
public class cashierManagerController {

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 商品类别服务接口
     * */
    @Autowired
    private CateService cateService;

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    @Autowired
    private Environment env;

    /**
     * 收银台首页
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/cashier/index")
    @RequestMapping(value = "/index")
    public String cashier(HttpServletRequest request, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        model.addAttribute("cateList", cateList);

        Map<String, Object> goodsParam = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoods> goodsList = goodsService.getStoreGoodsList(storeId);
        model.addAttribute("goodsList", goodsList);

        model.addAttribute("storeId", storeId);

        MtStore storeInfo = storeService.queryStoreById(storeId);
        model.addAttribute("storeInfo", storeInfo);

        String imagePath = env.getProperty("images.upload.url");
        model.addAttribute("imagePath", imagePath);

        return "cashier/index";
    }
}
