package com.unicom.smartcity.security.cas;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author liukai
 */
@Setter
@Getter
@Configuration
@PropertySource(value = "classpath:cas.properties")
public class CasProperties {

    @Value("${cas.server.prefix}")
    private String casServerPrefix;

    @Value("${cas.server.login}")
    private String casServerLogin;

    @Value("${cas.server.logout}")
    private String casServerLogout;

    @Value("${cas.client.prefix}")
    private String casClientPrefix;

    @Value("${cas.client.login}")
    private String casClientLogin;

    @Value("${cas.client.logout.relative}")
    private String casClientLogoutRelative;

    @Value("${cas.client.logout}")
    private String casClientLogout;

}
