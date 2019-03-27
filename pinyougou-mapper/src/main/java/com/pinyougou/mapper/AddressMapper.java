package com.pinyougou.mapper;

import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Address;

import java.util.List;

/**
 * AddressMapper 数据访问接口
 * @date 2019-02-27 09:55:07
 * @version 1.0
 */
public interface AddressMapper extends Mapper<Address>{

    /** 查询所有省份 */
    @Select("select * from tb_provinces")
    List<Provinces> findProvinceid();

    /** 查询城市 */
    @Select("SELECT * from tb_cities where provinceid = #{provinceId}")
    List<Cities> findCityById(Long provinceId);
    /** 查询城区信息 */
    @Select("SELECT * from tb_areas where cityid = #{ciytId}")
    List<Areas> findAreaById(Long ciytId);
}