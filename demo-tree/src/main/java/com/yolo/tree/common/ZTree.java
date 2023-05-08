package com.yolo.tree.common;

import lombok.*;

import java.io.Serializable;

/**
 * ZTree树结构实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ZTree implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点父ID
     */
    private Long pId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点标题
     */
    private String title;

    /**
     * 是否勾选
     */
    private boolean checked = false;

    /**
     * 是否展开
     */
    private boolean open = false;

    /**
     * 是否能勾选
     */
    private boolean nocheck = false;

}
