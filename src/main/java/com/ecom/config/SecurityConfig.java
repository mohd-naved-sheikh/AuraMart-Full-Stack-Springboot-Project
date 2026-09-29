package com.ecom.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ecom.model.UserDtls;
import com.ecom.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

	@Autowired
	@Lazy
	private AuthSuccessHandlerImpl authenticationSuccessHandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;

	// We inject UserService with @Lazy to avoid any circular dependencies!
	@Autowired
	@Lazy
	private UserService userService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf -> csrf.disable())
		    .cors(cors -> cors.disable())
		    .authorizeHttpRequests(req -> req
		        .requestMatchers("/user/**").hasRole("USER")
		        .requestMatchers("/admin/**").hasRole("ADMIN")
		        .requestMatchers("/**").permitAll().requestMatchers("/api/payment/**").permitAll()
		    )
		    .formLogin(form -> form
		        .loginPage("/signin")
		        .loginProcessingUrl("/login")
		        .failureHandler(authenticationFailureHandler)
		        .successHandler(authenticationSuccessHandler)
		    )
		    .oauth2Login(oauth -> oauth
		    	    .loginPage("/signin")
		    	    .userInfoEndpoint(userInfo -> userInfo
		    	        .userAuthoritiesMapper(authorities -> {
		    	            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		    	        })
		    	    )
		    	    .defaultSuccessUrl("/")
		    	)
		    .logout(logout -> logout
		        .logoutSuccessUrl("/signin?logout")
		        .permitAll()
		    );
		
		return http.build();
	}

	// =========================================================
	// THE ULTIMATE GOOGLE BOUNCER (Inline Success Handler)
	// =========================================================
	@Bean
	public AuthenticationSuccessHandler googleLoginSuccessHandler() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				
				OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
				String email = oauthUser.getAttribute("email");
				String name = oauthUser.getAttribute("name");
				
				// 1. Save user if they are new
				UserDtls user = userService.getUserByEmail(email);
				if (user == null) {
					user = userService.saveGoogleUser(email, name);
				}
				
				// 2. Force Spring to give them the official ROLE_USER badge
				GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
				Authentication newAuth = new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
				SecurityContextHolder.getContext().setAuthentication(newAuth);
				
				// 3. Send them to the homepage!
				response.sendRedirect("/");
			}
		};
	}
}