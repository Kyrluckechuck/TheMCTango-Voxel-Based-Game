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
	
	public static void createChunk(int x, int y, int z){
		String s = "" + x + "_" + y + "_"+ z;
		createChunk(s);
	}
	
	public static void createChunk(String s){
		if (isCreated(s)){
			//cant make a chunck thats already made
		}else if(!isCreated(s)){
			ChunkMap.put(s, true);
		}
	}
	
	//GETTERS & SETTERS
	public static Chunk getChunk(int x, int y, int z){
		return null;
		
	}
	public static void setChunk(int x, int y, int z){
		
	}
}
