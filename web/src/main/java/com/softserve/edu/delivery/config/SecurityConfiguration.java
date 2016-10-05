package com.softserve.edu.delivery.config;

import com.softserve.edu.delivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

    @Autowired
    @Qualifier("userService")
    private UserService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("customer@delivery.com").password("customer").roles("Customer");
        auth.inMemoryAuthentication().withUser("driver@delivery.com").password("driver").roles("Driver");
        auth.inMemoryAuthentication().withUser("admin@delivery.com").password("admin").roles("Admin");
        auth.inMemoryAuthentication().withUser("moderator@delivery.com").password("moderator").roles("Moderator");

        auth.userDetailsService(this.userDetailsService);
        auth.authenticationProvider(authProvider());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home", "/welcome").permitAll()
                .antMatchers("/login*").anonymous()
                .antMatchers("/registration*").anonymous()
                .antMatchers("/admin", "/admin/**").hasRole("Admin")
                .antMatchers("/moderator", "/moderator/**").hasRole("Moderator")
                .and().formLogin().loginPage("/login")
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/welcome")
                .failureUrl("/login?error=true")
                .and().logout().logoutSuccessUrl("/welcome")
                .invalidateHttpSession(true)
                .and().exceptionHandling().accessDeniedPage("/accessDenied");

    }
}