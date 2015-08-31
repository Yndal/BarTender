package control.dispenser;

public interface DispenserListener {
	public enum State {
		Idle, Busy, DownForMaintanance, DrinkMade, Initializing, InitializationSucceeded, InitializationFailed
	}
	public void onStateChanged(State state);
}