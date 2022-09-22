package com.fuint.application.service.source;

import com.fuint.application.vo.RouterVo;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.service.entities.TreeNode;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.domain.TreeSelect;
import java.util.List;

/**
 * 菜单管理接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface SourceService {

    /**
     * 根据会员ID获取菜单
     *
     * @param  userId 会员ID
     * @throws BusinessCheckException
     */
    List<TSource> getMenuListByUserId(Integer userId) throws BusinessCheckException;

    /**
     * 构建前端路由所需要的菜单
     *
     * @param  treeNodes 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<TreeNode> treeNodes);

    /**
     * 构建前端所需要树结构
     *
     * @param  menus 菜单列表
     * @return 树结构列表
     */
    List<TreeNode> buildMenuTree(List<TreeNode> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<TreeNode> menus);
}
