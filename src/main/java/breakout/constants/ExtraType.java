/**
 * 
 */
package breakout.constants;

public enum ExtraType {
	Empty('.', false),
	Points200('0', true),
	Points500('1', true),
	Points1000('2', true),
	Points2000('3', true),
	Points5000('4', true),
	Points10000('5', true),
	ExtraScore('g', true), // Extra score of 1000 for bricks with no bonus 
	Smaller('-', false),
	Larger('+', true),
	Life('l', true),
	Sticky('s', true, 10),
	EnergyBall('m', true, 5),
	NormalBall('b', true),
	Wall('w', true, 20),
	Freeze('f', false, 2),
	Weapon('p', true, 10),
	Random('?', true),
	Fast('>', false, 20),
	Slow('<', true, 20),
	Stars('j', true),
	Dark('d', false, 20),
	Chaos('c', false, 20),
	GhostPaddle('~', false, 20),
	Reset('!', false),
	Time('&', true),
	ExplosiveBall('*', true, 10),
	AttractBonus('}', true, 15),
	AttractMalus('{', false, 15),
	WeakBall('W', false, 10),
	Fast2(' ', false),
	Fast3(' ', false),
	Strange(' ', true);
	
	char type;
	boolean bonus;
	int timeOut;
	
	private ExtraType(char type, boolean bonus) {
		this.type = type;
		this.bonus = bonus;
		this.timeOut = 0;
	}
	
	private ExtraType(char type, boolean bonus, int timeOut) {
		this.type = type;
		this.bonus = bonus;
		this.timeOut = timeOut;
	}
	
	public char getType() {
		return type;
	}

	public boolean isBonus() {
		return bonus;
	}
	
	public int getTimeOut() {
		return timeOut;
	}
	
}