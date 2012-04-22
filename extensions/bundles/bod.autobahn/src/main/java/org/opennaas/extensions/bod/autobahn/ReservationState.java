package org.opennaas.extensions.bod.autobahn;

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

	public final int value;

	ReservationState(int value)
	{
		this.value = value;
	}

	public static ReservationState valueOf(int value)
	{
		for (ReservationState state: ReservationState.values()) {
			if (state.value == value) {
				return state;
			}
		}
		return null;
	}
}