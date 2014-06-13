package com.kyry.voxel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.util.vector.Vector2f;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.entities.mobs.Player;

public class BackgroundProcessing extends Thread{
    public void run() {
        System.out.println("Hello from a thread!");
        ConcurrentLinkedQueue<Chunk> toRemove;
//        Producer(ConcurrentLinkedQueue<Chunk> toRemove){
//           this.toRemove = toRemove;
//        }
        for(;;){
        update();
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    }
	public void update() {
		// Basically will check if chunk is in the "bufferzone" if not then
		// load, chunk, if not then delete
		// DELETE
		ArrayList<String> toRemove = new ArrayList<String>();
		Iterator<Entry<String, Chunk>> iterator = ChunkManager.activeChunks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Chunk> entry = iterator.next();
			String key = entry.getKey();
			if (!ChunkManager.isInZone(key)) { // checks if the chunk is in the render-zone
				toRemove.add(key);// end chunks to be removed
			} // end if
		}// end remove while

		// Remove Unused Chunks
		for (int q = 0; q < toRemove.size(); q++) {
			ChunkManager.removeChunkFromActive(toRemove.get(q));
		}

		// End Removal Of Unused Chunks
		// ADD
		// BLOCK RELATIVE
		Vector2f blockPos = ChunkManager.blockToChunk(Player.camera.getPos()); // Returns
																	// player's
																	// XZ Chunk
																	// coords
		for (int x = (int) (blockPos.getX() - Globals.WORLDRADIUS); x <= (int) (blockPos.getX() + Globals.WORLDRADIUS); x++) {
			for (int z = (int) (blockPos.getY() - Globals.WORLDRADIUS); z <= (int) (blockPos.getY() + Globals.WORLDRADIUS); z++) {
				String key = ChunkManager.key(x, z);
				if (!ChunkManager.activeChunks.containsKey(key)) {
					ChunkManager.loadChunkToActive(x, z);
					// Chunk to loaded buffer
				}
			}// end for z
		}// end for x
		/*
		 * iterate through queue
		 */
	}// End Update()
}
