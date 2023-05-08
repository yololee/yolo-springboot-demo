package com.yolo.tree;

import com.yolo.tree.mapper.SysMenuMapper;
import com.yolo.tree.pojo.SysMenu;
import com.yolo.tree.util.TreeUtilV3;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DemoTreeApplicationTests {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Test
    public void testTree1() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectMenuAll();
        List<SysMenu> childPerms = getChildPerms(sysMenuList, 0);
        System.out.println(childPerms);
    }

    @Test
    public void testTree2() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectMenuAll();
        List<SysMenu> sysMenus = TreeUtilV3.toTreeList(sysMenuList, 0L, SysMenu.class);

        System.out.println(sysMenus);

//        SysMenu sysMenu = sysMenuList.get(0);
//        SysMenu sysMenu1 = TreeUtilV3.toTree(sysMenuList, sysMenu, SysMenu.class);
//        System.out.println(sysMenu1);


    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

}
