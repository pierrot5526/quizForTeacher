package com.cnam.quiz.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.cnam.quiz.common.enums.AccountType;
import com.cnam.security.RestAccessDeniedHandler;
import com.cnam.security.RestAuthenticationFailureHandler;
import com.cnam.security.RestAuthenticationSuccessHandler;
import com.cnam.security.RestUnauthorizedEntryPoint;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "com.cnam.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String REMEMBER_ME_KEY = "rememberme_key";
	 	
	 @Autowired
     private UserDetailsService userDetailsService;
	
	 @Autowired
	 private RestUnauthorizedEntryPoint restAuthenticationEntryPoint;
	 
	 @Autowired
	 private RestAuthenticationSuccessHandler  restAuthenticationSuccessHandler;

	 @Autowired
	 RestAuthenticationFailureHandler restAuthenticationFailureHandler;
	 
	 @Autowired
	 RestAccessDeniedHandler  restAccessDeniedHandler;
	 
	 public SecurityConfig() {
	        super();
	        
	    }
	  
	 @Autowired
	 public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		 auth.userDetailsService(userDetailsService);
	 }
	 
	 @Override
	 public void configure(WebSecurity web) throws Exception {
	        web.ignoring().antMatchers( "/index.html", "/app/**","/app/view/forms/**","/css/**",
	                "/fonts/**", "/js/**","/forms/**");
	 }
	 
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
	  http
	   	.headers().disable()
	   	.csrf().disable()
	   	.authorizeRequests()
	    .antMatchers("/user/**").hasAnyAuthority(AccountType.AUDITOR.name(),AccountType.PROFESSOR.name()) 
	    .antMatchers("/professor/**").hasAnyAuthority(AccountType.AUDITOR.name(),AccountType.PROFESSOR.name())
	    .antMatchers("/topic/**").hasAnyAuthority(AccountType.AUDITOR.name(),AccountType.PROFESSOR.name())
	    .antMatchers("/sequence/**").hasAnyAuthority(AccountType.AUDITOR.name(),AccountType.PROFESSOR.name())
	    .antMatchers("/authenticate").permitAll()	
	    .antMatchers("/unsecured/**").permitAll()	
	    .anyRequest().authenticated()
	    .and()
	    .exceptionHandling()
	    .authenticationEntryPoint(restAuthenticationEntryPoint)
	    .accessDeniedHandler(restAccessDeniedHandler)
	    .and()
	    .formLogin()
	    .loginProcessingUrl("/authenticate")
	    .successHandler(restAuthenticationSuccessHandler)
	    .failureHandler(restAuthenticationFailureHandler)
	    .usernameParameter("username")
	    .passwordParameter("password")
	    .permitAll()
	    .and()
	    .logout()
	    .logoutUrl("/logout")
	    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
	    .deleteCookies("JSESSIONID")
	    .permitAll()
	    .and();
	 }

	}
