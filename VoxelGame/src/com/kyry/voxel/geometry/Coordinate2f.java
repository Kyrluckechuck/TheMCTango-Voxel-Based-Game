package com.kyry.voxel.geometry;

import org.lwjgl.util.vector.Vector2f;

@SuppressWarnings("serial")
public class Coordinate2f extends Vector2f {
	// class that is like vector except has an extra method called key
	// that gives a key we can use for hashmaps
	public Coordinate2f(float x, float y) {
		super(x, y);
	}

	public Coordinate2f(String key) {
		super(Float.parseFloat(key.split("_")[0]), Float.parseFloat(key
				.split("_")[1]));
	}

	public String key() {
		return new String((int) this.x + "_" + (int) this.y);
	}

	// converts to chunk coordinate ie (18,19,20) -> (1,1,1)
	// since it has no y we can just ignore it, or return a coordinate2f instead
	public Coordinate2f toChunk() {
		return new Coordinate2f(blockToChunk1f(x), blockToChunk1f(y));
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
	/*
	 * public Coordinate2f remainder(){ //gets the x,y,z values that would go in
	 * the box array of // a chunk Coordinate2f original = this; Coordinate2f
	 * chunkCoord = this.floor(); return new Coordinate2f(original.x -
	 * chunkCoord.x, original.y - chunkCoord.y); }
	 */
}