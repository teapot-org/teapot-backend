package org.teapot.backend.test.dao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan("org.teapot.backend")
@PropertySource("classpath:test-database.properties")
public class TestDatabaseConfig {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    private static String[] hibernateProperties = new String[] {
            "hibernate.hbm2ddl.auto",
            "hibernate.format_sql"
    };

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getRequiredProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username", USERNAME));
        dataSource.setPassword(env.getProperty("spring.datasource.password", PASSWORD));

        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource);

        try {
            entityManagerFactory.setJpaVendorAdapter(
                    (JpaVendorAdapter) Class.forName(
                            env.getRequiredProperty("entity-manager-factory.jpa-vendor-adapter")
                    ).newInstance());

            entityManagerFactory.setJpaDialect(
                    (JpaDialect) Class.forName(
                            env.getRequiredProperty("entity-manager-factory.jpa-dialect")
                    ).newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        entityManagerFactory.setPackagesToScan(env.getRequiredProperty(
                "entity-manager-factory.packages-to-scan"));

        entityManagerFactory.setJpaPropertyMap(getHibernateJpaProperties());

        return entityManagerFactory;
    }

    @Bean("transactionManager")
    public JpaTransactionManager getTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();

        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager;
    }

    private Map<String, ?> getHibernateJpaProperties() {
        HashMap<String, String> properties = new HashMap<>();

        for (String hibernateProperty : hibernateProperties) {
            properties.put(hibernateProperty,
                    env.getRequiredProperty(hibernateProperty));
        }

        return properties;
    }
}
