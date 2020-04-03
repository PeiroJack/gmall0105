package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.manage.util.PmsUploadUtil;
import com.atguigu.gmall.service.SpuService;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;


    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> pmsProductImageList = spuService.spuImageList(spuId);
        return pmsProductImageList;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = spuService.baseSaleAttrList();
        return pmsBaseSaleAttrs;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        String result = spuService.saveSpuInfo(pmsProductInfo);

        return result;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        //将图片或者视频上传到分布式的文件存储系统
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        //将图片的存储路劲返回给页面
        return imgUrl;
    }


}
