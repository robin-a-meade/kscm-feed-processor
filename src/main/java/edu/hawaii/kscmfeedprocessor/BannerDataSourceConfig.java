package edu.hawaii.kscmfeedprocessor;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BannerDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.banner")
    public DataSourceProperties bannerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.banner")
    public DataSource bannerDataSource() {
        return bannerDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate bannerJdbcTemplate() {
        return new JdbcTemplate(bannerDataSource());
    }

}
