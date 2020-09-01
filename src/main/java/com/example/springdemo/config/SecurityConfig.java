package com.example.springdemo.config;

import com.example.springdemo.model.user.UserServiceImpl;
import com.example.springdemo.security.CustomPermissionEvaluator;
import com.example.springdemo.security.JwtAuthenticationEntryPoint;
import com.example.springdemo.security.JwtAuthenticationFilter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    protected void configureGlobal (AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    CustomPermissionEvaluator customPermissionEvaluator(){
        return new CustomPermissionEvaluator();
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator());
        return expressionHandler;
    }

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtAuthenticationFilter authFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Configuration
    public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

        @Autowired
        private JwtAuthenticationEntryPoint unauthorizedHandler;

        @Bean(BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and().csrf().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                    .sessionManagement()
//                    .maximumSessions(1)
//                    .and()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    //.antMatchers(HttpMethod.GET, "/api/students").permitAll()
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/subjects").permitAll()
                    .antMatchers("/api/uploadFile").permitAll()
                    .antMatchers("/api/pictures/{fileName:.+}").permitAll()
                    .anyRequest()
                    .authenticated();

            http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        }

    }
//    @Configuration
//    public class DatabaseConfigurer {
//
//        @Value("${spring.datasource.url}")
//        private String dbUrl;
//
//        @Value("${spring.datasource.username}")
//        private String username;
//        @Value("${spring.datasource.password}")
//        private String password;
//
//        @Bean
//        public DataSource dataSource() {
//            HikariConfig config = new HikariConfig();
//            config.setJdbcUrl(dbUrl);
//            config.setUsername(username);
//            config.setPassword(password);
//            config.set
//            return new HikariDataSource(config);
//        }
//    }
}
