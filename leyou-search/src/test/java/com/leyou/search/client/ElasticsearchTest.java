package com.leyou.search.client;

import com.leyou.LeyouSearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class ElasticsearchTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Test
    public void createIndex(){
        // 创建索引库，以及映射
        this.template.createIndex(Goods.class);
        this.template.putMapping(Goods.class);
    }

    @Test
    public void creatIndex(){
        // 创建索引库，以及映射
        this.template.createIndex(Goods.class);
        this.template.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;

        do{

            //分页查询spu。获取分页结果集
            PageResult<SpuBo> result = this.goodsClient.querySpuBoByPage(null, null, page, rows);
            // 获取当前页的数据
            List<SpuBo> items = result.getItems();
            // 处理结果为List<Goods>
            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods((Spu)spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            // 执行新增数据的方法
            this.goodsRepository.saveAll(goodsList);
            // 获取当前页的数据条数，如果是最后一页，没有100条
            rows = result.getItems().size();
            page++;
        }while (rows ==100);

    }
}