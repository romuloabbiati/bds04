package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	
	private static final String[] CLIENT_ADMIN = { "/cities/**", "/events/**" };
	
	private static final String[] CLIENT_POST = { "/events/**" };
	
	private static final String[] ADMIN_POST = { "/cities/**" };
	
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private Environment environment;
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		// H2 config
				if(Arrays.asList(environment.getActiveProfiles()).contains("test")) {
					http.headers().frameOptions().disable();
				}
		
		http.authorizeRequests()
			.antMatchers(PUBLIC).permitAll()
			.antMatchers(HttpMethod.GET, CLIENT_ADMIN).permitAll()
			.antMatchers(HttpMethod.POST,CLIENT_POST).hasAnyRole("CLIENT", "ADMIN")
			.antMatchers(HttpMethod.POST, ADMIN_POST).hasRole("ADMIN")
			.anyRequest().hasAnyRole("ADMIN");
	}

}
