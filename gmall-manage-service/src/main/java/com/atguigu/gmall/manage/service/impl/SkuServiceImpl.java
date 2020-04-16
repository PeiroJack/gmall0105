package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import redis.clients.jedis.Jedis;


import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //添加到商品库存单元信息表PmsSkuInfo中
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }


        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        //添加到商品库存单元图片表PmsSkuImage中
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        // sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        // sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {
        //查寻SkuInfo

        System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"进入的商品详情的请求");

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis = null;
        try {
            //链接缓存
            jedis = redisUtil.getJedis();

            //查询缓存
            String skuKey = "sku:" + skuId + ":info";
            String skuJson = jedis.get(skuKey);

            if (StringUtils.isNotBlank(skuJson)) {
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"从缓存中获取商品详情");
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            } else {
                //如果缓存中没有，查询mysql
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"发现缓存中没有，申请缓存的分布式锁："+"sku:"+skuId+":lock");
                //设置分布式锁
                String token = UUID.randomUUID().toString();
                String OK = jedis.set("sku:"+skuId+":lock", token, "nx", "px",10*1000);//拿到锁的线程有10秒的过期时间
                if (StringUtils.isNotBlank(OK)&& OK.equals("OK")) {
                    //设置成功，有权在10秒的过期时间内访问数据库
                    System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"有权在10秒的过期时间内访问数据库："+"sku:"+skuId+":lock");
                    pmsSkuInfo = getSkuByIdFromDb(skuId);

                    //为了看出效果，让拿到锁的线程睡5秒
                    try {
                        Thread.sleep(1000*5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //mysql查询结果存入redis
                    if (pmsSkuInfo != null) {
                        jedis.set(skuKey, JSON.toJSONString(pmsSkuInfo));
                    } else {
                        //数据库中不存在该sku
                        //为了防止缓存穿透将 null 或者 空字符串值设置给redis
                        jedis.setex(skuKey, 60 * 3, JSON.toJSONString(""));
                    }

                    //在访问mysql后，将mysql的分布式锁释放
                    System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"使用完毕，将锁归还："+"sku:"+skuId+":lock");

                    String lockToken = jedis.get("sku:" + skuId + ":lock");
                    if (StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                        jedis.del("sku:"+skuId+":lock");//用token确认删除的是自己的sku的锁
                    }

                    //jedis.eval("lua");可以用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                    //String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    //jedis.eval(script, Collections.singletonList("sku:" + skuId + ":lock"),Collections.singletonList(token));

                }else{
                    //设置失败,自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                    System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"没有拿到锁，开始自旋");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return getSkuById(skuId,ip);
                }

            }
        } finally {
            jedis.close();
        }


        return pmsSkuInfo;


    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}
