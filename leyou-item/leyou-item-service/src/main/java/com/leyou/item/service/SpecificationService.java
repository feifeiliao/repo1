package com.leyou.item.service;

import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecgGroupMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecgGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    /**
     * 根据id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.groupMapper.select(specGroup);
    }

    /**
     * 根据gid查询参数详情
     *
     * @param cid
     * @param gid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParamsByGid(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.paramMapper.select(specParam);
    }

    /**
     * 保存分组信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGroups(SpecGroup group) {
        this.groupMapper.updateByPrimaryKeySelective(group);
    }

    /**
     * 保存参数规格信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveParam(SpecParam param) {
        this.paramMapper.updateByPrimaryKeySelective(param);
    }

    public List<SpecGroup> queryGroupsWithParam(Long id) {
        // 首先获取组
        List<SpecGroup> groups = this.queryGroupsByCid(id);
        // 遍历组获取规格参数
        groups.forEach(group->{
            List<SpecParam> params = this.queryParamsByGid(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
