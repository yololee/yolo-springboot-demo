package com.yolo.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.yolo.mybatis.dto.RoleDTO;
import com.yolo.mybatis.mapper.RoleMapper;
import com.yolo.mybatis.pojo.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = DemoOrmMybatisApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class RoleMapperXmlTest {

    @Autowired
    private RoleMapper roleMapper;


    @Test
    public void saveUser() {
        Role role = Role.builder().roleName("管理员").roleSort(1).status("0").remark("test").build();
        Integer num = roleMapper.save(role);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void insertList() {
        Role role1 = Role.builder().roleName("运维").roleSort(2).status("0").remark("test").build();
        Role role2 = Role.builder().roleName("运维管理员").roleSort(2).status("0").remark("test").build();
        Integer num = roleMapper.insertList(ListUtil.of(role1, role2));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void updateBatchStateById() {
        Integer num = roleMapper.updateBatchStateById(ListUtil.of(9,10),1);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void updateBatch() {
        Role role1 = Role.builder().id(9L).roleName("运维").roleSort(2).status("0").remark("test2222").build();
        Role role2 = Role.builder().id(10L).roleName("运维管理员").roleSort(2).status("0").remark("test1111").build();
        Integer num = roleMapper.updateBatch(ListUtil.of(role1, role2));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void selectAll(){
        List<Role> roleList = roleMapper.selectAll();
        Assert.assertTrue(CollUtil.isNotEmpty(roleList));
        log.debug("【roleList】= {}", roleList);
    }

    @Test
    public void selectById() {
        Role role = roleMapper.selectById(10L);
        Assert.assertNotNull(role);
        log.debug("【role】= {}", role);
    }

    @Test
    public void selectByIds() {
        List<Role> roleList = roleMapper.selectByIds(ListUtil.of(9, 10));
        Assert.assertNotNull(roleList);
        log.debug("【roleList】= {}", roleList);
    }


    @Test
    public void selectPage() {
        List<Role> roleList = roleMapper.selectPage(RoleDTO.builder().roleName("员").build());
        Assert.assertNotNull(roleList);
        log.debug("【roleList】= {}", roleList);
    }


    @Test
    public void deleteById() {
        Integer num = roleMapper.deleteById(6L);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void batchDelete() {
        Integer num = roleMapper.batchDelete(ListUtil.of(10,11));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

}
