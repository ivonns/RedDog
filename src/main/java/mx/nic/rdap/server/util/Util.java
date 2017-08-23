package mx.nic.rdap.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.exception.http.NotFoundException;
import mx.nic.rdap.server.configuration.RdapConfiguration;

/**
 * Random miscellaneous functions useful anywhere.
 */
public class Util {

	/**
	 * If the request's URI is /rdap/ip/192.0.2.0/24, then this returns
	 * ["192.0.2.0", "24"].
	 * 
	 * @param request
	 *            request you want the arguments from.
	 * @return request arguments.
	 * @throws HttpException
	 *             <code>request</code> is not a valid RDAP URI.
	 */
	public static String[] getRequestParams(HttpServletRequest request) throws HttpException {
		try {
			URLDecoder.decode(request.getRequestURI(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new BadRequestException("The request does not appear to be UTF-8 encoded.", e);
		}

		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			throw new NotFoundException("The request does not appear to be a valid RDAP URI. " //
					+ "I might need more arguments than that.");
		}
		// Ignores the first "/"
		return pathInfo.substring(1).split("/");
	}

	public static String getUsername(HttpServletRequest httpRequest) {
		String username = httpRequest.getRemoteUser();
		return RdapConfiguration.isAnonymousUsername(username) ? null : username;
	}

	/**
	 * Loads the properties configuration file
	 * <code>META-INF/fileName.properties</code> and returns it.
	 * 
	 * @param fileName
	 *            name of the configuration file you want to load.
	 * @return configuration requested.
	 * @throws IOException
	 *             Error attempting to read the configuration out of the
	 *             classpath.
	 */
	public static Properties loadProperties(String fileName) throws IOException {
		fileName = "META-INF/" + fileName + ".properties";
		Properties result = new Properties();
		try (InputStream configStream = Util.class.getClassLoader().getResourceAsStream(fileName)) {
			if (configStream != null) {
				result.load(configStream);
			}
		}
		return result;
	}

	public static Remark getOperationalProfileRemark() {
		Remark remark = new Remark();
		RemarkDescription description = new RemarkDescription();
		description.setDescription(
				"This response conforms to the RDAP Operational Profile for gTLD Registries and Registrars version 1.0");
		remark.getDescriptions().add(description);
		return remark;
	}

	public static Remark getEppInformationRemark() {
		Remark remark = new Remark();
		remark.setTitle("EPP Status Codes");
		RemarkDescription description = new RemarkDescription();
		description.setDescription("For more information on domain status codes, please visit https://icann.org/epp");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://icann.org/epp");
		remark.getLinks().add(link);
		return remark;
	}

	public static Remark getWhoisInaccuracyComplaintFormRemark() {
		Remark remark = new Remark();
		RemarkDescription description = new RemarkDescription();
		remark.setTitle("Whois Inaccuracy Complaint Form");
		description.setDescription("URL of the ICANN Whois Inaccuracy Complaint Form: https://www.icann.org/wicf");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://www.icann.org/wicf");
		remark.getLinks().add(link);
		return remark;
	}

}