package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.domain.TreeSelect;
import com.fuint.common.service.SourceService;
import com.fuint.common.vo.MetaVo;
import com.fuint.common.vo.RouterVo;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.repository.mapper.TSourceMapper;
import com.fuint.repository.model.TSource;
import com.fuint.common.domain.TreeNode;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.utils.ArrayUtil;
import com.fuint.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单管理接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SourceServiceImpl extends ServiceImpl<TSourceMapper, TSource> implements SourceService {

    @Resource
    private TSourceMapper tSourceMapper;

    /**
     * 获取有效的角色集合
     *
     * @return
     */
    @Override
    public List<TSource> getAvailableSources() {
        return tSourceMapper.findByStatus("A");
    }

    /**
     * 获取菜单的属性结构
     *
     * @return
     */
    @Override
    public List<TreeNode> getSourceTree() {
        List<TSource> tSources = this.getAvailableSources();
        List<TreeNode> trees = new ArrayList<>();
        if (tSources != null && tSources.size() > 0) {
            TreeNode sourceTreeNode = null;
            for (TSource tSource : tSources) {
                sourceTreeNode = new TreeNode();
                sourceTreeNode.setName(tSource.getSourceName());
                sourceTreeNode.setId(tSource.getSourceId());
                sourceTreeNode.setLevel(tSource.getSourceLevel());
                sourceTreeNode.setSort((tSource.getSourceStyle() == null || StringUtil.isEmpty(tSource.getSourceStyle())) ? 0 : Integer.parseInt(tSource.getSourceStyle()));
                sourceTreeNode.setPath(tSource.getPath());
                sourceTreeNode.setIcon(tSource.getNewIcon());
                sourceTreeNode.setIsMenu(tSource.getIsMenu());
                sourceTreeNode.setStatus(tSource.getStatus());
                sourceTreeNode.setPerms(tSource.getPath().replaceAll("/", ":"));
                if (tSource.getParentId() != null) {
                    sourceTreeNode.setpId(tSource.getParentId());
                } else {
                    sourceTreeNode.setpId(0);
                }
                trees.add(sourceTreeNode);
            }
        }
        return trees;
    }

    /**
     * 根据菜单ID集合查询菜单列表信息
     *
     * @param ids
     * @return
     */
    @Override
    public List<TSource> findDatasByIds(String[] ids) {
        Long[] arrays = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
             arrays[i] = Long.parseLong(ids[i]);
        }
        return tSourceMapper.findByIdIn(ArrayUtil.toList(arrays));
    }

    /**
     * 根据账号ID获取菜单列表
     *
     * @param  accountId 账号ID
     * @throws BusinessCheckException
     */
    @Override
    public List<TSource> getMenuListByUserId(Integer accountId) {
        List<TSource> sourceList = tSourceMapper.findSourcesByAccountId(accountId);
        return delRepeated(sourceList);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param treeNodes 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<TreeNode> treeNodes) {
        List<RouterVo> routers = new LinkedList<>();

        for (TreeNode menu : treeNodes) {
            RouterVo router = new RouterVo();
            if (menu.getIsMenu() == 1) {
                router.setHidden(false);
            } else {
                router.setHidden(true);
            }
            router.setName(menu.getEname());
            if (menu.getLevel() == 1) {
                router.setComponent("Layout");
                router.setPath("/" + menu.getEname().toLowerCase());
                router.setRedirect("noRedirect");
                router.setAlwaysShow(true);
            } else {
                router.setComponent(menu.getPath());
                router.setPath('/' + menu.getPath());
            }
            router.setMeta(new MetaVo(menu.getName(), menu.getNewIcon(), false, null));
            List<TreeNode> cMenus = menu.getChildrens();
            if (cMenus != null && !cMenus.isEmpty() && cMenus.size() > 0) {
                router.setChildren(buildMenus(cMenus));
            }
            routers.add(router);
        }

        return routers;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<TreeNode> menus) {
        List<TreeNode> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<TreeNode> buildMenuTree(List<TreeNode> menus) {
        List<TreeNode> returnList = new ArrayList<TreeNode>();
        List<Long> tempList = new ArrayList<Long>();
        for (TreeNode dept : menus) {
            tempList.add(dept.getId());
        }
        for (Iterator<TreeNode> iterator = menus.iterator(); iterator.hasNext();) {
            TreeNode menu = (TreeNode) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getpId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 添加菜单
     *
     * @param tSource
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增后台菜单")
    public void addSource(TSource tSource) {
        this.save(tSource);
    }

    /**
     * 修改菜单
     *
     * @param source
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改后台菜单")
    public void editSource(TSource source) {
        tSourceMapper.updateById(source);
    }

    /**
     * 菜单去重
     *
     * @param sources
     * @return
     */
    private List<TSource> delRepeated(List<TSource> sources) {
        List<TSource> distinct = new ArrayList<>();
        if (sources != null) {
            Map<Long, Boolean> sourceMap = new HashMap<>();
            for (TSource tSource : sources) {
                if (sourceMap.get(tSource.getSourceId()) == null) {
                    sourceMap.put(tSource.getSourceId().longValue(), true);
                    distinct.add(tSource);
                }
            }
        }
        return distinct;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<TreeNode> list, TreeNode t) {
        // 得到子节点列表
        List<TreeNode> childList = getChildList(list, t);
        t.setChildrens(childList);
        for (TreeNode tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<TreeNode> getChildList(List<TreeNode> list, TreeNode t) {
        List<TreeNode> tList = new ArrayList<TreeNode>();
        Iterator<TreeNode> it = list.iterator();
        while (it.hasNext()) {
            TreeNode n = it.next();
            if (n.getpId() == t.getId()) {
                tList.add(n);
            }
        }
        return tList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<TreeNode> list, TreeNode t) {
        return getChildList(list, t).size() > 0;
    }
}
