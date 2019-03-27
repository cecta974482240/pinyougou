package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.ContentCategory;

import com.pinyougou.service.ContentCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference(timeout = 1000)
    private ContentCategoryService contentCategoryService;

    /**多条件查询方法 */
    @GetMapping("/findByPage")
    public PageResult findByPage(ContentCategory contentCategory, Integer page, Integer rows){
        try {
            return contentCategoryService.findByPage(contentCategory,page,rows);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/findAll")
    public List<ContentCategory> findAll(){
        try{
            return contentCategoryService.findAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
