package de.kimrudolph.routing;

import de.kimrudolph.routing.entities.Customer;
import de.kimrudolph.routing.repositories.CustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RoutingTestConfiguration.class)
public class RoutingApplicationTests {

    @Autowired
    CustomerRepository customerRepository;

//    @Autowired
//    RoutingTestUtil routingTestUtil;

    @Test
    public void contextSwitchTest() throws Exception {

        // Create databases for each environment
//        for (DatabaseEnvironment databaseEnvironment : DatabaseEnvironment
//            .values()) {
//            routingTestUtil.createDatabase(databaseEnvironment);
//        }

        // Create a customer in each environment



        for (DatabaseEnvironment databaseEnvironment : DatabaseEnvironment
            .values()) {
            if(databaseEnvironment.equals(DatabaseEnvironment.D1)){
                continue;
            }
            DatabaseContextThreadLocal.set(databaseEnvironment);
            Customer devCustomer = new Customer();
            devCustomer.setName("Tony Tester");
            customerRepository.save(devCustomer);
            DatabaseContextThreadLocal.clear();
        }

        DataSource dataSource = DataSourceBuilder.create(DataSourceConfiguration.class.getClassLoader()).driverClassName("com.mysql.jdbc.Driver").password("123456").username("root").url("jdbc:mysql://127.0.0.1:3306/suda-auth?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT").build();
        HashMap<Object, Object> dataSourceMap = DataSourceConfiguration.getDataSourceMap();
        System.out.println(dataSourceMap.size());
        dataSourceMap.put(DatabaseEnvironment.D1,dataSource);
        DataSourceConfiguration.getRouter().setTargetDataSources(dataSourceMap);
        DataSourceConfiguration.getRouter().afterPropertiesSet();
        DatabaseContextThreadLocal.set(DatabaseEnvironment.D1);
        Customer devCustomer = new Customer();
        devCustomer.setName("Tony Tester");
        customerRepository.save(devCustomer);
        DatabaseContextThreadLocal.clear();
    }

}
