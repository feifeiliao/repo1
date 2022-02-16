package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("insert into tb_category_brand (category_id,brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据brand id删除中间表相关数据
     * @param bid
     */
    @Delete("delete from tb_category_brand WHERE brand_id = #{bid}")
    void deleteByBrandIdInCategoryBrand(@Param("bid") Long bid);

    /**
     * 根据cid查询所有品牌
     * @param cid
     * @return
     */
    @Select("SELECT * FROM tb_brand INNER JOIN tb_category_brand ON tb_brand.`id` = tb_category_brand.`brand_id` WHERE tb_category_brand.`category_id`=#{cid}")
    List<Brand> selectBrandsByCid(@Param("cid") Long cid);
}
