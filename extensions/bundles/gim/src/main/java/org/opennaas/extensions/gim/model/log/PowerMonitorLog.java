package org.opennaas.extensions.gim.model.log;

/*
 * #%L
 * GIM :: GIModel and APC PDU driver
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


import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.load.MeasuredLoad;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:58
 */
public class PowerMonitorLog {

	private int					samplePeriod;	// in mins
	private int					sampleDuration; // in mins

	private List<MeasuredLoad>	measuredLoads;

	public PowerMonitorLog(int sampTime, int sampDur) {
		samplePeriod = sampTime;
		sampleDuration = sampDur;

		measuredLoads = new ArrayList<MeasuredLoad>();
	}

	public List<MeasuredLoad> getMeasuredLoads() {
		return measuredLoads;
	}

	public void setMeasuredLoads(List<MeasuredLoad> measuredLoads) {
		this.measuredLoads = measuredLoads;
	}

	public void add(MeasuredLoad ml) {
		measuredLoads.add(ml);
	}

	public int getSampleDuration() {
		return sampleDuration;
	}

	public int getSamplePeriod() {
		return samplePeriod;
	}

	public void setSamplePeriod(int samplePeriod) {
		this.samplePeriod = samplePeriod;
	}

	public void setSampleDuration(int sampleDuration) {
		this.sampleDuration = sampleDuration;
	}

	public void finalize() throws Throwable {

	}

}