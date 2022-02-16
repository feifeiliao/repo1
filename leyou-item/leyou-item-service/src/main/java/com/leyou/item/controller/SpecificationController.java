package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据id查询分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据gid查询参数详情
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required = false)Boolean searching
    ){
        List<SpecParam> params = this.specificationService.queryParamsByGid(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 保存分组信息
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> saveGroups(@RequestBody SpecGroup group){
        this.specificationService.saveGroups(group);
        System.out.println(group);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    /**
     * 保存参数规格信息
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> saveParam(@RequestBody SpecParam param){
        this.specificationService.saveParam(param);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 根据id获取组及规格参数
     * @param id
     * @return
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long id){
        List<SpecGroup> groups = this.specificationService.queryGroupsWithParam(id);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}
