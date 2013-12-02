package org.opennaas.gui.vcpe.entities;

import javax.validation.Valid;

/**
 * @author Jordi
 */
public class MultipleProviderLogical extends LogicalInfrastructure {

	@Valid
	private Network			providerNetwork1;
	@Valid
	private Network			providerNetwork2;
	@Valid
	private Network			clientNetwork;
	@Valid
	private LogicalRouter	providerLR1;
	@Valid
	private LogicalRouter	providerLR2;
	@Valid
	private LogicalRouter	clientLR;

	/**
	 * @return the providerNetwork1
	 */
	public Network getProviderNetwork1() {
		return providerNetwork1;
	}

	/**
	 * @param providerNetwork1
	 *            the providerNetwork1 to set
	 */
	public void setProviderNetwork1(Network providerNetwork1) {
		this.providerNetwork1 = providerNetwork1;
	}

	/**
	 * @return the providerNetwork2
	 */
	public Network getProviderNetwork2() {
		return providerNetwork2;
	}

	/**
	 * @param providerNetwork2
	 *            the providerNetwork2 to set
	 */
	public void setProviderNetwork2(Network providerNetwork2) {
		this.providerNetwork2 = providerNetwork2;
	}

	/**
	 * @return the clientNetwork
	 */
	public Network getClientNetwork() {
		return clientNetwork;
	}

	/**
	 * @param clientNetwork
	 *            the clientNetwork to set
	 */
	public void setClientNetwork(Network clientNetwork) {
		this.clientNetwork = clientNetwork;
	}

	/**
	 * @return the providerLR1
	 */
	public LogicalRouter getProviderLR1() {
		return providerLR1;
	}

	/**
	 * @param providerLR1
	 *            the providerLR1 to set
	 */
	public void setProviderLR1(LogicalRouter providerLR1) {
		this.providerLR1 = providerLR1;
	}

	/**
	 * @return the providerLR2
	 */
	public LogicalRouter getProviderLR2() {
		return providerLR2;
	}

	/**
	 * @param providerLR2
	 *            the providerLR2 to set
	 */
	public void setProviderLR2(LogicalRouter providerLR2) {
		this.providerLR2 = providerLR2;
	}

	/**
	 * @return the clientLR
	 */
	public LogicalRouter getClientLR() {
		return clientLR;
	}

	/**
	 * @param clientLR
	 *            the clientLR to set
	 */
	public void setClientLR(LogicalRouter clientLR) {
		this.clientLR = clientLR;
	}

}
