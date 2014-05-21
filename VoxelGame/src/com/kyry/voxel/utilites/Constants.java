package com.kyry.voxel.utilites;

import java.util.Random;

public class Constants {
	
	public static final int WIDTH = 1280; //Window Width
	public static final int HEIGHT = 720; //Window Height
	public static float FPS = 30; // not a constant I know but it will work :)
	
	public static Random rand = new Random(); //Instantiate a new 'Random' object for chunk gen
	public static final float PLAYER_SPEED = 10f; //Player speed
	public static float viewDistance = 50f; //The view distance (how far)
	public static float viewClose = 0.01f; //How closely it renders (helps the text not get chopped off)
	public static int WORLDSIZE = 1; //How many chunks in the world
	public static int CHUNKSIZE = 8; //How many blocks a chunk is
	public static final int TextureSize = 16; //The pixel size of a texture (the entire file is 256x256 pixels, but each texture is 16x16 pixels)
	
	
	public static final int initX = 4, initY = 10, initZ = 4;
	public static int chunksLoaded = 0; //How many chunks have been loaded
	public static int chunksFrustum = 0; //How many chunks are in the visible frustum
	
	public static float textSize = 0.35f; //The physical size of the text being rendered on screen
}
