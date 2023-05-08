package com.yolo.tree.pojo;


import com.yolo.tree.annotation.TreeChild;
import com.yolo.tree.annotation.TreeEntity;
import com.yolo.tree.annotation.TreeId;
import com.yolo.tree.annotation.TreePid;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@ToString
@TreeEntity
public class SysMenu extends BaseEntity{
    private static final long serialVersionUID = 1L;


    /**
     * 菜单ID
     */
    @TreeId
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 父菜单ID
     */
    @TreePid
    private Long parentId;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 菜单URL
     */
    private String url;

    /**
     * 打开方式（menuItem页签 menuBlank新窗口）
     */
    private String target;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 是否刷新（0刷新 1不刷新）
     */
    private String isRefresh;

    /**
     * 权限字符串
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 子菜单
     */
    @TreeChild
    private List<SysMenu> children = new ArrayList<>();


}
