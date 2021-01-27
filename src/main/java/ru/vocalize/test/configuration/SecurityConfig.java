package ru.vocalize.test.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final Filter filter;

  public SecurityConfig(Filter filter) {
    this.filter = filter;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic()
        .disable()
        .formLogin()
        .disable()
        .csrf()
        .disable()
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/status", "/in", "/out", "/report","/files")
        .authenticated()
        .anyRequest()
        .permitAll();
  }
}
