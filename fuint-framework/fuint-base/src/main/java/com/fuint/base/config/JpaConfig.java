package com.fuint.base.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.fuint.base.dao.BaseRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * spring data jpa 配置
 * <p/>
 * Created by FSQ
 * Contact wx fsq_better
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.*.*.dao.repositories", repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class, entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
public class JpaConfig {

    private static final Logger logger = LoggerFactory.getLogger(JpaConfig.class);

    @Autowired
    private Environment env;


    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
        vendorAdapter.setShowSql(Boolean.TRUE);
        factory.setDataSource(dataSource());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.*.*.dao.entities");
        factory.setPersistenceUnitName("defaultPersistenceUnit");
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("jpa.hibernate.hbm2ddl.auto"));
        jpaProperties.put("hibernate.dialect", env.getProperty("jpa.hibernate.dialect"));
        jpaProperties.put("hibernate.show_sql", env.getProperty("jpa.hibernate.show.sql"));
        jpaProperties.put("hibernate.format_sql", true);
        jpaProperties.put("hibernate.event.merge.entity_copy_observer", "allow");
        //不区分数据库表字段大小写
        jpaProperties.put("hibernate.naming_strategy", "hibernate.ejb.naming_strategy");
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet();
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return factory;
    }


    @Bean(name = "transactionManager")
    @Primary
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager manager = new JpaTransactionManager();
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = this.entityManagerFactory();
        manager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return manager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        try {
            dataSource.setInitialSize(Integer.parseInt(env.getProperty("jdbc.initial-size")));
            dataSource.setMinIdle(Integer.parseInt(env.getProperty("jdbc.min-idle")));
            dataSource.setMinIdle(Integer.parseInt(env.getProperty("jdbc.max-idle")));
            dataSource.setMaxActive(Integer.parseInt(env.getProperty("jdbc.max-active")));
            dataSource.setMaxWait(Integer.parseInt(env.getProperty("jdbc.maxWait")));
            dataSource.setTimeBetweenConnectErrorMillis(Integer.parseInt(env.getProperty("jdbc.timeBetweenEvictionRunsMillis")));
            dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(env.getProperty("jdbc.minEvictableIdleTimeMillis")));
            dataSource.setTestWhileIdle(Boolean.parseBoolean(env.getProperty("jdbc.testWhileIdle")));
            dataSource.setTestOnBorrow(Boolean.parseBoolean(env.getProperty("jdbc.testOnBorrow")));
            dataSource.setTestOnReturn(Boolean.parseBoolean(env.getProperty("jdbc.testOnReturn")));
        } catch (Exception e) {
            logger.error("=====Jpa datasource initialize error:{}", e);
            throw new RuntimeException(e);
        }
        return dataSource;
    }

}
