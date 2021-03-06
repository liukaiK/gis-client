package com.unicom.smartcity.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * @author liukai
 */
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .logout().logoutSuccessUrl("http://127.0.0.1:8080/cas/logout")
                .and()
                .oauth2Login()
                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
//                .and()
//                .logout()
//                .logoutSuccessUrl("{baseUrl}/{action}/oauth2/code/{registrationId}");
//                .loginPage("/login/oauth2");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/css/**", "/lib/**", "/images/**", "/favicon.ico");
    }

}
