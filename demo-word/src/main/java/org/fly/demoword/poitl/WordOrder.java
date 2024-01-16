package org.fly.demoword.poitl;

import lombok.Data;

@Data
public class WordOrder {


    private String num;
    private String type;
    private String content;
    private String price;
    private String unit;
    private String count;
    private String duration;
    private String discountTotal;
    private String remark;
}