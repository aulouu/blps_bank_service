package itmo.blps.bank_service.config;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.postgresql.xa.PGXADataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class TransactionConfiguration {
    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(environment.getProperty("spring.jta.atomikos.datasource.orders-xa-ds.xa-properties.url"));
        pgxaDataSource.setUser(environment.getProperty("spring.jta.atomikos.datasource.orders-xa-ds.xa-properties.user"));
        pgxaDataSource.setPassword(environment.getProperty("spring.jta.atomikos.datasource.orders-xa-ds.xa-properties.password"));

        AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
        atomikosDataSource.setXaDataSource(pgxaDataSource);
        atomikosDataSource.setUniqueResourceName("bankXADS");
        atomikosDataSource.setMinPoolSize(5);
        atomikosDataSource.setMaxPoolSize(20);
        return atomikosDataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(false);
        adapter.setDatabase(Database.POSTGRESQL);
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJtaDataSource(dataSource());
        em.setJpaVendorAdapter(jpaVendorAdapter);
        em.setPackagesToScan("itmo.blps.bank_service.model");

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public UserTransactionManager userTransactionManager() {
        System.setProperty("com.atomikos.icatch.log_base_dir", "./atomikos-logs/bank-service");
        System.setProperty("com.atomikos.icatch.log_base_name", "tmlog-bank");
        System.setProperty("com.atomikos.icatch.tm_unique_name", "bankTransactionManager");

        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public JtaTransactionManager transactionManager() {
        JtaTransactionManager transactionManager = new JtaTransactionManager();
        transactionManager.setTransactionManager(userTransactionManager());
        transactionManager.setUserTransaction(userTransactionManager());
        return transactionManager;
    }
}
