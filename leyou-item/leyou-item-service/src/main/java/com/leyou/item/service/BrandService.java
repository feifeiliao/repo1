package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandsByPages(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
     // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        // key:模糊查询,name或者首字母查询
        if((StringUtils.isNotBlank(key))){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //page，rows 添加分页条件
        PageHelper.startPage(page,rows);
        // sortBy desc 添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }

        List<Brand> brands = this.brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void addBrands(Brand brand, @RequestParam("cids") List<Long> cids) {
        // 先新增Brand表
        this.brandMapper.insertSelective(brand);
        // 新增中间表

        cids.forEach(cid ->{
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 品牌更新
     * @param brand
     * @param cids
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) {
        // 删除中间表信息
        deleteByBrandIdInCategoryBrand(brand.getId());
        // 修改品牌信息
        this.brandMapper.updateByPrimaryKeySelective(brand);
        for (Long cid : cids) {
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        }
    }

    /**
     * 删除中间表中的数据
     * @param bid
     */
    public void deleteByBrandIdInCategoryBrand(Long bid) {
        brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }

    /**
     * 删除品牌
     * @param bid
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(Long bid) {
        // 删除品牌表中信息
        this.brandMapper.deleteByPrimaryKey(bid);
        //删除中间表信息
        deleteByBrandIdInCategoryBrand(bid);
    }

    /**
     * 根据cid查询所有的品牌信息
     * @param cid
     * @return
     */
    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.selectBrandsByCid(cid);
    }

    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
