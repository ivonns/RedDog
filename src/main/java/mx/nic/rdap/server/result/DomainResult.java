package mx.nic.rdap.server.result;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.LinkDAO;
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.DomainJsonWriter;
import mx.nic.rdap.server.util.Util;

/**
 * A result from a Domain request
 */
public class DomainResult extends RdapResult {

	private Domain domain;

	public DomainResult(String header, String contextPath, Domain domain, String userName)
			throws FileNotFoundException {
		notices = new ArrayList<Remark>();
		this.domain = domain;
		this.userInfo = new UserInfo(userName);
		addSelfLinks(header, contextPath, domain);
		validateResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {

		return DomainJsonWriter.getJson(domain, userInfo.isUserAuthenticated(), userInfo.isOwner(domain));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, there is no notices for this request
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			OperationalProfileValidator.validateDomain(domain);
			// Point 1.5.18 of rdap operational profile by ICANN
			domain.getRemarks().add(Util.getEppInformationRemark());
			// Point 1.5.20 of rdap operational profile by ICANN
			domain.getRemarks().add(Util.getWhoisInaccuracyComplaintFormRemark());

		}
	}

	/**
	 * Generates a link with the self information and add it to the domain
	 */
	public static void addSelfLinks(String header, String contextPath, Domain domain) {
		LinkDAO self = new LinkDAO(header, contextPath, "domain",
				domain.getLdhName() + "." + ZoneModel.getZoneNameById(domain.getZoneId()));
		domain.getLinks().add(self);

		for (Nameserver ns : domain.getNameServers()) {
			self = new LinkDAO(header, contextPath, "nameserver", ns.getLdhName());
			ns.getLinks().add(self);
		}

		for (Entity ent : domain.getEntities()) {
			self = new LinkDAO(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}

		if (domain.getIpNetwork() != null) {
			self = new LinkDAO(header, contextPath, "ip", domain.getIpNetwork().getStartAddress().getHostAddress());
			domain.getIpNetwork().getLinks().add(self);
		}
	}

}
