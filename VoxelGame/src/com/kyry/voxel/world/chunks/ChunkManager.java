package com.kyry.voxel.world.chunks;

import java.util.HashMap;

public class ChunkManager {
	public static HashMap<String, Boolean> ChunkMap = new HashMap<String, Boolean>();
	
	public static boolean isCreated(int x, int y, int z){
		return isCreated(x + "_" + y + "_" + z);
	}
	
	public static boolean isCreated(String s){
		boolean result = false;
		try{
			result = ChunkMap.get(s);
		}
		catch(Exception e){
		}
		return result;
	}
	
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
			ChunkMap.put(x + "_" + y + "_" + z, true);
		 //Chunk.createChunk(s); //Create the chunk, then load the chunk somehow
		}
	}
	
	//GETTERS & SETTERS ***To be honest, I don't think the chunks should be easily movable.. that means that every file would have to change according to each move, etc.. :S
	public static Chunk getChunk(int x, int y, int z){
		return null;
		
	}
	public static void setChunk(int x, int y, int z){
		
	}
}
