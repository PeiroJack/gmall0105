package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductImageMapper pmsProducetImageMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);

        return pmsProductInfos;
    }



    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
//        Pms_product_info
//        Pms_product_image
//        Pms_product_sale_attr
//        Pms_procuct_sale_attr_value
//        Pms_base_sale_attr
        // 保存商品信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        // 生成商品主键
        String productId = pmsProductInfo.getId();

        // 保存商品图片信息
        List<PmsProductImage> SpuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : SpuImageList) {
            pmsProductImage.setProductId(productId);
            pmsProducetImageMapper.insertSelective(pmsProductImage);
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> SpuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : SpuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productId);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            // 保存销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productId);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }


        return "success";
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {

        return pmsBaseSaleAttrMapper.selectAll();
    }
}
