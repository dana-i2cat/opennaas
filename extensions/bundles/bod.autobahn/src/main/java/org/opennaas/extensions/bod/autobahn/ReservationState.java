package org.opennaas.extensions.bod.autobahn;

/*
 * #%L
 * OpenNaaS :: BoD :: Autobahn driver
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

public enum ReservationState
{
	UNKNOWN(0),
	ACCEPTED(1),
	PATHFINDING(2),
	LOCAL_CHECK(3),
	SCHEDULING(4),
	SCHEDULED(5),
	CANCELLING(6),
	DEFERRED_CANCEL(7),
	WITHDRAWING(8),
	ACTIVATING(9),
	ACTIVE(10),
	FINISHING(11),
	FINISHED(21),
	CANCELLED(22),
	FAILED(23);

	public final int	value;

	ReservationState(int value)
	{
		this.value = value;
	}

	public static ReservationState valueOf(int value)
	{
		for (ReservationState state : ReservationState.values()) {
			if (state.value == value) {
				return state;
			}
		}
		return null;
	}
}