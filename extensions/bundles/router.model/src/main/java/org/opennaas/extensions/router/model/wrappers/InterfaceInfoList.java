package org.opennaas.extensions.router.model.wrappers;

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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class containing {@link InterfaceInfo} list
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "interfaceInfos")
@XmlAccessorType(XmlAccessType.FIELD)
public class InterfaceInfoList {

	@XmlElement(name = "interfaceInfo")
	private List<InterfaceInfo>	interfaceInfos;

	public List<InterfaceInfo> getInterfaceInfos() {
		return interfaceInfos;
	}

	public void setInterfaceInfos(List<InterfaceInfo> interfaceInfos) {
		this.interfaceInfos = interfaceInfos;
	}

}
