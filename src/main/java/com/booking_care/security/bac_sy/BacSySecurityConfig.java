package com.booking_care.security.bac_sy;

import com.booking_care.model.TaiKhoan;
import com.booking_care.repository.TaiKhoanRepository;
import com.booking_care.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Order(2)
@EnableWebSecurity
public class BacSySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Bean
    @Qualifier("bacSyUserDetailsService")
    public UserDetailsService bacSyDetailsService() {
        return new BacSyDetailsServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(bacSyDetailsService()).passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager bacSyAuthenticationManagerBean() throws Exception { // Sửa tên bean
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider3() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(bacSyDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authenticationProvider(authenticationProvider3())
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**", "/api/login/bac-sy", "/bacsy/login").permitAll() // Cập nhật /api/login/bac-sy
                .antMatchers("/bacsy/**").hasAuthority("BAC_SY")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/bacsy/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/bacsy/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        String username = userDetails.getUsername();
                        String token = jwtTokenProvider.generateToken(authentication);

                        TaiKhoan taiKhoan = taiKhoanRepo.findByUsername(username);
                        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tài khoản không phải bác sĩ");
                            return;
                        }

                        response.setHeader("Authorization", "Bearer " + token);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"token\": \"" + token + "\", \"redirect\": \"/bacsy/home\"}");
                    }
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/bacsy/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/bacsy/login")
                .and()
                .exceptionHandling().accessDeniedPage("/403");

        http.rememberMe()
                .rememberMeParameter("bacSyId")
                .rememberMeCookieName("bacSyId")
                .key("aaaaa12123123")
                .tokenValiditySeconds(1296000)
                .userDetailsService(bacSyDetailsService());
    }
}