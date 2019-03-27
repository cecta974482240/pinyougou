package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.AreasService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class addressController {
    @Reference(timeout = 100000)
    private ProvincesService provincesService;
    @Reference(timeout = 100000)
    private CitiesService citiesService;
    @Reference(timeout = 100000)
    private AreasService areasService;
    @Reference(timeout = 100000)
    private AddressService addressService;



    @GetMapping("/findAllProvinces")
    public List<Provinces> findItemCatByParentId(Long parentId){
        try {
            return provincesService.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    @GetMapping("/findCitiesByParentId")
    public List<Cities> findCitiesByParentId(Long parentId) {
        try {
            return citiesService.findCitiesByParentId(parentId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @GetMapping("/findAreaByParentId")
    public List<Areas> findAreaByParentId(Long parentId) {
        try {
            return areasService.findAreaByParentId(parentId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    @PostMapping("/insertAddress")
    public boolean insertAddress(@RequestBody Address address){
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @GetMapping("/showAddress")
    public List<Address> showAddress(String loginName){
        return addressService.showAddress(loginName);
    }
    //删除买家地址
    @GetMapping("/deleteAddress")
    public boolean deleteAddress(Long id){
        try {
            addressService.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //编辑买家地址-获取地址
    @GetMapping("/getAddress")
    public Address getAddress(Long id){
        try {
            return addressService.findOne(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //编辑买家地址-获取地址
    @PostMapping("/updateAddress")
    public boolean updateAddress(@RequestBody Address address){
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //修改地址默认状态码
    @GetMapping("/selectDefault")
    public boolean selectDefault(Long id){
        try {
             addressService.selectDefault(id);
             return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
