package kw.kng.security.sso.hr.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * ============================================================================
 * 
 * ClientServiceImpl -- عنوان جهاز المستخدم
 * 
 * ----------------------------------------------------------------------------
 * 
 * Implements BOTH methods on ClientService. The previous version of this file
 * 
 * implemented only getClientIp(), which is why the project would not compile:
 * 
 * a class that leaves an interface method unimplemented is abstract, and this
 * 
 * one is annotated @Service.
 *
 * 
 * 
 * FIX 1 -- X-Forwarded-For WAS TRUSTED UNCONDITIONALLY
 * 
 * -------------------------------------------------------
 * 
 * The original was:
 *
 * 
 * 
 * String xfHeader = request.getHeader("X-Forwarded-For");
 * 
 * if (xfHeader != null && xfHeader.length() > 0) {
 * 
 * return xfHeader.split(",")[0];
 * 
 * }
 * 
 * return request.getRemoteAddr();
 *
 * 
 * 
 * X-Forwarded-For is a REQUEST HEADER. Any client can send one. While this
 * 
 * value only reached a log line, that hardly mattered.
 *
 * 
 * 
 * It matters now. It decides which camp a registration is recorded against,
 * 
 * and therefore which camp's SECA device receives the patient. Anyone able to
 * 
 * reach the application could set the header by hand and have their
 * 
 * registration delivered to a camp they are not at -- and the database would
 * 
 * record that camp as fact, with an audit trail that looks entirely ordinary.
 *
 * 
 * 
 * The header is now read ONLY when app.trust-proxy=true. Set that only if a
 * 
 * reverse proxy genuinely sits in front of this application. With no proxy,
 * 
 * leave it false: getRemoteAddr() is the TCP peer address and the client
 * 
 * cannot forge it.
 *
 * 
 * 
 * app.trust-proxy=false # no proxy -- the safe default
 *
 * 
 * 
 * When enabled, the LAST entry of the chain is taken rather than the first.
 * 
 * A proxy APPENDS the address it saw; anything earlier was supplied by the
 * 
 * caller and is exactly as untrustworthy as before.
 *
 * 
 * 
 * FIX 2 -- IPv6 LOOPBACK IS NORMALISED
 * 
 * ------------------------------------
 * 
 * A developer machine reports "0:0:0:0:0:0:0:1", which matches no office
 * 
 * prefix, so the resolver correctly returns NONE and the camp dropdown sits
 * 
 * empty. That is right, but it looks like a defect during local testing.
 * 
 * Mapping it to 127.0.0.1 makes local behaviour predictable and weakens
 * 
 * nothing.
 *
 * 
 * 
 * ALSO REMOVED: the System.out.println of the header on every request.
 * 
 * ============================================================================
 * 
 */

@Service

public class ClientServiceImpl implements ClientService {

	private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

	/**
	 * 
	 * True ONLY when a reverse proxy sits in front of this application. With
	 * 
	 * no proxy, a true value hands every caller the ability to declare their
	 * 
	 * own IP address.
	 * 
	 */

	@Value("${app.trust-proxy:false}")

	private boolean trustProxy;

// ================================================================

// 1) The caller's IP address

// ================================================================

	@Override

	public String getClientIp(HttpServletRequest request) {

		if (request == null) {

			return null;

		}

		if (trustProxy) {

			String xf = request.getHeader("X-Forwarded-For");

			if (xf != null && xf.trim().length() > 0) {

				/*
				 * 
				 * Take the LAST hop. Each proxy appends what it saw, so the
				 * 
				 * final entry was written by the proxy we trust. Earlier
				 * 
				 * entries came from the client and are forgeable.
				 * 
				 */

				String[] parts = xf.split(",");

				String last = parts[parts.length - 1].trim();

				if (!last.isEmpty()) {

					return normalise(last);

				}

			}

		}

		return normalise(request.getRemoteAddr());

	}

// ================================================================

// 2) The subnet pattern -- THIS IS THE METHOD THAT WAS MISSING

// ================================================================

	/**
	 * 
	 * Turns 10.216.70.62 into "10.216.*.*".
	 *
	 * 
	 * 
	 * WHAT THIS IS FOR, AND WHAT IT IS NOT FOR
	 * 
	 * ----------------------------------------
	 * 
	 * JespaAuthFilter stores the result in the session as "clientIpPattern",
	 * 
	 * SsoServiceImpl reads it back, and HrGeneralServiceImpl puts it on the
	 * 
	 * model as "sso_ip_addr_pattern". Every one of those uses is DISPLAY and
	 * 
	 * LOGGING. Nothing compares it against anything.
	 *
	 * 
	 * 
	 * In particular, this is NOT what resolves a camp. That is
	 * 
	 * OfficeResolverService, which matches the raw IP against
	 * 
	 * DET_WORKSTATION.PC_IP first and DET_OFFICE.IP_PREFIX second -- and
	 * 
	 * DET_OFFICE.IP_PREFIX is stored in a different shape entirely ("10.216."
	 * 
	 * with a trailing dot, matched with LIKE).
	 *
	 * 
	 * 
	 * The two shapes are deliberately different and must not be swapped. The
	 * 
	 * star form here is for a human reading a screen; the dotted form there is
	 * 
	 * for a SQL LIKE. If this method is ever changed to return "10.216." it
	 * 
	 * will silently start looking like the routing key, and somebody will use
	 * 
	 * it as one.
	 *
	 * 
	 * 
	 * DEFENSIVE ON PURPOSE
	 * 
	 * --------------------
	 * 
	 * Returns the input unchanged when it is not a dotted-quad -- an IPv6
	 * 
	 * address, or a hostname. Returning null instead would put the word "null"
	 * 
	 * on the SSO header of every page for anyone on IPv6, which looks like a
	 * 
	 * fault and is not one.
	 * 
	 */

	@Override

	public String getClientIpPattern(String clientIp) {

		if (clientIp == null) {

			return null;

		}

		String ip = clientIp.trim();

		if (ip.isEmpty()) {

			return null;

		}

		String[] p = ip.split("\\.");

		if (p.length != 4) {

// Not IPv4. Hand it back as-is rather than inventing a pattern.

			return ip;

		}

		return p[0] + "." + p[1] + ".*.*";

	}

// ================================================================

// helpers

// ================================================================

	/** IPv6 loopback to IPv4 loopback, so local testing behaves predictably. */

	private String normalise(String ip) {

		if (ip == null) {

			return null;

		}

		String s = ip.trim();

		if ("0:0:0:0:0:0:0:1".equals(s) || "::1".equals(s)) {

			return "127.0.0.1";

		}

		return s;

	}
}