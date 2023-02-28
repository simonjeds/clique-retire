package com.clique.retire.repository.cosmos.configuration;

import java.util.HashMap;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author everton.gomes
 * Classe responsável por carregar as configurações de dataSource do CosmosDB
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = "com.clique.retire.repository.cosmos", 
	entityManagerFactoryRef = "cosmosEntityManager", 
	transactionManagerRef = "cosmosTransactionManager"
)
public class CosmosConfiguration {

	@Autowired
	private Environment env;
	
	@Bean("cosmosEntityManager")
    public LocalContainerEntityManagerFactoryBean cosmosEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cosmosDataSource());
        em.setPackagesToScan("com.clique.retire.model.cosmos");
 
        HibernateJpaVendorAdapter vendorAdapter  = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
 
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("jpa.hibernate.ddl.auto"));
        properties.put("hibernate.show_sql", env.getProperty("jpa.show.sql"));
        properties.put("hibernate.format_sql", env.getProperty("jpa.format.sql"));
        properties.put("hibernate.dialect", env.getProperty("jpa.dialect")); 
        properties.put("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", false);
        properties.put("spring.datasource.default-transaction-isolation-level", 1);
        		
        em.setJpaPropertyMap(properties);
        
        return em;
    }
	
	@Bean("cosmosDataSource")
    public DataSource cosmosDataSource() {
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
    	dataSource.setUrl(env.getProperty("ds.cosmos.url"));
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("sql.driverClassName")));
	    dataSource.setUsername(env.getProperty("sql.cosmos.username"));
	    dataSource.setPassword(env.getProperty("sql.cosmos.password"));
	    return dataSource;
    }
	
	@Bean("cosmosTransactionManager")
    public PlatformTransactionManager cosmosTransactionManager() {
       JpaTransactionManager transactionManager = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(cosmosEntityManager().getObject());
       return transactionManager;
    }
    
}
