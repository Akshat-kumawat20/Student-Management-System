package com.example.myproject.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login", "/css/**").permitAll().anyRequest().authenticated()).oauth2Login(oauth -> oauth.authorizationEndpoint(endpoint -> endpoint.authorizationRequestResolver(authorizationRequestResolver(null))).defaultSuccessUrl("/students", true)).logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll());
		return http.build();
	
}
	@Bean
	public OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository repo) {
		DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
		resolver.setAuthorizationRequestCustomizer(customizer -> customizer.additionalParameters(params -> params.put("prompt", "select_account")));
		return resolver;
}
	
}
// .invalidateHttpSession(true) -> Destroys session 
// .clearAuthentication(true) -> removes authorization details
// .deleteCookies("JSESSIONID") -> deletes login cookie