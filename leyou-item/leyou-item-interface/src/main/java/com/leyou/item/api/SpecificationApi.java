package com.leyou.item.api;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("spec")
public interface SpecificationApi {




    /**
     * 根据gid查询参数详情
     * @param gid
     * @return
     */
    @GetMapping("params")
    public List<SpecParam> queryParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required = false)Boolean searching
    );

    /**
     * 根据id获取组及规格参数
     * @param id
     * @return
     */
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long id);

}
