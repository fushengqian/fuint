package com.fuint.repository.mapper;

import com.fuint.repository.model.MtArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtArticleMapper extends BaseMapper<MtArticle> {
   void increaseClick(@Param("articleId") Integer articleId);
}
