package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.AutnumDAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.result.AutnumResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "autnum", urlPatterns = { "/autnum/*" })
public class AutnumServlet extends DataAccessServlet<AutnumDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected AutnumDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getAutnumDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "autnum";
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, AutnumDAO dao)
			throws HttpException, RdapDataAccessException {
		AutnumRequest request = new AutnumRequest(Util.getRequestParams(httpRequest)[0]);

		Autnum autnum = dao.getByRange(request.getAutnum());
		if (autnum == null) {
			return null;
		}
		
		return new AutnumResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), autnum,
				Util.getUsername(httpRequest));
	}

	private class AutnumRequest {

		private Long autnum;

		public AutnumRequest(String autnum) throws BadRequestException {
			super();
			try {
				this.autnum = Long.parseLong(autnum);
			} catch (NumberFormatException e) {
				throw new BadRequestException("Cannot parse the given autnum as a positive 32-bit integer.", e);
			}
			if (this.autnum < 0) {
				throw new BadRequestException("Autnums are supposed to be positive.");
			}
			if (this.autnum > 0xFFFFFFFFL) {
				throw new BadRequestException("The autnum is too large to fit in an unsigned 32-bit integer.");
			}
		}

		public Long getAutnum() {
			return autnum;
		}
	}

}