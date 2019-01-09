package breakout.constants;




public enum GameImage {
	Background("back", ".png"),
	FrLeft("fr_left.png"),
	FrRight("fr_right.png"),
	FrTop("fr_top.png"),
	Life("life.png", 1, 3),
	Paddle("paddle.png", 1, 4),
	Ball("ball.png", 5, 1),
	Brick("bricks.png", 21, 1),
	Extra("extras.png", 32, 1),
	Shot("shot.png", 4, 1),
	Weapon("weapon.png", 4, 1);
	
	private String path;
	private String suffix;
	private int numX;
	private int numY;
	
	private GameImage(String path) {
		this(path, 1, 1);
	}
	
	private GameImage(String path, String suffix) {
		this.path = path;
		this.suffix = suffix;
		this.numX = 1;
		this.numY = 1;
	}
	
	private GameImage(String path, int numX, int numY) {
		this.path = path;
		this.suffix = null;
		this.numX = numX;
		this.numY = numY;
	}
	
	public String getPath() {
		return path;
	}

	public int getNumX() {
		return numX;
	}

	public int getNumY() {
		return numY;
	}

	public String getSuffix() {
		return suffix;
	}

}
