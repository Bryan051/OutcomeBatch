package com.sparta.outcomebatch.batch.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sparta.outcomebatch.batch.domain.write", // 레포지토리가 위치한 패키지
        entityManagerFactoryRef = "batchEntityManagerFactory",
        transactionManagerRef = "batchTransactionManager"
)
//@EntityScan(
//        basePackages = {"com.sparta.outcomebatch.batch.domain"}
//)
public class BatchDataSourceConfig {

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory(
            @Qualifier("batchDataSource") DataSource batchDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(batchDataSource);
        em.setPackagesToScan("com.sparta.outcomebatch.batch.domain");// 엔티티가 위치한 패키지
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");// 절대 update 를 하지마
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager batchTransactionManager(
            @Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory) {
        return new JpaTransactionManager(batchEntityManagerFactory);
    }
}