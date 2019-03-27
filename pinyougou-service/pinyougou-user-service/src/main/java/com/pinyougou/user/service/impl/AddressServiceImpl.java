package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * 地址服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-03-20<p>
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(Address address) {

    }

    @Override
    public void update(Address address) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    @Override
    public List<Address> findAddressByUser(String userId) {
        try{
            // SELECT * FROM `tb_address` WHERE user_id = 'itcast' ORDER BY is_default DESC
            // 创建Example对象
            Example example = new Example(Address.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = 'itcast'
            criteria.andEqualTo("userId", userId);
            // 排序  ORDER BY is_default DESC
            example.orderBy("isDefault").desc();

            // 条件查询
            return addressMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询所有省份
     */
    @Override
    public List<Provinces> findProvinceid() {
        try{
            return addressMapper.findProvinceid();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 监控 user.address.provinceId 变量,查询城市
     */
    @Override
    public List<Cities> findCityById(Long provinceId) {
        try{
            return addressMapper.findCityById(provinceId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询城区信息
     *
     * @param ciytId
     */
    @Override
    public List<Areas> findAreaById(Long ciytId) {
        try{
            return addressMapper.findAreaById(ciytId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
