package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtRegion;
import com.fuint.application.dao.repositories.MtRegionRepository;
import com.fuint.application.dto.RegionDto;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省/市/区controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/region")
public class RegionController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private MtRegionRepository regionRepository;

    /**
     * 获取树状结构列表
     */
    @RequestMapping(value = "/tree")
    @CrossOrigin
    public ResponseObject tree(HttpServletRequest request) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        Specification<MtRegion> specification = regionRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<MtRegion> regionList = regionRepository.findAll(specification, sort);

        List<RegionDto> treeData = new ArrayList<>();

        for (MtRegion mtRegion : regionList) {
             if (mtRegion.getLevel().equals("1")) {
                 RegionDto dto = new RegionDto();
                 dto.setId(mtRegion.getId());
                 dto.setName(mtRegion.getName());
                 dto.setCode(mtRegion.getCode());
                 dto.setPid(mtRegion.getPid());
                 dto.setLevel(mtRegion.getLevel());
                 dto.setCity(new ArrayList<>());
                 treeData.add(dto);
             }
        }

        for (int i = 0; i < treeData.size(); i++) {
            List<RegionDto> cityArr = new ArrayList<>();
            for (MtRegion mtRegion : regionList) {
                if (treeData.get(i).getId().equals(mtRegion.getPid())) {
                    RegionDto dto = new RegionDto();
                    dto.setId(mtRegion.getId());
                    dto.setName(mtRegion.getName());
                    dto.setCode(mtRegion.getCode());
                    dto.setPid(mtRegion.getPid());
                    dto.setLevel(mtRegion.getLevel());
                    List<RegionDto> regionArr = new ArrayList<>();
                    for (MtRegion mtRegion1 : regionList) {
                        if (mtRegion.getId().equals(mtRegion1.getPid())) {
                            RegionDto dto1 = new RegionDto();
                            dto1.setId(mtRegion1.getId());
                            dto1.setName(mtRegion1.getName());
                            dto1.setCode(mtRegion1.getCode());
                            dto1.setPid(mtRegion1.getPid());
                            dto1.setLevel(mtRegion1.getLevel());
                            regionArr.add(dto1);
                        }
                    }
                    dto.setRegion(regionArr);
                    cityArr.add(dto);
                }
            }

            treeData.get(i).setCity(cityArr);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", treeData);

        return getSuccessResult(result);
    }
}
