package com.fuint.application.service.source;

import com.fuint.application.domain.TreeSelect;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.service.entities.TreeNode;
import com.fuint.base.service.source.TSourceService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.vo.RouterVo;
import com.fuint.application.vo.MetaVo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单管理接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class SourceServiceImpl implements SourceService {

    @Autowired
    private TSourceService tSourceService;

    /**
     * 根据会员ID获取员工信息
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     */
    @Override
    public List<TSource> getMenuListByUserId(Integer userId) {
        List<TSource> sourceList = tSourceService.findSourcesByAccountId(userId);
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
    public List<TreeNode> buildMenuTree(List<TreeNode> menus)
    {
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
     * 菜单去重
     *
     * @param sources
     * @return
     */
    private List<TSource> delRepeated(List<TSource> sources) {
        List<TSource> distinct = new ArrayList<>();
        if (sources != null) {
            Map<Long, Boolean> sourceMap = new HashedMap();
            for (TSource tSource : sources) {
                if (sourceMap.get(tSource.getId()) == null) {
                    sourceMap.put(tSource.getId(), true);
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
        List<TreeNode> tlist = new ArrayList<TreeNode>();
        Iterator<TreeNode> it = list.iterator();
        while (it.hasNext()) {
            TreeNode n = (TreeNode) it.next();
            if (n.getpId() == t.getId()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<TreeNode> list, TreeNode t) {
        return getChildList(list, t).size() > 0;
    }
}
