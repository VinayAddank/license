/**
 * 
 */
package org.rta;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author arun.verma
 *
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@PropertySources({ @PropertySource("classpath:application.properties"),
		@PropertySource("classpath:/services/registration-services.properties") })
public class CitizenApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CitizenApplication.class, args);
	}

	@Bean(name = "sessionFactory")
	public SessionFactory sessionFactory(HibernateEntityManagerFactory factory) {
		return factory.getSessionFactory();
	}

	@Bean(name = "restTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SmsEmailService getSmsEmailService() {
		return new SmsEmailServiceImpl();
	}

}
