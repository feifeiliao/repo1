package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    public Map<String,Object> loadData(Long SpuId){
        Map<String,Object> model = new HashMap<>();

        // 查询spu
        Spu spu = this.goodsClient.querySpuById(SpuId);
        // 查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(SpuId);
        // 查询categories Map<string object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        // 初始化一个目录的分类list
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }
        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        // 查询skus
        List<Sku> skus = this.goodsClient.querySkusBuSpuId(SpuId);
        // 查询groups
        List<SpecGroup> groups = this.specificationClient.queryGroupsWithParam(spu.getCid3());
        // 查询特殊规格参数
        List<SpecParam> params = this.specificationClient.queryParamsByGid(null, spu.getCid3(), false, null);
        // 初始化特殊规格参数的map
        Map<Long,String> paramMap = new HashMap<>();
        params.forEach(param->{
            paramMap.put(param.getId(),param.getName());
        });

        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",groups);
        model.put("paramMap",paramMap);

        return model;
    }
}
