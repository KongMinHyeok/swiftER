package kr.co.swiftER.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	
	private final DataSource dataSource;
	private final AuthenticationFailureHandler customFailureHandler;
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// 사이트 위조 방지 설정
		http.csrf().disable();
		
		
		// 인가(접근권한) 설정
		http.authorizeHttpRequests()
				.requestMatchers("/css/**").permitAll() // spring security가 css, img,js 폴더 접근도 막아서 설정해
				.requestMatchers("/img/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/member/login").permitAll()
				.requestMatchers("/member/terms").permitAll()
				.requestMatchers("/member/registerNor").permitAll()
				.requestMatchers("/member/registerDoc").permitAll()
				.requestMatchers("/error/**").permitAll()
				.requestMatchers("/cs/**").hasAnyRole("0", "1", "2")
				.requestMatchers("/community/**").hasAnyRole("0", "1", "2")
				.requestMatchers("/download").permitAll()
				.requestMatchers("/").permitAll()
				.requestMatchers("/member/**").permitAll();
		
		/*
		// 자동로그인 설정
		http.rememberMe()
			.userDetailsService(userService)
			.tokenRepository(tokenRepository());
		*/
		
		// 로그인 설정
		http.formLogin()
		.loginPage("/member/login")
		.defaultSuccessUrl("/")
		.failureHandler(customFailureHandler)
		.usernameParameter("uid")
		.passwordParameter("pass");
		
		// 로그아웃 설정
		http.logout()
		.invalidateHttpSession(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
		.logoutSuccessUrl("/index")
		.deleteCookies("remember-me", "JSESIONID"); // 자동 로그인 쿠키 삭제
		
		return http.build();
	}
	
	@Bean
    public PersistentTokenRepository tokenRepository() {
      // JDBC 기반의 tokenRepository 구현체
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); // dataSource 주입
        return jdbcTokenRepository;
    }
	
	@Autowired
	private SecurityUserService userService;
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Security 사용자에 대한 권한 설정 (noop은 평문으로 저장해줌)
		//auth.inMemoryAuthentication().withUser("admin").password("{noop}1234").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("manager").password("{noop}1234").roles("MANAGER");
		//auth.inMemoryAuthentication().withUser("member").password("{noop}1234").roles("MEMBER");
	
		// 로그인 인증 처리 서비스, 암호화 방식 설정(필수 설정)
		auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
	
	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
}
