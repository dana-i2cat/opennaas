package org.opennaas.extensions.pdu.model;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUModel implements IModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4002472167559948067L;

	/**
	 * A live reference to the PDU in GIM model
	 */
	private PDU					pdu;

	public PDU getPdu() {
		return pdu;
	}

	public void setPdu(PDU pdu) {
		this.pdu = pdu;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	@Override
	public String toXml() throws SerializationException {
		throw new UnsupportedOperationException();
	}

}
