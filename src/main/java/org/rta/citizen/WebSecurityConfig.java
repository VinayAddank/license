package org.rta.citizen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CitizenAuthEntry unauthorizedHandler;

	@Autowired
	private UserDetailsService citizenDetailsService;

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.citizenDetailsService);
	}

	@Bean
	public CitizenAuthTokenFilter authenticationTokenFilterBean() throws Exception {
		CitizenAuthTokenFilter authenticationTokenFilter = new CitizenAuthTokenFilter();
		authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationTokenFilter;
	}
	
	@Override  
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// Each request should be authenticated except /login api
				.authorizeRequests().antMatchers("/*/login/**").permitAll().antMatchers("/COVDetails").permitAll()
				.antMatchers("/covDetailsList").permitAll()
				.antMatchers("/ageGroup").permitAll().antMatchers("/getCOV").permitAll().antMatchers("/getFreshLLRAadhaarDetails").permitAll()
				.antMatchers("/application/services").permitAll()
				.antMatchers("{servicetype}/covDetails").permitAll().antMatchers("/citizenDetails").permitAll().
				antMatchers("/application/search/data/app/**").permitAll() 
				.antMatchers("/ots/approve/buyer/app/**").permitAll()
				.antMatchers("/permit/authorizationcard/**").permitAll()
				.antMatchers("/permit/certificate/app/**").permitAll()
				.antMatchers("{servicetype}/dlFreshCov").permitAll()
				.antMatchers("/hpa/approve/financier/app/**").permitAll()
				.antMatchers("/application/status/app/**").permitAll()
				.antMatchers("{servicetype}/getDLDetails").permitAll().
				antMatchers("{servicetype}/llRetestDetails").permitAll().
				antMatchers("/gettestdetails/{applicationNo}").permitAll().
				antMatchers("{servicetype}/saveCovDetails").permitAll().
				antMatchers("/customer/invoice/**").permitAll().
				antMatchers("/rta/signature/**").permitAll().
				antMatchers("/getAge").permitAll().
				antMatchers("/exam").permitAll().
				antMatchers("/exam/result").permitAll().
				antMatchers("/covDetails").permitAll().
				antMatchers("/invoice/**").permitAll().
				antMatchers("/receipt/**").permitAll().
				antMatchers("/getVehicleClassDetails").permitAll().
				antMatchers("/application/ottoken/**").permitAll().
				antMatchers("/getslotbookingdetails/{applicationNumner}").permitAll().
				antMatchers("/getApprovalVehicleclass/{applicationNo}").permitAll().
				antMatchers("{servicetype}/lleCovDetails").permitAll().antMatchers("{servicetype}/dlCovDetails").permitAll()
				.antMatchers("/getEndosmentDetails").permitAll()
				.antMatchers("/application/reiterate/app/**").permitAll()
				.antMatchers("/fitness/certificate/app/**").permitAll()
				.antMatchers("/iib_insurancedetails/{regNo}/**").permitAll()
				.antMatchers("/forgot/**").permitAll()
				.antMatchers("/license/details/{applicationno}").permitAll()
				.antMatchers("/status/attachment/doctype").permitAll()
				.antMatchers("/communication/test/{applicationNumber}").permitAll()//testing
				.antMatchers("/application/authentication/{appNumber}").permitAll()
				.anyRequest().fullyAuthenticated();
		
		// Custom JWT based security filter
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
	}
}
