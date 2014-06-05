package com.kyry.voxel.world.chunks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Frustum;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class ChunkManager {
	
	public static HashMap<String, Boolean> chunkMap = new HashMap<String, Boolean>();
	public static HashMap<String, Chunk> activeChunks = new HashMap<String, Chunk>();
	public static HashMap<String, Boolean> activeChunksRender = new HashMap<String, Boolean>();
	public static HashMap<String, Chunk> loadedChunks = new HashMap<String, Chunk>();
	public static HashMap<String, Boolean> loadedChunksRender = new HashMap<String, Boolean>();
	public static Random rand = rand = new Random();
	//public static MobManager mobManager;
	
	private static ShaderProgram shader;
	
	//Constructor
	public ChunkManager(){
		//mobManager = new MobManager();
		//Shader temp = new Shader("/shaders/chunk.vert", "/shaders/chunk.frag");
		//shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}
	//Helper methods
	public static String key(float x, float z){//creates a key for the chunk
		return new String ((int)x + "_" + (int)z);
	}
	public static String key(float x, float y, float z){//creates a key for the chunk
		return new String ((int)x + "_" + y + "_" + (int)z);
	}
	public static String key(Vector2f vec){//creates a key for the chunk
		return new String ((int)vec.getX() + "_" + vec.getY());
	}
	public static int keyX(String s){
		return Integer.parseInt(s.split("_")[0]);
	}
	public static int keyZ(String s){
		return Integer.parseInt(s.split("_")[1]);
	}
//	public static int keyZ(String s){
//		return Integer.parseInt(s.split("_")[2]);
//	}
	
	public static Vector2f blockToChunk(Vector3f v) {
		return new Vector2f(blockToChunk1f(v.getX()),blockToChunk1f(v.getZ()));
//		return new Vector3f(blockToChunk1f(v.x),blockToChunk1f(v.y),blockToChunk1f(v.z));
	}
	public static Vector2f blockToChunk(float x, float z) {
		return new Vector2f(blockToChunk1f(x),blockToChunk1f(z));
//		return new Vector2f(blockToChunk1f(x),blockToChunk1f(y),blockToChunk1f(z));
	}
	public static Vector3f blockToChunk(float x, float y, float z) {
		return new Vector3f(blockToChunk1f(x),blockToChunk1f(y),blockToChunk1f(z));
	}
	public static int blockToChunk1f(float f){
		int i = (int) f;
		if(i < 0){
			i = (int) ((i+1)/16) -1;
		}else if(i >= 0){
			i = (int) (i/16);
		}
		return i;
	}
	public static String filePath(int x, int z){
		return new String("E:\\Save\\" + x + "_" + z + ".dat");
	}
	
	public static boolean isCreated(int x, int z){
		File f = new File(filePath(x,z));
		if(f.exists() && !f.isDirectory()) { 
			return true;
			}
		else 
			return false;
		//return isCreated(key(x, y, z));
		
	}
/*	public static boolean isCreated(int x, int y, int z){
		try {
			FileInputStream saveFile;
			saveFile = new FileInputStream(filePath(x, y, z));
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			short[][][] test = (short[][][])  restore.readObject();
			restore.close();
			return true;
		} catch (IOException | ClassNotFoundException e) {
			//e.printStackTrace();
			return false;
		}
		
		
		//return isCreated(key(x, y, z));
		
	}
*//*	public static boolean isCreated(String s){
		boolean result = false;
		try{
			
			result = chunkMap.get(s);
		}
		catch(Exception e){
		}
		return result;
	}*/
///////////////////////////////////
	/*
	public void readChunk(int x, int y, int z){
		//String s = "" + x + "_" + y + "_"+ z;
		checkChunk(x, y, z );
	}
	
	public void checkChunk(int x, int y, int z){
		if (isCreated(x, y, z)){
			//can't make a chunk thats already made
			//Load the chunk somehow
			//short[][][] blocks = Chunk.loadChunk(x,y,z);
		}else if(!isCreated(x, y, z)){
			//Chunk.createChunk(x,y,z);
			System.out.println("Should have been created");
			chunkMap.put(key(x,y,z), true);
		 //Chunk.createChunk(s); //Create the chunk, then load the chunk somehow
		}
	}*/
	//SAVE / LOAD
	private static void saveChunk(float f, float h, short[][][] blocks) { //Save chunk to data file
		int x = (int) f;
		//int y = (int) g;
		int z = (int) h;
		try {
			File dir = new File(filePath(x, z));
			dir.getParentFile().mkdir();
			FileOutputStream saveFile = new FileOutputStream(filePath(x, z));
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(blocks);
			save.flush();
			save.close();
			System.out.println("(" + x + "," + z + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + x + "," + z + ")");
			e.printStackTrace();
		}

	}

	public static Chunk loadChunkToMem(int x, int z) { //Add chunk from file to memory chunk list
		if (isCreated(x,z)){
			try {
				FileInputStream saveFile = new FileInputStream(filePath(x, z));
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new Vector2f(x,z), (short[][][]) restore.readObject());
				restore.close();
				loadedChunksRender.put(key(x, z), false);
				loadedChunks.put(key(x, z), chunk);
				
				System.out.println("(" + x + "," + z + ") Loaded Successfully.");
				return chunk;
			} catch (Exception e) {
				// Take a second try through, creating the chunk forcefully.
				e.printStackTrace();
				System.out.println("Failed to load");
//				createChunk(x, z);
//				return (loadChunkToMem(x, z));
				
				return null;
			}
		}else{
			createChunk(x, z);
			return (loadChunkToMem(x, z));
		}
	}
	public static Chunk loadChunkToActive(int x, int z) { //Add chunk from memory to active chunk list
		loadedChunksRender.remove(key(x, z));
		activeChunksRender.put(key(x, z), true);
		activeChunks.put(key(x, z), loadedChunks.get(key(x,z)));
		return null;

	}
	public static Chunk removeChunkFromActive(int x, int z) { //Remove chunk from active chunk list
		activeChunksRender.remove(key(x, z));
		loadedChunksRender.put(key(x, z), false);
		activeChunks.remove(key(x, z));
		return null;
		
	}
	public static void createChunk(int f,  int h) {
		int sizeAll = Constants.CHUNKSIZE;
		int worldHeight = Constants.WORLDHEIGHT;
		short[][][] blocks = new short[sizeAll][worldHeight][sizeAll];
		// int internX = (int) Player.camera.getX() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internY = (int) Player.camera.getY() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internZ = (int) Player.camera.getZ() - chunkX *
		// Constants.CHUNKSIZE ;
		/*
		 * if (type == World.AIRCHUNK) { for (int x = 0; x < sizeAll; x++) { for
		 * (int y = 0; y < sizeAll; y++) { for (int z = 0; z < sizeAll; z++) {
		 * blocks[x][y][z] = Block.Air.getId(); } } } } if (type ==
		 * World.MIXEDCHUNK) {
		 */
		for (int x = 0; x < sizeAll; x++) {
			for (int y = 0; y < worldHeight; y++) {
				for (int z = 0; z < sizeAll; z++) {
					blocks[x][y][z] = Block.Grass.getId();
					if (y == 14) {
						// blocks[x][y][z] = Block.Sand.getId();
					} else if ((x == 0) && (z == 0)) {
						blocks[x][y][z] = Block.Air.getId();
					} else if (y == 0) {
						blocks[x][y][z] = Block.Brick.getId();
					} else if (y > 14) {
						blocks[x][y][z] = Block.Air.getId();
					} else if (blocks[x][y][z] == Block.CrackedStone.getId() && (rand.nextInt(7) == 0)) {
						blocks[x][y][z] = Block.CrackedStone.getId();
					} else if (rand.nextInt(2) == 0) {
						if (rand.nextBoolean())
							blocks[x][y][z] = Block.Air.getId();
					} else
						blocks[x][y][z] = Block.CrackedStone.getId();

					/*
					 * if (rand.nextInt(5) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Block.CrackedStone.getId(); if
					 * (rand.nextInt(9) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Block.Brick.getId(); else
					 * blocks[x][y][z] = Block.Glass.getId();
					 */
					try {
						if (blocks[x][y][z] != Block.Air.getId()) {
							// PhysicsWorld.newBlock(x, y, z);
							// CollisionLibrary.newBlock(x,y,z);
							CollisionLibrary.newBlock(f, 0, h, x, y, z);
							/*
							 * Vector3f somePosition = new Vector3f(x,y,z);
							 * CollisionLibrary
							 * .BlockList.get(Constants.BlocksLoaded
							 * ).update(somePosition);
							 */
						}
					} catch (NullPointerException e) {
						System.out.println("Block error in chunk (" + f + "," + h + ")" + " at X: " + x + " Y: " + y + " Z: " + z);
					}
				}
			}
			// }
		}
		// Store data to (chunkX)(chunkY)(chunkZ).dat;
		/*
		 * "("+x+")"+"("+y+")"+"("+z+")"+"=" + blocks[x][y][z].getId()"Like so:
		 * (0)(0)(0)=0; (0)(0)(1)=6; ... (15)(15)(15)=3; .. (x)(y)(z)=blockId;
		 */
		ChunkManager.saveChunk(f, h, blocks);
		/////
		//Seems redundant and unneeded. The createChunk method will not always be called in one instance of the game, loadChunk will be
		//Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new Vector3f(f,g,h), blocks);
		//activeChunks.put(key(f,g,h), chunk);
		System.out.println("(" + f + "," + h + ") Created Successfully.");
	}
	//
	//GETTERS & SETTERS ***To be honest, I don't think the chunks should be easily movable.. that means that every file would have to change according to each move, etc.. :S
	/*public static Chunk getChunk(int x, int y, int z){
		return null;
		
	}
	public static void setChunk(int x, int y, int z){
		
	}*/
	public void update(){
		//Basically will check if chunk is in the "bufferzone" if not then load, chunk, if not then delete
		//DELETE
		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String, Chunk> entry = iterator.next();
			String key = entry.getKey();
			if (isInZone(key)) {
			}
			else{
				//removes chunk
				removeChunk(key);
				iterator.remove();
			}
		}//end while for iterator
		//ADD
		//BLOCK RELATIVE
		Vector2f pos = blockToChunk(Player.camera.getPos());
		for (int x = (int) (pos.getX()-Constants.WORLDRADIUS); x <= (int)(pos.getX() + Constants.WORLDRADIUS); x++) {
			//for (int y = (int) (pos.y - Constants.WORLDRADIUS); y <= (int)(pos.y + Constants.WORLDRADIUS); y++) {
				for (int z = (int) (pos.getY() - Constants.WORLDRADIUS); z <= (int)(pos.getY() + Constants.WORLDRADIUS); z++) {
					String key = key(x, z);
					if(activeChunks.containsKey(key)){
						//leaveHimAlone! (no buffer needed)
					}else{
						loadedChunksRender.remove(key(x, z));
						activeChunksRender.put(key(x, z), true);
						activeChunks.put(key, loadedChunks.get(key)); //Add the chunk to loaded buffer
						//loadChunkToMem(x, y, z); 
					}
				}//end for z
			//}//end for y
		}//end for x
		
	}//End Update()
	
	private void removeChunk(String key) {//remove chunk from.. current set and Collision zone?
		//activeChunks.remove(key);//removes chunk//causes ERROR!
		//remove the collision blocks
		for (int x = 0; x < Constants.CHUNKSIZE; x++) {
			for (int y = 0; y < Constants.WORLDHEIGHT; y++) {
				for (int z = 0; z < Constants.CHUNKSIZE; z++) {
					CollisionLibrary.removeBlock(keyX(key), keyZ(key), x, y, z);
				}//end for z
			}//end for y
		}//end for x
	}
	private boolean isInZone(String key) {
		boolean result = false;
		int x = keyX(key);
		//int y = keyY(key); 
		int z = keyZ(key);
		//chunk relative position of player
		Vector2f playerPos = blockToChunk(Player.camera.getX(),  Player.camera.getZ()); //Player.camera.getY(),
		if(x <= (playerPos.getX() + Constants.WORLDRADIUS) && x >= (playerPos.getX() - Constants.WORLDRADIUS)){
			//if(y <= (playerPos.getY() + Constants.WORLDRADIUS) && y >= (playerPos.getY() - Constants.WORLDRADIUS)){
				if(z <= (playerPos.getY() + Constants.WORLDRADIUS) && z >= (playerPos.getY()- Constants.WORLDRADIUS)){
					result = true;
				}
			//}
		}
		return result;
	}
	public void render() {
		//String key = ChunkManager.key(x, y, z);
//		for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
//		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		}
		
		
		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String, Chunk> entry = iterator.next();
			if (Frustum.getFrustum().cubeFullyInFrustum(
					entry.getValue().getPos().getX(),
					0,//entry.getValue().getPos().getY(),
					entry.getValue().getPos().getY(),
					entry.getValue().getPos().getX() + Constants.CHUNKSIZE,
					0,//entry.getValue().getPos().getY() + Constants.CHUNKSIZE,
					entry.getValue().getPos().getY() + Constants.CHUNKSIZE)) {
				if (Math.abs(entry.getValue().getCenter().getX()
						- (int) Player.camera.getX()) < 64
//						&& Math.abs(entry.getValue().getCenter().getZ()
//								- Player.camera.getZ()) < 64
						&& Math.abs(entry.getValue().getCenter().getY()
								- Player.camera.getY()) < 32) {
					Constants.chunksFrustum++;
					entry.getValue().render();

				}
			}
		}//end while for iterator
	}//end render
}
