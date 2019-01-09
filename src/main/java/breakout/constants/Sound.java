/**
 * 
 */
package breakout.constants;

public enum Sound {
	Click("click.wav"),
	Reflect("reflect.wav"),
	ExplosionBall("expl_ball.wav"),
	WeakBall("weak_ball.wav"), 
	Explosion("exp.wav"), 
	LooseLife("looselife.wav"),
	ExtraBall("extraball.wav"),
	GameOver("damn.wav"), 
	GainLife("gainlife.wav"),
	Score("score.wav");
	
	private String fileName;
	
	private Sound(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
}