package org.fly.demoword;


import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import org.fly.demoword.poitl.AcWordModel;
import org.fly.demoword.poitl.WordOrder;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class DemoWordApplicationTests {

    @Test
    public void contextLoads() throws IOException {
        //要写入模板的数据
        Map<String,Object> exampleData = new HashMap<>();
        exampleData.put("title", "Hello, poi-tl Word模板引擎");
        exampleData.put("text", "Hello World");
        exampleData.put("author", "god23bin");
        exampleData.put("desc", "这还不关注 god23bin ？再不关注我可要求你关注了！");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/Hello World.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream)
                .render(exampleData);
        //文件输出流
        FileOutputStream out = new FileOutputStream("/Users/huanglei/Desktop/work/giteeCode/yolo-springboot-demo/demo-word/src/main/resources/templates/hello-world.docx");
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }

    @Test
    public void test1(){
        // 获取数据，这里假装是从数据库中查询得到的
        AcWordModel data = getFromDB();
        // 获取 Word 模板所在路径
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/Hello World.docx");
        // 给标签绑定插件，这里就绑定表格行循环的插件
        Configure configure = Configure.builder()
                .bind("orders", new LoopRowTableRenderPolicy())
                .build();
        // 通过 XWPFTemplate 编译文件并渲染数据到模板中
        assert inputStream != null;
        XWPFTemplate template = XWPFTemplate.compile(inputStream, configure).render(data);
        try {
            // 将完成数据渲染的文档写出
            template.writeAndClose(Files.newOutputStream(Paths.get("/Users/huanglei/Desktop/work/giteeCode/yolo-springboot-demo/demo-word/src/main/resources/templates/hello-world2.docx")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test2(){
        // 获取数据，这里假装是从数据库中查询得到的
        Map<String,Object> data = new HashMap<>();
        data.put("orders",getOrderList());
        data.put("total","1000元");

        // 获取 Word 模板所在路径
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/XXX&迈异信息分布式云服务业务协议(云服务模板2222）.docx");
        // 给标签绑定插件，这里就绑定表格行循环的插件
        Configure configure = Configure.builder()
                .bind("orders", new LoopRowTableRenderPolicy())
                .build();
        // 通过 XWPFTemplate 编译文件并渲染数据到模板中
        assert inputStream != null;
        XWPFTemplate template = XWPFTemplate.compile(inputStream, configure).render(data);
        try {
            // 将完成数据渲染的文档写出
            template.writeAndClose(Files.newOutputStream(Paths.get("/Users/huanglei/Desktop/work/giteeCode/yolo-springboot-demo/demo-word/src/main/resources/templates/XXX&迈异信息分布式云服务业务协议(云服务模板3333）.docx")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getOrderList() {

        WordOrder wordOrder = new WordOrder();
        wordOrder.setNum("1");
        wordOrder.setType("云主机");
        wordOrder.setContent("hhhhh");
        wordOrder.setPrice("100");
        wordOrder.setUnit("园");
        wordOrder.setCount("2");
        wordOrder.setDuration("一个月");
        wordOrder.setDiscountTotal("20元");
        wordOrder.setRemark("");

        WordOrder wordOrder2 = new WordOrder();
        wordOrder2.setNum("2");
        wordOrder2.setType("云主机");
        wordOrder2.setContent("hhhhh");
        wordOrder2.setPrice("100");
        wordOrder2.setUnit("园");
        wordOrder2.setCount("2");
        wordOrder2.setDuration("一个月");
        wordOrder2.setDiscountTotal("20元");
        wordOrder2.setRemark("");

        ArrayList<WordOrder> wordOrders = new ArrayList<>();
        wordOrders.add(wordOrder);
        wordOrders.add(wordOrder2);
        return wordOrders;
    }

    private AcWordModel getFromDB() {
        AcWordModel acWordModel = new AcWordModel();
        WordOrder wordOrder = new WordOrder();
        wordOrder.setNum("1");
        wordOrder.setType("云主机");
        wordOrder.setContent("hhhhh");
        wordOrder.setPrice("100");
        wordOrder.setUnit("园");
        wordOrder.setCount("2");
        wordOrder.setDuration("一个月");
        wordOrder.setDiscountTotal("20元");
        wordOrder.setRemark("");

        WordOrder wordOrder2 = new WordOrder();
        wordOrder2.setNum("2");
        wordOrder2.setType("云主机");
        wordOrder2.setContent("hhhhh");
        wordOrder2.setPrice("100");
        wordOrder2.setUnit("园");
        wordOrder2.setCount("2");
        wordOrder2.setDuration("一个月");
        wordOrder2.setDiscountTotal("20元");
        wordOrder2.setRemark("");

        ArrayList<WordOrder> wordOrders = new ArrayList<>();
        wordOrders.add(wordOrder);
        wordOrders.add(wordOrder2);

        Map<String,Object> exampleData = new HashMap<>();
        exampleData.put("title", "Hello, poi-tl Word模板引擎");
        exampleData.put("text", "Hello World");
        exampleData.put("author", "god23bin");
        exampleData.put("desc", "这还不关注 god23bin ？再不关注我可要求你关注了！");

        acWordModel.setOrders(wordOrders);
        acWordModel.setExampleData(exampleData);


        return acWordModel;
    }


}
