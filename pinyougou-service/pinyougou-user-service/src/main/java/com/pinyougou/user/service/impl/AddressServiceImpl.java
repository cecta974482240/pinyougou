package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 地址服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-03-20<p>
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional(rollbackFor = RuntimeException.class)
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private AreasMapper areasMapper;

    @Override
    public void save(Address address) {

    }

    @Override
    public void update(Address address) {
        try {
            if (address.getId() == null){
            address.setCreateDate(new Date());
            address.setIsDefault("0");
            addressMapper.insertSelective(address);
            }else {
                addressMapper.updateByPrimaryKey(address);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(Serializable id) {
        try {
            addressMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        try {
            //根据ID返回用户地址对象
            return  addressMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    /*初始化页面显示地址详细信息*/
    @Override
    public List<Address> showAddress(String loginName) {
        try {
            Example example = new Example(Address.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", loginName);
            example.orderBy("isDefault").desc();
            List<Address> addresses = addressMapper.selectByExample(example);

            for (Address address : addresses) {
                /*给省赋值*/
                Example province = new Example(Provinces.class);
                Example.Criteria criteria1 = province.createCriteria();
                criteria1.andEqualTo("provinceId", address.getProvinceId());
                address.setProvinceId(provincesMapper.selectByExample(province).get(0).getProvince());
                /*给市赋值*/
                Example cities = new Example(Cities.class);
                Example.Criteria criteria2 = cities.createCriteria();
                criteria2.andEqualTo("cityId", address.getCityId());
                address.setCityId(citiesMapper.selectByExample(cities).get(0).getCity());
                /*给区赋值*/
                Example areas = new Example(Areas.class);
                Example.Criteria criteria3 = areas.createCriteria();
                criteria3.andEqualTo("areaId", address.getTownId());
                address.setTownId(areasMapper.selectByExample(areas).get(0).getArea());
            }
            return addresses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void selectDefault(Serializable id) {
        try {
            //1 取消已经默认的地址
            Example example = new Example(Address.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("isDefault","1" );
            Address as = addressMapper.selectByExample(example).get(0);
            as.setIsDefault("0");
            addressMapper.updateByPrimaryKey(as);
            //2 根据传过来的id修改该用户地址为默认
            Address ad = addressMapper.selectByPrimaryKey(id);
            ad.setIsDefault("1");
            addressMapper.updateByPrimaryKey(ad);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
