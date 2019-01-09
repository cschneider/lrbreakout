/**
 * 
 */
package breakout.constants;


public enum BrickType {
	Empty('.', false, 0),
	Indestructible('E', false, 0),
	IndestEnergy('#', false, 1000),
	IndestEnergyRandom('@', false, 1000),
	BlueOne('a', true),
	BlueTwo('b', true, 80 * 2),
	BlueThree('c', true, 80 * 3),
	Transparent('v', true, 80 * 4),
	GreenOne('x', true, 80 * 2),
	GreenTwo('y', true, 80 * 4),
	GreenThree('z', true, 80 *6),
	Red('d', true),
	Orange('e', true),
	Green('f', true),
	Blue('g', true),
	Yellow('h', true),
	Pink('i', true),
	White('j', true),
	Gray('k', true),
	TNT('*', true, 80 * 2),
	Growing('!', true, 80 * 2);
	
	private static final int BRICK_SCORE = 80;

	char type;
	boolean toDestroy;
	int score;

	BrickType(char type, boolean toDestroy, int score) {
		this.type = type;
		this.toDestroy = toDestroy;
		this.score = score;
	}
	
	BrickType(char type, boolean toDestroy) {
		this.type = type;
		this.toDestroy = toDestroy;
		this.score = BRICK_SCORE;
	}
	
	public char getType() {
		return this.type;
	}

	public boolean toDestroy() {
		return this.toDestroy;
	}
	
	public int getScore() {
		return score;
	}
	
}