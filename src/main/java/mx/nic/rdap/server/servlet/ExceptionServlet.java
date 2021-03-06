package mx.nic.rdap.server.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.result.ExceptionResult;
import mx.nic.rdap.server.result.RdapResult;

@WebServlet(name = "exception", urlPatterns = { "/exception" })
public class ExceptionServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest) {
		Object object = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		// The servlet was accesed directly
		if (object == null) {
			httpRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
			httpRequest.setAttribute(RequestDispatcher.ERROR_MESSAGE, httpRequest.getRequestURI());
		}
		RdapResult result = new ExceptionResult(httpRequest);
		return result;
	}

}
