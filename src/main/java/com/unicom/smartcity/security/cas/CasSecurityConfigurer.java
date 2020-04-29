package com.unicom.smartcity.security.cas;

import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class CasSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    @Autowired
    private AuthenticationDetailsSource authenticationDetailsSource;

    @Override
    public void configure(HttpSecurity http) {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        casAuthenticationFilter.setAuthenticationDetailsSource(authenticationDetailsSource);

        http.addFilter(casAuthenticationFilter);

    }


    @Bean
    public AuthenticationDetailsSource authenticationDetailsSource(ServiceProperties serviceProperties) {
        return new ServiceAuthenticationDetailsSource(serviceProperties);
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
        //请求客户端  会跳转到CAS 去登录
        casAuthenticationEntryPoint.setLoginUrl("http://127.0.0.1:8080/cas/login");
        return casAuthenticationEntryPoint;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("http://127.0.0.1:8081/login/cas");
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(AuthenticationUserDetailsService authenticationUserDetailsService, TicketValidator ticketValidator) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService);
        casAuthenticationProvider.setTicketValidator(ticketValidator);
        casAuthenticationProvider.setKey("1234567");
        return casAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService authenticationUserDetailsService(UserDetailsService userDetailsService) {
        UserDetailsByNameServiceWrapper authenticationUserDetailsService = new UserDetailsByNameServiceWrapper();
        authenticationUserDetailsService.setUserDetailsService(userDetailsService);
        return authenticationUserDetailsService;
    }


    @Bean
    public TicketValidator ticketValidator() {
        TicketValidator ticketValidator = new Cas30ServiceTicketValidator("http://127.0.0.1:8080/cas");
        return ticketValidator;
    }


    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();

        userDetailsService.createUser(User.builder().username("admin").password("123456").roles("USER").build());
        return userDetailsService;
    }

}
