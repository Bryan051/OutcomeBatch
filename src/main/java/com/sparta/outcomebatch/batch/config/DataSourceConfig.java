package com.sparta.outcomebatch.batch.config;//package com.sparta.outcome.batch.config;
//
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//
////    @Bean
////    @Primary
////    @ConfigurationProperties("spring.datasource.batch")
////    public DataSourceProperties batchDataSourceProperties() {
////        return new DataSourceProperties();
////    }
////
////    @Bean
////    @Primary
////    public DataSource batchDataSource() {
////        return batchDataSourceProperties().initializeDataSourceBuilder().build();
////    }
//
//    @Bean
//    @Primary
//    @ConfigurationProperties("spring.datasource")
//    public DataSourceProperties springgDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @Primary
//    public DataSource streamingDataSource() {
//        return springgDataSourceProperties().initializeDataSourceBuilder().build();
//    }
//}