package com.fuint.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger接口文档
 *
 * springfox 3.0.0 基于 javax.servlet，Spring Boot 3 下不可用，
 * 已迁移到 springdoc-openapi（OpenAPI 3），接口文档地址不变：/swagger-ui.html
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
public class SwaggerConfig {

     @Bean
     public OpenAPI fuintOpenAPI() {
         return new OpenAPI()
                 .info(new Info()
                         .title("fuint会员营销系统接口文档")
                         .description("fuint会员营销系统接口文档，“/clientApi”目录接口为会员端相关接口，“/backendApi”目录接口为后台管理端相关接口。")
                         .termsOfService("https://www.fuint.cn/")
                         .contact(new Contact()
                                 .name("海南延禾信息技术有限公司")
                                 .url("https://www.fuint.cn/")
                                 .email("fushengqian@qq.com"))
                         .version("1.0"));
     }
}
