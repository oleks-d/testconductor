package edu.testconductor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;
import javax.xml.ws.soap.Addressing;

@Configuration
@EnableWebSecurity

@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers( "/h2-console/**", "/register", "/registration", "/login", "/register_student", "/registrationInfo").permitAll()
//                              .antMatchers( "/", "/h2-console/**", "/register_for_test", "/start_test", "/test").permitAll()
//                .antMatchers("/**")
//                .hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/signin")

                .usernameParameter("username")  // Username parameter, used in name attribute
                // of the <input> tag. Default is 'username'.
                .passwordParameter("password")  // Password parameter, used in name attribute
                // of the <input> tag. Default is 'password'.
                .successHandler((req,res,auth)->{    //Success handler invoked after successful authentication
                    req.getSession().setAttribute("teacher", auth.getName());
                    req.getSession().setAttribute("exams", "");
                    req.getSession().setAttribute("warning", "");
                    req.getSession().setAttribute("message", "");
                    res.sendRedirect("/"); //"("/?search"); // Redirect user to index/home page
                })
//    .defaultSuccessUrl("/")   // URL, where user will go after authenticating successfully.
                // Skipped if successHandler() is used.
                .failureHandler((req,res,exp)->{  // Failure handler invoked after authentication failure
                    String errMsg="";
                    if(exp.getClass().isAssignableFrom(BadCredentialsException.class)){
                        errMsg="Невірний логін чи пароль. Invalid username or password.";
                    }else{
                        errMsg="Помилка / Error "+exp.getMessage();
                    }
                    req.getSession().setAttribute("warning", errMsg);
                    res.sendRedirect("/"); // Redirect user to login page with error message.
                })

                .permitAll();
    }

    //TODO finish experiment
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("u")
//                        .password("u")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers( "/resources/**",  "/static/**", "/css/**", "/js/**", "/img/**", "/h2-console/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                //.passwordEncoder(bCryptPasswordEncoder)
        .usersByUsernameQuery("select username, password, active from system_users where username = ?1 or email = ?1")
                .authoritiesByUsernameQuery("select u.username, ur.role_id from system_users u inner join user_role ur on u.id = ur.user_id where u.username=?");
//           .authoritiesByUsernameQuery("select u.username, ur.roles_id from system_users u inner join user_role ur on u.id = ur.user_id where u.username=?");
//        auth.
//                jdbcAuthentication()
//                .usersByUsernameQuery(usersQuery)
//                .authoritiesByUsernameQuery(rolesQuery)
//                .dataSource(dataSource)
//                .passwordEncoder(bCryptPasswordEncoder);
    }


    /*
    @Value("${spring.queries.users-query}")
private String usersQuery;
@Value("${spring.queries.roles-query}")
private String rolesQuery;
@Override
protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
    auth.
            jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .authoritiesByUsernameQuery(rolesQuery)
            .dataSource(dataSource)
            .passwordEncoder(bCryptPasswordEncoder);
}
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.
            authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/registration").permitAll()
            .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
            .authenticated().and().csrf().disable().formLogin()
            .loginPage("/login").failureUrl("/login?error=true")
            .defaultSuccessUrl("/admin/adminHome")
            .usernameParameter("email")
            .passwordParameter("password")
            .and().logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/").and().exceptionHandling()
            .accessDeniedPage("/access-denied")
    ;
}
@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring()
            .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/console/**");
}
     */
}