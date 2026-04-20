package com.example.demo.security;

//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 【設施門口的魔法感應器】 這是每一項設施門口都會有的感應器。 每次有人想玩設施（發送請求）時，它都會攔截下來，並說：「請先感應你的魔法手環
 * (MagicBand)！」
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
		this.tokenProvider = tokenProvider;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("PATH: " + request.getServletPath());
		System.out.println("HEADER: " + request.getHeader("Authorization"));

		String jwt = getJwtFromRequest(request);
		System.out.println("JWT: " + jwt);

		if (jwt != null) {
			System.out.println("VALID: " + tokenProvider.validateToken(jwt));
		}

		System.out.println("=====================");

		// 2. 🔥 【關鍵修改位置】

		// ⭐⭐⭐ 這段加在這裡（最上面）
		String path = request.getServletPath();
		if (!path.equals("/api/auth/change-password") && path.startsWith("/api/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

			String email = tokenProvider.getUserEmailFromToken(jwt);

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
					null, userDetails.getAuthorities());

			// ✅ 只要跑完這行，Spring Security 就會認識你是誰，不會再給 403
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		// 最後一定要執行這行，讓請求繼續往下走
		filterChain.doFilter(request, response);
	}

	/**
	 * 【從請求頭部提取手環內容】
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		// 通常手環會藏在名為 "Authorization" 的口袋裡，且開頭會寫著 "Bearer "
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
