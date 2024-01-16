package org.fly.demoword.poitl;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AcWordModel
 * @Description: 我还没有写描述
 * @Date: 2024/1/16 15:12
 * @author: Blue
 */
@Data
public class AcWordModel {

    /**
     * 文章明细数据模型-表格行循环
     */
    private List<WordOrder> orders;

    private Map<String,Object> exampleData;

}
