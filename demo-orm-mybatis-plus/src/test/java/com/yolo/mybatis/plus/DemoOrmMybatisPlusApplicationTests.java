package com.yolo.mybatis.plus;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yolo.mybatis.plus.domain.Company;
import com.yolo.mybatis.plus.mapper.CompanyMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DemoOrmMybatisPlusApplicationTests {

    @Autowired
    private CompanyMapper companyMapper;

    @Test
    public void insertTest(){
        Company company = new Company();
        company.setName("腾讯");
        company.setContact("李四");
        company.setContactType("17683720003");
        company.setRemoved(0);
        companyMapper.insert(company);
    }


    @Test
    public void updateTest1(){
        Company company = new Company();
        company.setId(1656900129375956993L);
        company.setName("腾讯");
        company.setContact("李四1113423");
        company.setContactType("17683720005");
        company.setRemoved(0);
        companyMapper.updateById(company);
    }

    @Test
    public void updateTest2(){
        //第一种
        Company company = new Company();
        company.setContact("张三222"); //需要更新的字段
        //queryWrapper对象，用于设置条件
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",1656854581440679937L);//设置查询条件
        companyMapper.update(company,queryWrapper);
    }

    @Test
    public void updateTest3(){
        //第二种
        //UpdateWrapper更新操作
        UpdateWrapper<Company> warp = new UpdateWrapper<>();
        //通过set设置需要修改的内容，eq设置条件
        warp.set("name","阿里111").set("contact","zhansgan3333").eq("id",1656854581440679937L);
        companyMapper.update(null,warp);
    }

    @Test
    public void selectTest(){
        //根据id查询
        Company company = companyMapper.selectById(6);
        System.out.println(company);

        //根据id集合查询
        List<Company> companyList = companyMapper.selectBatchIds(ListUtil.of(1656854581440679937L, 1656856229496020993L));
        System.out.println(companyList);

        //根据条件查询一个
        QueryWrapper<Company> query = new QueryWrapper<>();
        query.eq("name","华为");
        Company company1 = companyMapper.selectOne(query);
        System.out.println(company1);

        //根据map查询
        Map<String, Object> map = new HashMap<>();
        map.put("contact","张三");
        List<Company> companyList1 = companyMapper.selectByMap(map);
        System.out.println(companyList1);

        //根据条件查询个数
        QueryWrapper<Company> query1 = new QueryWrapper<>();
        query1.eq("contact","张三");
        Integer integer = companyMapper.selectCount(query1);
        System.out.println(integer);
        //根据条件查询多个
        List<Company> companyList2 = companyMapper.selectList(query1);
        System.out.println(companyList2);
    }

    @Test
    public void pageTest(){
        IPage<Company> page = new Page<>(1,1);
        IPage<Company> companyIPage = companyMapper.selectPage(page, null);

        System.out.println(companyIPage);
    }


    @Test
    public void deleteTest(){
        int i = companyMapper.deleteById(1656856229496020993L);
        System.out.println(i);
    }



}
