package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserGradeCatchTypeEnum;
import com.fuint.common.service.UserGradeService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtUserGradeMapper;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGrade;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员等级业务接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class UserGradeServiceImpl extends ServiceImpl<MtUserGradeMapper, MtUserGrade> implements UserGradeService {

    @Resource
    private MtUserGradeMapper mtUserGradeMapper;

    /**
     * 分页查询会员等级列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserGrade> queryUserGradeListByPagination(PaginationRequest paginationRequest) {
        Page<MtUserGrade> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtUserGrade> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserGrade::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtUserGrade::getName, name);
        }
        String catchType = paginationRequest.getSearchParams().get("catchType") == null ? "" : paginationRequest.getSearchParams().get("catchType").toString();
        if (StringUtils.isNotBlank(catchType)) {
            lambdaQueryWrapper.like(MtUserGrade::getCatchType, catchType);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtUserGrade::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtUserGrade::getId);
        List<MtUserGrade> dataList = mtUserGradeMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtUserGrade> paginationResponse = new PaginationResponse(pageImpl, MtBanner.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加会员等级信息
     *
     * @param mtUserGrade
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增会员等级")
    public MtUserGrade addUserGrade(MtUserGrade mtUserGrade) {
        mtUserGradeMapper.insert(mtUserGrade);
        return mtUserGrade;
    }

    /**
     * 根据ID获取会员等级信息
     *
     * @param id 会员等级ID
     * @return
     */
    @Override
    public MtUserGrade queryUserGradeById(Integer id) {
        return mtUserGradeMapper.selectById(id);
    }

    /**
     * 修改会员等级
     *
     * @param mtUserGrade
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改会员等级")
    public MtUserGrade updateUserGrade(MtUserGrade mtUserGrade) {
        MtUserGrade userGrade = mtUserGradeMapper.selectById(mtUserGrade.getId());
        if (null != userGrade) {
            mtUserGradeMapper.updateById(mtUserGrade);
        }
        return mtUserGrade;
    }

    /**
     * 根据ID删除会员等级
     *
     * @param id       ID
     * @param operator 操作人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除会员等级")
    public Integer deleteUserGrade(Integer id, String operator) {
        MtUserGrade mtUserGrade = this.queryUserGradeById(id);
        if (null == mtUserGrade) {
            return 0;
        }
        mtUserGrade.setStatus(StatusEnum.DISABLE.getKey());
        mtUserGradeMapper.updateById(mtUserGrade);
        return mtUserGrade.getId();
    }

    /**
     * 获取默认的会员等级
     */
    @Override
    public MtUserGrade getInitUserGrade() {
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        param.put("CATCH_TYPE", UserGradeCatchTypeEnum.INIT.getKey());
        List<MtUserGrade> dataList = mtUserGradeMapper.selectByMap(param);
        MtUserGrade initGrade = new MtUserGrade();
        initGrade.setId(0);
        if (dataList != null && dataList.size() > 0) {
            initGrade = dataList.get(0);
        }
        return initGrade;
    }

    /**
     * 获取付费会员等级列表
     * @param userInfo
     * */
    @Override
    public List<MtUserGrade> getPayUserGradeList(MtUser userInfo) {
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        param.put("catch_type", UserGradeCatchTypeEnum.PAY.getKey());

        List<MtUserGrade> userGrades = mtUserGradeMapper.selectByMap(param);

        List<MtUserGrade> dataList = new ArrayList<>();
        if (userGrades.size() > 0 && userInfo != null) {
            for (MtUserGrade grade : userGrades) {
                if (!userInfo.getGradeId().equals(grade.getId().toString()) && (grade.getGrade() > Integer.parseInt(userInfo.getGradeId()))) {
                    dataList.add(grade);
                }
            }
        }

        return dataList;
    }
}
