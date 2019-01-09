package breakout;

public class TimedEvent {
	long startTime;
	int duration; // in seconds
	boolean active;
	
	public TimedEvent() {
		active = false;
	}
	
	public boolean checkTriggered() {
		if (active && getSecondsRemaining()<=0)  {
			active = false;
			duration = 0;
			return true;
		}
		return false;
	}
	
	public int getSecondsRemaining() {
		int secondsRemaining = duration-new Long((System.nanoTime()-startTime)/1000000000L).intValue(); 
		return (secondsRemaining > 0) ? secondsRemaining:0;
	}
	
	public void setupEvent(int duration) {
		if (!active) {
			startTime = System.nanoTime();
			active = true;
		}
		this.duration += duration; 
	}

	public boolean isActive() {
		return active;
	}

	public void deActivate() {
		this.active = false;
		duration = 0;
	}
}
