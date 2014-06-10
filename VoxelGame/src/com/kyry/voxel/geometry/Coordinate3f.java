package com.kyry.voxel.geometry;

import org.lwjgl.util.vector.Vector3f;

@SuppressWarnings("serial")
public class Coordinate3f extends Vector3f {
	//class that is like vector except has an extra method called key
		//that gives a key we can use for hashmaps
	public Coordinate3f(Vector3f v){
		super(v.x, v.y, v.z);
	}
	public Coordinate3f(float x, float y, float z){
		super(x, y, z);
	}
	public Coordinate3f(String key){
		super(	Float.parseFloat(key.split("_")[0]),
				Float.parseFloat(key.split("_")[1]),
				Float.parseFloat(key.split("_")[2]));
	}
	public String key(){
		return new String ((int)this.x + "_" + (int)this.y + "_" + (int)this.z);
	}
	//converts to chunk coordinate ie (18,19,20) -> (1,1,1) 
	//since it has no y we can just ignore it, or return a coordinate2f instead
	public Coordinate3f toChunk(){
		return new Coordinate3f(blockToChunk1f(x), blockToChunk1f(y), blockToChunk1f(z));
	}
	public static int blockToChunk1f(float f) {
		int i = (int) f;
		if (i < 0) {
			i = ((int) ((i + 1) / 16)) - 1;
		} else if (i >= 0) {
			i = (int) (i / 16);
		}
		return (int) i;
	}
	//
	
	/*public Coordinate3f remainder(){
		//gets the x,y,z values that would go in the box array of
		// a chunk
		Coordinate3f original = this;
		Coordinate3f chunkCoord  = this.floor();
		return new Coordinate3f(original.x - chunkCoord.x, original.y - chunkCoord.y, original.z - chunkCoord.z);
	}*/
}