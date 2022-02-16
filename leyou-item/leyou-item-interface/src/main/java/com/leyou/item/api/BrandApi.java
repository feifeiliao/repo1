package com.leyou.item.api;


import com.leyou.item.pojo.Brand;


import org.springframework.web.bind.annotation.*;


@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据商品品牌id，查询商品的品牌
     */
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);


}
