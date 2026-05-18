package com.example.demo.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * 【樂園門禁過濾系統】
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        JwtAuthenticationFilter jwtAuthenticationFilter =
           new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
      
        http
            // 1. 啟用 CORS 並帶入最下方的配置
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. 關掉 CSRF
            .csrf(csrf -> csrf.disable()) 
            // 3. 設定為「無狀態」模式，完全依賴 Token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth 
                // 問路的人（Preflight 預檢請求）：通通放行
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                
                // 公開訪問的 API 放行名單
                .requestMatchers("/api/sse/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                
                // 💡 修正點：同時放行 /api/users/details、/api/users/details/ 以及 /api/users/details/**
                .requestMatchers("/api/users/details", "/api/users/details/**").permitAll()
                
                .requestMatchers("/api/auth/send-mail").permitAll()      
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/notifications/**").permitAll()
                .requestMatchers("/api/news/**").permitAll()
                .requestMatchers("/api/risk/**").permitAll()
                
                // 其他業務 API 放行
                .requestMatchers("/by-email").permitAll()
                .requestMatchers("/api/assets/**").permitAll()
                .requestMatchers("/api/strategy-api/**").permitAll()
                .requestMatchers("/api/strategy-set/**").permitAll()
                .requestMatchers("/api/rebalance/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/monte/**").permitAll()
                .requestMatchers("/send-email").permitAll()
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/api/asset-history/**").permitAll()
                .requestMatchers("/api/liabilities/**").permitAll()

                // 必須登入驗證的 API
                .requestMatchers("/api/auth/change-password").authenticated() 
                .requestMatchers("/profile").authenticated()

                // 剩下的神秘區域通通要檢查身分
                .anyRequest().authenticated()
            );
        
        // 在進入其他過濾器前，先讓「手環檢查員」感應一下
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
       
        return http.build();
    }

    /**
     * 【樂園入口的語言與溝通設定 (CORS)】
     * 乾淨唯一的 CORS 配置，徹底解決與 _1 的命名衝突
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 精確允許本地端以及你們兩個的前端 Netlify 網址
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "https://wealthmap-jingyi-20260514.netlify.app",
            "https://your-own-frontend-url.netlify.app" // 記得把這行改成你的前端網址，如果有的話
        ));
        
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}