package com.booking_care.security.benh_nhan;

import com.booking_care.model.CustomOAuth2User;
import com.booking_care.model.TaiKhoan;
import com.booking_care.repository.TaiKhoanRepository;
import com.booking_care.security.JwtTokenProvider;
import com.booking_care.service.BenhNhanGoogleService;
import com.booking_care.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Order(1)
@EnableWebSecurity
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private BenhNhanGoogleService benhNhanGoogleService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @Qualifier("benhNhanUserDetailsService")
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider2() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/api/**","/images/**","/fonts/**","/", "/oauth/**", "/dang-nhap", "/api/login", "/js/**", "/css/**", "/api/bacsy/search", "/danh-sach-bac-sy", "/api/chuyen-khoa", "/bac-sy/{id}", "/api/bacsy").permitAll()
             //   .antMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider2())
                .formLogin()
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .successHandler((request, response, authentication) -> {
                    String token = jwtTokenProvider.generateToken(authentication);
                    response.setHeader("Authorization", "Bearer " + token);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"token\": \"" + token + "\", \"redirect\": \"/\"}");
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Tài khoản hoặc mật khẩu không đúng\"}");
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                .oauth2Login()
                .loginPage("/dang-nhap")
                .failureUrl("/dang-nhap?error")
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {
                        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                        TaiKhoan taiKhoan = taiKhoanRepo.findByUsername(oauthUser.getEmail());
                        if (taiKhoan == null) {
                            benhNhanGoogleService.processOAuthPostLogin(oauthUser);
                        }
                        UserDetails userDetails = userDetailsService().loadUserByUsername(oauthUser.getEmail());
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                        String token = jwtTokenProvider.generateToken(usernamePasswordAuthenticationToken);
                        response.setHeader("Authorization", "Bearer " + token);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"token\": \"" + token + "\", \"redirect\": \"/profile\"}");
                    }
                });

        http.rememberMe()
                .rememberMeParameter("benhNhanId")
                .rememberMeCookieName("benhNhanId")
                .key("uniqueAndSecret")
                .tokenValiditySeconds(1296000)
                .userDetailsService(userDetailsService());
    }
}