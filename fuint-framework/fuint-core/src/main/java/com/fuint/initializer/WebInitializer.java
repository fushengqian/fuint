package com.fuint.initializer;

import com.fuint.config.ApplicationConfig;
import com.fuint.config.WebMvcConfig;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * application. web.xml
 * <p/>
 * Created by fsq on 19/11/29.
 */
@Order(2)
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final String CHARACTER_SET = "UTF-8";

    /**
     * 设置相关配置
     *
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ApplicationConfig.class};
    }

    /**
     * 设置相关配置
     *
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class};
    }

    /**
     * 设置mapping
     *
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * 设置filter
     *
     * @return
     */
    @Override
    protected Filter[] getServletFilters() {
        /**字符集filter**/
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter(CHARACTER_SET, true);
        return new Filter[]{characterEncodingFilter};
    }
}
