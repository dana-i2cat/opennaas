package org.opennaas.extensions.openflowswitch.capability.portstatistics;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Switch Port statistics
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PortStatistics {

	private int		port;

	private long	receivePackets;
	private long	transmitPackets;
	private long	receiveBytes;
	private long	transmitBytes;
	private long	receiveDropped;
	private long	transmitDropped;
	private long	receiveErrors;
	private long	transmitErrors;
	private long	receiveFrameErrors;
	private long	receiveOverrunErrors;
	private long	receiveCRCErrors;
	private long	collisions;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getReceivePackets() {
		return receivePackets;
	}

	public void setReceivePackets(long receivePackets) {
		this.receivePackets = receivePackets;
	}

	public long getTransmitPackets() {
		return transmitPackets;
	}

	public void setTransmitPackets(long transmitPackets) {
		this.transmitPackets = transmitPackets;
	}

	public long getReceiveBytes() {
		return receiveBytes;
	}

	public void setReceiveBytes(long receiveBytes) {
		this.receiveBytes = receiveBytes;
	}

	public long getTransmitBytes() {
		return transmitBytes;
	}

	public void setTransmitBytes(long transmitBytes) {
		this.transmitBytes = transmitBytes;
	}

	public long getReceiveDropped() {
		return receiveDropped;
	}

	public void setReceiveDropped(long receiveDropped) {
		this.receiveDropped = receiveDropped;
	}

	public long getTransmitDropped() {
		return transmitDropped;
	}

	public void setTransmitDropped(long transmitDropped) {
		this.transmitDropped = transmitDropped;
	}

	public long getReceiveErrors() {
		return receiveErrors;
	}

	public void setReceiveErrors(long receiveErrors) {
		this.receiveErrors = receiveErrors;
	}

	public long getTransmitErrors() {
		return transmitErrors;
	}

	public void setTransmitErrors(long transmitErrors) {
		this.transmitErrors = transmitErrors;
	}

	public long getReceiveFrameErrors() {
		return receiveFrameErrors;
	}

	public void setReceiveFrameErrors(long receiveFrameErrors) {
		this.receiveFrameErrors = receiveFrameErrors;
	}

	public long getReceiveOverrunErrors() {
		return receiveOverrunErrors;
	}

	public void setReceiveOverrunErrors(long receiveOverrunErrors) {
		this.receiveOverrunErrors = receiveOverrunErrors;
	}

	public long getReceiveCRCErrors() {
		return receiveCRCErrors;
	}

	public void setReceiveCRCErrors(long receiveCRCErrors) {
		this.receiveCRCErrors = receiveCRCErrors;
	}

	public long getCollisions() {
		return collisions;
	}

	public void setCollisions(long collisions) {
		this.collisions = collisions;
	}

}
