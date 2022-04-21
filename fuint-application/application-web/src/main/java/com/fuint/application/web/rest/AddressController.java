package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtAddress;
import com.fuint.application.dao.entities.MtRegion;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.repositories.MtRegionRepository;
import com.fuint.application.dto.AddressDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.address.AddressService;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/address")
public class AddressController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 收货地址服务接口
     * */
    @Autowired
    private AddressService addressService;

    @Autowired
    private MtRegionRepository regionRepository;

    /**
     * 保存收货地址
     */
    @RequestMapping(value = "/save")
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String name = param.get("name") == null ? "" : param.get("name").toString();
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        Integer provinceId = param.get("provinceId") == null ? 0 : Integer.parseInt(param.get("provinceId").toString());
        Integer cityId = param.get("cityId") == null ? 0 : Integer.parseInt(param.get("cityId").toString());
        Integer regionId = param.get("regionId") == null ? 0 : Integer.parseInt(param.get("regionId").toString());
        String detail = param.get("detail") == null ? "" : param.get("detail").toString();
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String isDefault = param.get("isDefault") == null ? "" : param.get("isDefault").toString();
        Integer addressId = param.get("addressId") == null ? 0 : Integer.parseInt(param.get("addressId").toString());

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtAddress mtAddress = new MtAddress();

        mtAddress.setId(addressId);
        mtAddress.setName(name);
        mtAddress.setMobile(mobile);
        mtAddress.setProvinceId(provinceId);
        mtAddress.setCityId(cityId);
        mtAddress.setRegionId(regionId);
        mtAddress.setDetail(detail);
        mtAddress.setStatus(status);
        mtAddress.setUserId(mtUser.getId());
        mtAddress.setIsDefault(isDefault);

        addressService.saveAddress(mtAddress);

        return getSuccessResult(true);
    }

    /**
     * 获取收货地址列表
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        } else {
            param.put("userId", mtUser.getId().toString());
        }

        param.put("status", StatusEnum.ENABLED.getKey());

        List<MtAddress> addressList = addressService.queryListByParams(param);

        List<AddressDto> dataList = new ArrayList<>();
        for (MtAddress mtAddress : addressList) {
            AddressDto dto = new AddressDto();
            BeanUtils.copyProperties(dto, mtAddress);

            String province = "";
            String city = "";
            String region = "";

            if (dto.getProvinceId() > 0) {
                MtRegion mtProvince = regionRepository.findOne(dto.getProvinceId());
                if (mtProvince != null) {
                    province = mtProvince.getName();
                }
            }
            if (dto.getCityId() > 0) {
                MtRegion mtCity = regionRepository.findOne(dto.getCityId());
                if (mtCity != null) {
                    city = mtCity.getName();
                }
            }
            if (dto.getCityId() > 0) {
                MtRegion mtRegion = regionRepository.findOne(dto.getRegionId());
                if (mtRegion != null) {
                    region = mtRegion.getName();
                }
            }

            dto.setProvinceName(province);
            dto.setCityName(city);
            dto.setRegionName(region);

            dataList.add(dto);
        }

        result.put("list", dataList);

        return getSuccessResult(result);
    }

    /**
     * 获取收货地址详情
     */
    @RequestMapping(value = "/detail")
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        Integer addressId = request.getParameter("addressId") == null ? 0 : Integer.parseInt(request.getParameter("addressId"));

        String token = request.getHeader("Access-Token");

        Map<String, Object> result = new HashMap<>();

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser || StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtAddress mtAddress = null;
        if (addressId > 0) {
            mtAddress = addressService.detail(addressId);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", mtUser.getId().toString());
            params.put("isDefault", "Y");
            List<MtAddress> addressList = addressService.queryListByParams(params);
            if (addressList.size() > 0) {
                mtAddress = addressList.get(0);
            }
        }

        if (mtAddress != null) {
            if (mtAddress.getUserId().equals(mtUser.getId())) {
                AddressDto dto = new AddressDto();

                BeanUtils.copyProperties(dto, mtAddress);

                String province = "";
                String city = "";
                String region = "";

                if (dto.getProvinceId() > 0) {
                    MtRegion mtProvince = regionRepository.findOne(dto.getProvinceId());
                    if (mtProvince != null) {
                        province = mtProvince.getName();
                    }
                }
                if (dto.getCityId() > 0) {
                    MtRegion mtCity = regionRepository.findOne(dto.getCityId());
                    if (mtCity != null) {
                        city = mtCity.getName();
                    }
                }
                if (dto.getRegionId() > 0) {
                    MtRegion mtRegion = regionRepository.findOne(dto.getRegionId());
                    if (mtRegion != null) {
                        region = mtRegion.getName();
                    }
                }

                dto.setProvinceName(province);
                dto.setCityName(city);
                dto.setRegionName(region);

                result.put("address", dto);
            }
        }

        return getSuccessResult(result);
    }
}
