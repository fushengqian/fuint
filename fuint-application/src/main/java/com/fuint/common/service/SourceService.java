package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.domain.TreeSelect;
import com.fuint.common.vo.RouterVo;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.common.domain.TreeNode;
import com.fuint.repository.model.TSource;
import java.util.List;

/**
 * 菜单管理业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SourceService extends IService<TSource> {

    /**
     * 获取有效的菜单集合
     *
     * @return
     */
    List<TSource> getAvailableSources();

    /**
     * 获取菜单的属性结构
     *
     * @return
     */
    List<TreeNode> getSourceTree();

    /**
     * 根据菜单ID集合查询菜单列表信息
     *
     * @param ids
     * @return
     */
    List<TSource> findDatasByIds(String[] ids);

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

    /**
     * 添加菜单
     *
     * @param tSource
     */
    void addSource(TSource tSource);

    /**
     * 修改菜单
     *
     * @param source
     * */
    void editSource(TSource source);
}
