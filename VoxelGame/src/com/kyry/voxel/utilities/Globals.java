package com.kyry.voxel.utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

/**
 * @author Justin Kyryliuk & Eliel Zamora
 * Definitions of globals used throughout the program
 */
public class Globals {
	/*Window Height & Width */
	public static final int WIDTH = 1280; // Window Width
	public static final int HEIGHT = 720; // Window Height
	/* Full Screen status (Default false) */
	public static boolean fullscreen = false;
	/* Current FPS */
	public static float FPS = 30;
	/* Instantiate a new 'Random' object for chunk gen */
	public static Random rand = new Random();
	/* Player speed when flying */
	public static  float PLAYER_SPEED = 10f;
	/* The view distance (how far) */
	public static float viewDistance = 50f;
	/* How closely it renders (helps the text not get chopped off) */
	public static float viewClose = 0.01f;
	
	/*How far over the chunks are being rendered */
	public static int WORLDRADIUS = 1;
	/* How many blocks wide+long a chunk is so (CHUNKSIZE^2)*WORLDHEIGHT = # of blocks in chunk. (ie 16x16x32) */
	public static int CHUNKSIZE = 16;
	/* The World Height (aka chunk height) so (CHUNKSIZE^2)*WORLDHEIGHT = # of blocks in chunk. (ie 16x16x32) */
	public static int WORLDHEIGHT = 64; 
	/* Players height (distance between the center's of two playerSphere's */
	public static float playerHeight = 1.0f;
	/* The pixel size of a texture (the entire spritesheet is 256x256 pixels, but each texture is 16x16 pixels) */
	public static final int TextureSize = 16;
	/* Fog state (Default true) */
	public static boolean fogEnabled = true;
	/* How close the fog begins */
	public static float fogNear = 5.5f;
	/* How far until fog becomes too thick to see through */
	public static float fogFar = 12.4f;
	/* How dense the fog is */
	public static float fogDensity = 0.01f;
	/* Fog colour */
	public static Color fogColor = new Color(0.3f, 0.6f, 0.8f, 1f);
	/* The constant downwards acceleration */
	public static float gravity = 0.016f * (Globals.FPS);
	/* The power of the jump */
	public static float jumpPower = 2.5f;
	/* The length of the jump */
	public static float jumpFrames = 0.15f;
	/* The jump count */
	public static float jumpCounter = 0f;
	/* initial position of the player */
	public static final Vector3f initPos = new Vector3f(4f, 50f, 4f);
	/* Maximum speed of the player */
	public static float maxSpeed = 7f;
	/* Store the player's current speed */
	public static Vector3f playerSpeed = new Vector3f(0f, 0f, 0f);
	/* The acceleration of players motion */
	public static Vector3f playerMove = new Vector3f(0f, 0f, 0f);
	/* The player's previous position */
	public static Vector3f playerPrevPos = new Vector3f(initPos.x, initPos.y,initPos.z);
	/* The player's current position */
	public static Vector3f playerPos = new Vector3f(initPos.x, initPos.y, initPos.z);
	/* The player's current chunk position */
	public static Vector3f playerChunkPos = new Vector3f(initPos.x / CHUNKSIZE, initPos.y/CHUNKSIZE, initPos.z/CHUNKSIZE);
	/* The player's previous chunk position (last cycle) */
	public static Vector3f playerPrevChunkPos = new Vector3f(initPos.x / CHUNKSIZE, initPos.y/CHUNKSIZE, initPos.z/CHUNKSIZE);
	
	/* Current Ray */
	public static Vector3f ray = new Vector3f(0f, 0f, 0f);
	/* Ray constant used in ray calculations */
	public static float rayConstant = 5f;
	/* How far away the raycasting will check */
	public static int rayDistance = 5;
	/* The chunks in queue to be saved */
	public static List<String> chunkToSave = new LinkedList<String>();
	/* How many chunks have been loaded */
	public static int chunksLoaded = 0;
	/* How many chunks are active */
	public static int chunksActive = 0;
	/* How many chunks are in the visible (in the visible frustum) */
	public static int chunksFrustum = 0;
	/* Number of collision blocks loaded, not 100% accurate */
	public static int PhysBlocksLoaded = 0;
	/* Number of rendered blocks loaded, not 100% accurate */
	public static int RenderBlocksLoaded = 0;
	/* The size of the text being rendered on screen */
	public static float textSize = 0.35f;
	/* Default assume you can jump */
	public static boolean jumpEnabled = true;
	/* Toggle Color Black/White for text being rendered on screen */
	public static Color textColor = Color.white;
	/* The location of the block that could be added */
	public static Vector3f blockToAdd = new Vector3f(0, 0, 0);
	/* The location of the block currently selected, possibly removed */
	public static Vector3f selectedBlock = new Vector3f(0, 0, 0);
	/* The selected block that could be placed, default is '4' (Dirt) */
	public static byte blockToAddType = 4;
	/* noClip status (Default false)
	 * noClip in the terms of being clipped to the world and affected by gravity/physics
	 */
	public static boolean noClip = false;
	/* render HUD text (Default true) */
	public static boolean  renderHUDText = true;
}
