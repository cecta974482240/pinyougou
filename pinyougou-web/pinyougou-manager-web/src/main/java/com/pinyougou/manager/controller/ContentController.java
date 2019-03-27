package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Content;
import com.pinyougou.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("content")
public class ContentController {
    @Reference(timeout = 10000)
    private ContentService contentService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Content content, Integer page, Integer rows){
        try{
            return contentService.findByPage(content,page,rows);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

