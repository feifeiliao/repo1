package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 根据条件分页查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加查询条件
        if(StringUtils.isNotBlank(key)){

            criteria.andLike("title","%"+key+"%");
        }
        //添加上下架的过滤条件
        if(saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }
        // 分页条件
        PageHelper.startPage(page,rows);
        // 执行查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        // spu集合转化为spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            // 查询产品名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            // 查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));

            return spuBo;
        }).collect(Collectors.toList());
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 保存商品
     * @param spuBo
     * @return
     */
    /*
    整体是一个json格式数据，包含Spu表所有数据：

- brandId：品牌id
- cid1、cid2、cid3：商品分类id
- subTitle：副标题
- title：标题
- spuDetail：是一个json对象，代表商品详情表数据
  - afterService：售后服务
  - description：商品描述
  - packingList：包装列表
  - specialSpec：sku规格属性模板
  - genericSpec：通用规格参数
- skus：spu下的所有sku数组，元素是每个sku对象：
  - title：标题
  - images：图片
  - price：价格
  - stock：库存
  - ownSpec：特有规格参数
  - indexes：特有规格参数的下标

  spu:
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    private Date lastUpdateTime;// 最后修改时间
 SpuDetail:
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String specialSpec;// 商品特殊规格的名称及可选值模板
    private String genericSpec;// 商品的全局规格属性
    private String packingList;// 包装清单
    private String afterService;// 售后服务
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        // 新增spu
        // id、saleable、valid、createTime、lastUpdateTime没有传递
        spuBo.setId(null);//自增长
        spuBo.setSaleable(true);//上架
        spuBo.setValid(true);//有效
        spuBo.setCreateTime(new Date());//现在创建的
        spuBo.setLastUpdateTime(spuBo.getCreateTime());// 和创建时间保持一致
        this.spuMapper.insertSelective(spuBo);

        // 新增spudetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        // 新增sku
        // 缺少id，spuId,enable、creatTime、lastUpdateTime
        saveSkuAndStock(spuBo);


        sendMsg("insert",spuBo.getId());

    }

    private void sendMsg(String type,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        for (Sku sku : skus) {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            // 新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        }
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku集合
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBuSpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(s -> {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        // 删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(sku);
        // 删除库存
        skus.forEach(s ->{
            this.stockMapper.deleteByPrimaryKey(s.getId());
        });
        this.skuMapper.delete(sku);
        // 更新sku
        saveSkuAndStock(spuBo);

        // 更新spu
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMsg("update",spuBo.getId());
    }
    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
