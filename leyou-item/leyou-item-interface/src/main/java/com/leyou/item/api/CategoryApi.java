package com.leyou.item.api;


import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    @GetMapping("names")
    public List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);

    @GetMapping("all/level")
    public List<Category> queryAllByCid3(@RequestParam("id")Long id);
}
