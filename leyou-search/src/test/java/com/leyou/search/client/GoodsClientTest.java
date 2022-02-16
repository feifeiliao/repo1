package com.leyou.search.client;

import com.leyou.LeyouSearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class GoodsClientTest extends TestCase {
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void testQuerySpuBoByPage(){
        PageResult<SpuBo> pageResult = this.goodsClient.querySpuBoByPage(null, null, 1, 100);
        System.out.println(pageResult);
    }

}