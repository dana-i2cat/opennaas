/**
 * This file was auto-generated by mofcomp -j version 1.0.0 on Mon Apr 18
 * 09:34:15 CEST 2011.
 */

package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import java.io.Serializable;

/**
 * This Class contains accessor and mutator methods for all properties defined in the CIM class DeviceConnection as well as methods comparable to the
 * invokeMethods defined for this class. This Class implements the DeviceConnectionBean Interface. The CIM class DeviceConnection is described as
 * follows:
 * 
 * The DeviceConnection relationship indicates that two or more Devices are connected together.
 */
public class DeviceConnection extends Dependency implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1627260301916983811L;

	/**
	 * This constructor creates a DeviceConnectionBeanImpl Class which implements the DeviceConnectionBean Interface, and encapsulates the CIM class
	 * DeviceConnection in a Java Bean. The CIM class DeviceConnection is described as follows:
	 * 
	 * The DeviceConnection relationship indicates that two or more Devices are connected together.
	 */
	public DeviceConnection() {
	};

	/**
	 * This method create an Association of the type DeviceConnection between one LogicalDevice object and LogicalDevice object
	 */
	public static DeviceConnection link(LogicalDevice antecedent, LogicalDevice
			dependent) {

		return (DeviceConnection) Association.link(DeviceConnection.class, antecedent, dependent);
	}// link

	/**
	 * The following constants are defined for use with the ValueMap/Values qualified property negotiatedSpeed.
	 */
	private long	negotiatedSpeed;

	/**
	 * This method returns the DeviceConnection.negotiatedSpeed property value. This property is described as follows:
	 * 
	 * When several bus and connection speeds are possible, the NegotiatedSpeed property defines the one that is in use between the Devices. Speed is
	 * specified in bits per second. If connection or bus speeds are not negotiated, or if this information is not available or not important to
	 * Device management, the property should be set to 0.
	 * 
	 * @return long current negotiatedSpeed property value
	 * @exception Exception
	 */
	public long getNegotiatedSpeed() {

		return this.negotiatedSpeed;
	} // getNegotiatedSpeed

	/**
	 * This method sets the DeviceConnection.negotiatedSpeed property value. This property is described as follows:
	 * 
	 * When several bus and connection speeds are possible, the NegotiatedSpeed property defines the one that is in use between the Devices. Speed is
	 * specified in bits per second. If connection or bus speeds are not negotiated, or if this information is not available or not important to
	 * Device management, the property should be set to 0.
	 * 
	 * @param long new negotiatedSpeed property value
	 * @exception Exception
	 */
	public void setNegotiatedSpeed(long negotiatedSpeed) {

		this.negotiatedSpeed = negotiatedSpeed;
	} // setNegotiatedSpeed

	/**
	 * The following constants are defined for use with the ValueMap/Values qualified property negotiatedDataWidth.
	 */
	private long	negotiatedDataWidth;

	/**
	 * This method returns the DeviceConnection.negotiatedDataWidth property value. This property is described as follows:
	 * 
	 * When several bus and connection data widths are possible, the NegotiatedDataWidth property defines the one that is in use between the Devices.
	 * Data width is specified in bits. If data width is not negotiated, or if this information is not available or not important to Device
	 * management, the property should be set to 0.
	 * 
	 * @return long current negotiatedDataWidth property value
	 * @exception Exception
	 */
	public long getNegotiatedDataWidth() {

		return this.negotiatedDataWidth;
	} // getNegotiatedDataWidth

	/**
	 * This method sets the DeviceConnection.negotiatedDataWidth property value. This property is described as follows:
	 * 
	 * When several bus and connection data widths are possible, the NegotiatedDataWidth property defines the one that is in use between the Devices.
	 * Data width is specified in bits. If data width is not negotiated, or if this information is not available or not important to Device
	 * management, the property should be set to 0.
	 * 
	 * @param long new negotiatedDataWidth property value
	 * @exception Exception
	 */
	public void setNegotiatedDataWidth(long negotiatedDataWidth) {

		this.negotiatedDataWidth = negotiatedDataWidth;
	} // setNegotiatedDataWidth

} // Class DeviceConnection
