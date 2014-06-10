package com.kyry.voxel.utilities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class Constants {

	public static final int WIDTH = 1280; // Window Width
	public static final int HEIGHT = 720; // Window Height
	public static float FPS = 30; // not a constant I know but it will work :)
	public static String state = "EXPLORE"; // Default to game state for now

	public static Random rand = new Random(); // Instantiate a new 'Random'
												// object for chunk gen
	public static  float PLAYER_SPEED = 10f; // Player speed
	public static float viewDistance = 50f; // The view distance (how far)
	public static float viewClose = 0.01f; // How closely it renders (helps the
											// text not get chopped off)
	public static int WORLDRADIUS = 1; // How far over the chunks are being rendered *fix
	public static int CHUNKSIZE = 16; // How many blocks wide+long a chunk is so (CHUNKSIZE^2)*WORLDHEIGHT = # of blocks in chunk. (ie 16x16x32)
	public static int WORLDHEIGHT = 128; // The World Height (aka chunk height) so (CHUNKSIZE^2)*WORLDHEIGHT = # of blocks in chunk. (ie 16x16x32)
	public static float playerHeight = 0.8f; // Players height (distance between center's of two playerSphere's
	public static final int TextureSize = 16; // The pixel size of a texture
												// (the entire file is 256x256
												// pixels, but each texture is
												// 16x16 pixels)

	public static float fogNear = 6.5f; // How close the fog begins
	public static float fogFar = 9.4f; // How far until fog becomes too thick to
										// see through
	public static Color fogColor = new Color(0.3f, 0.6f, 0.8f, 1f); // Default
																	// fog
																	// colour
																	// style
	public static float gravity = 0.002f * (Constants.FPS);// is the
															// acceleration
	public static float jumpPower = 1;
	public static float jumpFrames = 1;
	public static float jumpCounter = 0;
	// initial position of the player
	public static final Vector3f initPos = new Vector3f(4f, 14f, 4f);
	// Last center chunk that was used for the rendering system //to be finished
	public static final Vector3f lastRenderChunk = new Vector3f(0f, 0f, 0f);
	// store the speed of the player
	public static Vector3f playerSpeed = new Vector3f(0f, 0f, 0f);
	// the acceleration of players motion
	public static Vector3f playerMove = new Vector3f(0f, 0f, 0f);
	// Previous Position
	public static Vector3f playerPrevPos = new Vector3f(initPos.x, initPos.y,
			initPos.z);
	//player's current position
	public static Vector3f playerPos = new Vector3f(initPos.x, initPos.y,
			initPos.z);
	//player's current chunk position
	public static Vector3f playerChunkPos = new Vector3f(initPos.x / CHUNKSIZE, initPos.y/CHUNKSIZE,
			initPos.z/CHUNKSIZE);
	//player's previous chunk position (last cycle)
	public static Vector3f playerPrevChunkPos = new Vector3f(initPos.x / CHUNKSIZE, initPos.y/CHUNKSIZE,
			initPos.z/CHUNKSIZE);
	
	// ray
	public static Vector3f ray = new Vector3f(0f, 0f, 0f);
	public static float rayConstant = 5f;
	public static int rayDistance = (int) viewDistance;
	
	public static int chunksLoaded = 0; // How many chunks have been loaded
	public static int chunksActive = 0;  // How many chunks are active
	public static int chunksFrustum = 0; // How many chunks are in the visible
											// frustum
	public static int PhysBlocksLoaded = -1; // Number of collision blocks loaded, default to
											// -1 so that when the first one is
											// made, it becomes '0'
	public static int RenderBlocksLoaded = -1; // Number of rendered blocks loaded, default to
	// -1 so that when the first one is
	// made, it becomes '0'

	public static float textSize = 0.35f; // The physical size of the text being
											// rendered on screen
	public static boolean jumpEnabled = true; // Default assume you can jump.

	public static Color textColor = Color.white; // Toggle Color Black/White for text being
    // rendered on screen
	
	
	
}
