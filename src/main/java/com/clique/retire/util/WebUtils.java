package com.clique.retire.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebUtils {

	private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
	private static final String IP = "ip";
	private static HttpServletRequest request;

	@SuppressWarnings("static-access")
	@Autowired
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Busca o IP do cliente pela requisição.
	 * 
	 * @return
	 */
	public static String getClientIp() {

		String remoteAddr = StringUtils.EMPTY;
		if (request != null) {
			remoteAddr = request.getHeader(IP);
		}

        return remoteAddr;
	}
	
	/**
	 * Busca o IP do cliente pela requisição.
	 * 
	 * @return
	 */
	public static String getClientIpTCP() {
		
		String remoteAddr = StringUtils.EMPTY;
		if (request != null) {
			remoteAddr = request.getHeader(X_FORWARDED_FOR);
			
			if (StringUtils.isBlank(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		
		return remoteAddr;
	}
	
	public static String getHostName(){
		String hostName = StringUtils.EMPTY;
		if (request != null) {
			hostName = request.getRemoteHost();
		}

		return hostName;
	}
}