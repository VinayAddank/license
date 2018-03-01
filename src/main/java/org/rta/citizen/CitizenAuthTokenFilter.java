package org.rta.citizen;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.rta.citizen.common.CustomUserDetail;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.UserRepository;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class CitizenAuthTokenFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private RegistrationService registrationService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authToken = httpRequest.getHeader(this.tokenHeader);
		if (null != authToken
				&& (!jwtTokenUtil.isTokenExpired(authToken) || !jwtTokenUtil.isRegTokenExpired(authToken))) {
			String tokenType = jwtTokenUtil.getTokenType(authToken);
			if (TokenType.CITIZEN.toString().equalsIgnoreCase(tokenType)) {
				Long sessionId = jwtTokenUtil.getSessionIdFromToken(authToken);
				if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserSessionEntity userDetails = userRepo.findBySessionId(sessionId);
					UserDetails um = null;
					if (!ObjectsUtil.isNull(userDetails)) {
						um = new UserSessionModel(userDetails.getSessionId(), userDetails.getAadharNumber(),
								Status.getStatus(userDetails.getCompletionStatus()), userDetails.getUniqueKey(),
								ServiceType.getServiceType(userDetails.getServiceCode()));
					}
					if (null != um && SecurityContextHolder.getContext().getAuthentication() == null) {
						if (!jwtTokenUtil.isTokenExpired(authToken)) {
							UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
									um, null, um.getAuthorities());
							authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				}
			} else {
				UserModel userModel = null;
				try {
					userModel = registrationService.getRtaUserByToken(authToken);
				} catch (UnauthorizedException e) {
					e.printStackTrace();
				}
				if (null != userModel && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails um = new CustomUserDetail(null, null, userModel.getUserName(), userModel.getStatus(),
							userModel.getUserRole());
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(um,
							null, um.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
		chain.doFilter(request, response);
	}
}
