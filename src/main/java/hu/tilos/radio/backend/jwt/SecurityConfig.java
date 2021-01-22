package hu.tilos.radio.backend.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    OidcAuthoritiesMapper oidcAuthoritiesMapper;
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("dex");
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}");
        builder.scope("openid", "email", "profile");
        builder.clientId("tilos");
        builder.userNameAttributeName("email");
        builder.jwkSetUri("http://127.0.0.1:5556/dex/keys");
        builder.clientSecret("ZXhhbXBsZS1hcHAtc2VjcmV0");
        builder.authorizationUri("http://127.0.0.1:5556/dex/auth");
        builder.tokenUri("http://127.0.0.1:5556/dex/token");
        builder.clientName("Tilos");
        return builder.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }


    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/auth/password_reset").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/show/*/contact").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/api/**").authenticated()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/showFeed/**").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll().and()
                .oauth2Login()
                .userInfoEndpoint().userAuthoritiesMapper(oidcAuthoritiesMapper)
                .and()
                .and()
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));
        http.oauth2Login();
        http.addFilterAfter(new JwtFilter(authenticationManagerBean()), AnonymousAuthenticationFilter.class);

    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(
                        this.clientRegistrationRepository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(
                URI.create("http://localhost:8081/home"));

        return oidcLogoutSuccessHandler;
    }

}
