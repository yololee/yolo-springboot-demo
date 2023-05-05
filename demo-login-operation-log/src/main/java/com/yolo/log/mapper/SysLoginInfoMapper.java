package com.yolo.log.mapper;

import com.yolo.log.pojo.SysLoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SysLoginInfoMapper {

    void insertLoginInfo(SysLoginInfo sysLoginInfo);

    List<SysLoginInfo> selectLoginInfoList(SysLoginInfo sysLoginInfo);

    int deleteLoginInfoByIds(String[] ids);

    int cleanLoginInfo();
}
