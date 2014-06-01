package com.kyry.voxel.utilites;

import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.world.physics.CollisionLibrary;

public class CollisionTest {
	   public static void main(String[] args) {
	      Sphere sphere = new Sphere(5);
	      AABB box = new AABB(0,0,0,10,1,5, 1);
	      Vector3f somePosition = new Vector3f();
	      somePosition.x = 10;
	      somePosition.y = 5;
	      
	      // make sure to update the position of the colliders before testing for them
	      box.update(somePosition);
	      //sphere.update(somePosition);
	      
	      // test for overlap
	      if(CollisionLibrary.testCircleAABB(sphere,box)) {
	         System.out.println("COLLIDED");
	      }
	      else {
	    	  System.out.println("NO COLLIDED");
	      }
	   }
	}
