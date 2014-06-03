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

import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Frustum;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class ChunkManager {
	
	public static HashMap<String, Boolean> chunkMap = new HashMap<String, Boolean>();
	public static HashMap<String, Chunk> activeChunks = new HashMap<String, Chunk>();
	public static HashMap<String, Chunk> loadedChunks = new HashMap<String, Chunk>();
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
	public static String key(float x, float y, float z){//creates a key for the chunk
		return new String ((int)x + "_" + (int)y + "_" + (int)z);
	}
	public static int keyX(String s){
		return Integer.parseInt(s.split("_")[0]);
	}
	public static int keyY(String s){
		return Integer.parseInt(s.split("_")[1]);
	}
	public static int keyZ(String s){
		return Integer.parseInt(s.split("_")[2]);
	}
	
	public static Vector3f blockToChunk(Vector3f v) {
		return new Vector3f(blockToChunk1f(v.x),blockToChunk1f(v.y),blockToChunk1f(v.z));
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
	public static String filePath(int x, int y, int z){
		return new String("E:\\Save\\" + x + "_" + y + "_" + z + ".dat");
	}
	
	public static boolean isCreated(int x, int y, int z){
		File f = new File(filePath(x,y,z));
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
	private static void saveChunk(float f, float g, float h, short[][][] blocks) { //Save chunk to data file
		int x = (int) f;
		int y = (int) g;
		int z = (int) h;
		try {
			File dir = new File(filePath(x, y, z));
			dir.getParentFile().mkdir();
			FileOutputStream saveFile = new FileOutputStream(filePath(x, y, z));
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(blocks);
			save.flush();
			save.close();
			System.out.println("(" + x + "," + y + "," + z + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + x + "," + y + "," + z + ")");
			e.printStackTrace();
		}

	}

	public static Chunk loadChunkToMem(int x, int y, int z) { //Add chunk from file to memory chunk list
		if (isCreated(x,y,z)){
			try {
				FileInputStream saveFile = new FileInputStream(filePath(x, y, z));
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new Vector3f(x,y,z), (short[][][]) restore.readObject());
				restore.close();
				loadedChunks.put(key(x, y, z), chunk);
				System.out.println("(" + x + "," + y + "," + z + ") Loaded Successfully.");
				return chunk;
			} catch (Exception e) {
				// Take a second try through, creating the chunk forcefully.
				//createChunk(x, y, z);
				//return (loadChunk(x, y, z));
				return null;
			}
		}else{
			createChunk(x, y, z);
			return (loadChunkToMem(x, y, z));
		}
	}
	public static Chunk loadChunkToActive(int x, int y, int z) { //Add chunk from memory to active chunk list

		activeChunks.put(key(x, y, z), loadedChunks.get(key(x,y,z)));
		return null;

	}
	public static Chunk removeChunkFromActive(int x, int y, int z) { //Remove chunk from active chunk list
		
		activeChunks.remove(key(x, y, z));
		return null;
		
	}
	public static void createChunk(int f, int g, int h) {
		int sizeAll = Constants.CHUNKSIZE;
		short[][][] blocks = new short[sizeAll][sizeAll][sizeAll];
		// int internX = (int) Player.camera.getX() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internY = (int) Player.camera.getY() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internZ = (int) Player.camera.getZ() - chunkX *
		// Constants.CHUNKSIZE ;
		/*
		 * if (type == World.AIRCHUNK) { for (int x = 0; x < sizeAll; x++) { for
		 * (int y = 0; y < sizeAll; y++) { for (int z = 0; z < sizeAll; z++) {
		 * blocks[x][y][z] = Tile.Air.getId(); } } } } if (type ==
		 * World.MIXEDCHUNK) {
		 */
		for (int x = 0; x < sizeAll; x++) {
			for (int y = 0; y < sizeAll; y++) {
				for (int z = 0; z < sizeAll; z++) {
					blocks[x][y][z] = Tile.Grass.getId();
					if (y == 14) {
						// blocks[x][y][z] = Tile.Sand.getId();
					} else if ((x == 0) && (z == 0)) {
						blocks[x][y][z] = Tile.Air.getId();
					} else if (y == 0) {
						blocks[x][y][z] = Tile.Brick.getId();
					} else if (y > 14) {
						blocks[x][y][z] = Tile.Air.getId();
					} else if (blocks[x][y][z] == Tile.CrackedStone.getId() && (rand.nextInt(7) == 0)) {
						blocks[x][y][z] = Tile.CrackedStone.getId();
					} else if (rand.nextInt(2) == 0) {
						if (rand.nextBoolean())
							blocks[x][y][z] = Tile.Air.getId();
					} else
						blocks[x][y][z] = Tile.CrackedStone.getId();

					/*
					 * if (rand.nextInt(5) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Tile.CrackedStone.getId(); if
					 * (rand.nextInt(9) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Tile.Brick.getId(); else
					 * blocks[x][y][z] = Tile.Glass.getId();
					 */
					try {
						if (blocks[x][y][z] != Tile.Air.getId()) {
							// PhysicsWorld.newBlock(x, y, z);
							// CollisionLibrary.newBlock(x,y,z);
							CollisionLibrary.newBlock(f, g, h, x, y, z);
							/*
							 * Vector3f somePosition = new Vector3f(x,y,z);
							 * CollisionLibrary
							 * .BlockList.get(Constants.BlocksLoaded
							 * ).update(somePosition);
							 */
						}
					} catch (NullPointerException e) {
						System.out.println("Block error in chunk (" + f + "," + g + "," + h + ")" + " at X: " + x + " Y: " + y + " Z: " + z);
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
		ChunkManager.saveChunk(f, g, h, blocks);
		/////
		//Seems redundant and unneeded. The createChunk method will not always be called in one instance of the game, loadChunk will be
		//Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new Vector3f(f,g,h), blocks);
		//activeChunks.put(key(f,g,h), chunk);
		System.out.println("(" + f + "," + g + "," + h + ") Created Successfully.");
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
		Vector3f pos = blockToChunk(Player.camera.getPos());
		for (int x = (int) (pos.x-Constants.WORLDRADIUS); x <= (int)(pos.x + Constants.WORLDRADIUS); x++) {
			for (int y = (int) (pos.y - Constants.WORLDRADIUS); y <= (int)(pos.y + Constants.WORLDRADIUS); y++) {
				for (int z = (int) (pos.z - Constants.WORLDRADIUS); z <= (int)(pos.z + Constants.WORLDRADIUS); z++) {
					String key = key(x, y, z);
					if(activeChunks.containsKey(key)){
						//leaveHimAlone! (no buffer needed)
					}else{
						activeChunks.put(key, loadedChunks.get(key)); //Add the chunk to loaded buffer
						//loadChunkToMem(x, y, z); 
					}
				}//end for z
			}//end for y
		}//end for x
		
	}//End Update()
	
	private void removeChunk(String key) {//remove chunk from.. current set and Collision zone?
		//activeChunks.remove(key);//removes chunk//causes ERROR!
		//remove the collision blocks
		for (int x = 0; x < Constants.CHUNKSIZE; x++) {
			for (int y = 0; y < Constants.CHUNKSIZE; y++) {
				for (int z = 0; z < Constants.CHUNKSIZE; z++) {
					CollisionLibrary.removeBlock(keyX(key), keyY(key), keyZ(key), x, y, z);
				}//end for z
			}//end for y
		}//end for x
	}
	private boolean isInZone(String key) {
		boolean result = false;
		int x = keyX(key);
		int y = keyY(key);
		int z = keyZ(key);
		//chunk relative position of player
		Vector3f playerPos = blockToChunk(Player.camera.getX(), Player.camera.getX(), Player.camera.getX());
		if(x <= (playerPos.x + Constants.WORLDRADIUS) && x >= (playerPos.x - Constants.WORLDRADIUS)){
			if(y <= (playerPos.y + Constants.WORLDRADIUS) && y >= (playerPos.y - Constants.WORLDRADIUS)){
				if(z <= (playerPos.z + Constants.WORLDRADIUS) && z >= (playerPos.z- Constants.WORLDRADIUS)){
					result = true;
				}
			}
		}
		return result;
	}
	public void render() {
		//String key = ChunkManager.key(x, y, z);
 
		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String, Chunk> entry = iterator.next();
			if (Frustum.getFrustum().cubeInFrustum(
					entry.getValue().getPos().getX(),
					entry.getValue().getPos().getY(),
					entry.getValue().getPos().getZ(),
					entry.getValue().getPos().getX() + Constants.CHUNKSIZE,
					entry.getValue().getPos().getY() + Constants.CHUNKSIZE,
					entry.getValue().getPos().getZ() + Constants.CHUNKSIZE)) {
				if (Math.abs(entry.getValue().getCenter().getX()
						- (int) Player.camera.getX()) < 64
						&& Math.abs(entry.getValue().getCenter().getZ()
								- Player.camera.getZ()) < 64
						&& Math.abs(entry.getValue().getCenter().getY()
								- Player.camera.getY()) < 32) {
					Constants.chunksFrustum++;
					entry.getValue().render();

				}
			}
		}//end while for iterator
	}//end render
}
