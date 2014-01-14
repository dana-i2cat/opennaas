package org.opennaas.extensions.vnmapper;

/*
 * #%L
 * OpenNaaS :: VNMapper Resource
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

public class VNLifetime {

	private int				vnID;
	private int				arrivalTime;
	private int				releasingTime;
	private int				providerID;
	private MappingResult	mres;
	private int				waitingForReleasing;

	public VNLifetime() {
		mres = new MappingResult();
		waitingForReleasing = -1;
	}

	public VNLifetime(int id, int arrival, int life)
	{
		mres = new MappingResult();
		vnID = id;
		arrivalTime = arrival;
		releasingTime = life;
		waitingForReleasing = -1;
	}

	public int getVnID() {
		return vnID;
	}

	public void setVnID(int vnID) {
		this.vnID = vnID;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getReleasingTime() {
		return releasingTime;
	}

	public void setReleasingTime(int releasingTime) {
		this.releasingTime = releasingTime;
	}

	public int getProviderID() {
		return providerID;
	}

	public void setProviderID(int providerID) {
		this.providerID = providerID;
	}

	public MappingResult getMres() {
		return mres;
	}

	public void setMres(MappingResult mres) {
		this.mres = mres;
	}

	public int getWaitingForReleasing() {
		return waitingForReleasing;
	}

	public void setWaitingForReleasing(int waitingForReleasing) {
		this.waitingForReleasing = waitingForReleasing;
	}

}
