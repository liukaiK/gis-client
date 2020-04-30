package com.unicom.smartcity.security.cas;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class CasSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    @Autowired
    private CasProperties casProperties;

    @Autowired
    private AuthenticationDetailsSource<HttpServletRequest, ServiceAuthenticationDetails> authenticationDetailsSource;

    @Override
    public void configure(HttpSecurity http) {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        casAuthenticationFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        http.addFilter(casAuthenticationFilter);

        http.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);

        http.addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class);

    }


    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        singleSignOutFilter.setCasServerUrlPrefix(casProperties.getCasServerPrefix());
        return singleSignOutFilter;
    }

    @Bean
    public LogoutFilter requestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getCasServerLogout(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(casProperties.getCasClientLogoutRelative());
        return logoutFilter;
    }

    @Bean
    public AuthenticationDetailsSource<HttpServletRequest, ServiceAuthenticationDetails> authenticationDetailsSource(ServiceProperties serviceProperties) {
        return new ServiceAuthenticationDetailsSource(serviceProperties);
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
        //请求客户端  会跳转到CAS 去登录
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLogin());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        //回调地址
        serviceProperties.setService(casProperties.getCasClientLogin());
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService, TicketValidator ticketValidator) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService);
        casAuthenticationProvider.setTicketValidator(ticketValidator);
        casAuthenticationProvider.setKey("1234567");
        return casAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService(UserDetailsService userDetailsService) {
        UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken> authenticationUserDetailsService = new UserDetailsByNameServiceWrapper<>();
        authenticationUserDetailsService.setUserDetailsService(userDetailsService);
        return authenticationUserDetailsService;
    }


    @Bean
    public TicketValidator ticketValidator() {
        return new Cas30ServiceTicketValidator(casProperties.getCasServerPrefix());
    }


    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();

        userDetailsService.createUser(User.builder().username("admin").password("123456").roles("USER").build());
        return userDetailsService;
    }

}
