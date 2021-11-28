package com.example.minimarket.security;

import com.example.minimarket.services.impl.MarketUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MarketUserService marketUserService;
    private final PasswordEncoder passwordEncoder;

    public ApplicationSecurityConfig(MarketUserService marketUserService, PasswordEncoder passwordEncoder) {
        this.marketUserService = marketUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
         httpSecurity.authorizeRequests()
                 .antMatchers("/js/**", "/css/**", "/img/**").permitAll()
                 .antMatchers("/", "/users/login", "/users/register", "/categories/all", "/products/all",
                         "/couriers/all", "/brands/all", "/products/details/{name}",
                         "/brands/allByBrand/{name}", "/categories/allByCategory/{name}", "/products/api",
                         "/products/promotion", "/contacts").permitAll()
                 .antMatchers("/products/add", "/categories/add", "/brands/add", "/couriers/add"
                         , "/products/addQuantity").hasRole("ADMIN")
                 .antMatchers("/**")
                 .authenticated()
                 .and()
                 .formLogin()
                 .loginPage("/users/login")
                 .usernameParameter("username")
                 .passwordParameter("password")
                 .defaultSuccessUrl("/home")
                 .failureForwardUrl("/users/login-error")
                 .successForwardUrl("/users/login")
                 .and()
                 .logout()
                 .logoutUrl("/logout")
                 .logoutSuccessUrl("/")
                 .invalidateHttpSession(true)
                 .deleteCookies("JSESSIONID");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.marketUserService)
                .passwordEncoder(this.passwordEncoder);
    }

}
