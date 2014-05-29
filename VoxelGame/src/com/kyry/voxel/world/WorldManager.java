package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Frustum;
import com.kyry.voxel.utilites.Spritesheet;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {

	MobManager mobManager;
	public static Sphere playerSphere = new Sphere(0.5f);;
	//PhysicsWorld physicsWorld;
	
	public ArrayList<Chunk> loadedChunks;
	public ArrayList<Chunk> activeChunks;

	private ShaderProgram shader;

	public WorldManager() {
		initGL();
		init();
		createWorld();
	}

	private void initGL() {
		Shader temp = new Shader("/shaders/chunk.vert", "/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}

	private void init() {
		//physicsWorld = new PhysicsWorld();
		mobManager = new MobManager();
		loadedChunks = new ArrayList<Chunk>();
		activeChunks = new ArrayList<Chunk>();
	}

	private void createWorld() {
		for (int x = 0; x < Constants.WORLDSIZE; x++) {
			for (int y = 0; y < Constants.WORLDSIZE; y++) {
				for (int z = 0; z < Constants.WORLDSIZE; z++) {
					activeChunks.add(new Chunk(shader, 1, x
							* Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z
							* Constants.CHUNKSIZE));
					/*
					 * activeChunks.add(new Chunk(shader, 1, 2*x
					 * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, 2*z
					 * Constants.CHUNKSIZE));
					 */
					Constants.chunksLoaded++;
				}
			}
		}
		//saveChunksTemp();
	}

	private void saveChunksTemp() {
		for (int i = 0; i < activeChunks.size(); i++) {
			saveChunk(activeChunks.get(i));
		}
	}

	public void update() {
		mobManager.update();

	}

	public void render() {
//		float groundDim = 10000f;
//		{
//			glBegin(GL_QUADS);
//			glColor4f(0.5f, 0.5f, 0.5f, 1);
//			glVertex4f(-groundDim, 0, -groundDim, 1);
//			glColor4f(0.5f, 0.5f, 0.5f, 1);
//			glVertex4f(-groundDim, 0, +groundDim, 1);
//			glColor4f(0.5f, 0.5f, 0.5f, 1);
//			glVertex4f(+groundDim, 0, +groundDim, 1);
//			glColor4f(0.5f, 0.5f, 0.5f, 1);
//			glVertex4f(+groundDim, 0, -groundDim, 1);
//			glEnd();
//		}
		// ////////
		Constants.chunksFrustum = 0;
		Spritesheet.tiles.bind();
	/*	GL11.glBegin(GL11.GL_QUADS);
		Vector3f playerPos= PhysicsWorld.playerBody.getWorldTransform(PhysicsWorld.DEFAULT_TRANSFORM).origin;
		Shape.createCube(playerPos.x, playerPos.y + 0.2f, playerPos.z,
				Tile.getTile(Tile.Wireframe.getId()).getColor(),
				Tile.getTile(Tile.Wireframe.getId()).getTexCoords(), 1f);
		GL11.glEnd();*/

		// get vector from physics()

		//

		for (int i = 0; i < activeChunks.size(); i++) {
			if (Frustum.getFrustum().cubeInFrustum(
					activeChunks.get(i).getPos().getX(),
					activeChunks.get(i).getPos().getY(),
					activeChunks.get(i).getPos().getZ(),
					activeChunks.get(i).getPos().getX() + Constants.CHUNKSIZE,
					activeChunks.get(i).getPos().getY() + Constants.CHUNKSIZE,
					activeChunks.get(i).getPos().getZ() + Constants.CHUNKSIZE)) {
				if (Math.abs(activeChunks.get(i).getCenter().getX()
						- (int) mobManager.getPlayer().getX()) < 64
						&& Math.abs(activeChunks.get(i).getCenter().getZ()
								- mobManager.getPlayer().getZ()) < 64
						&& Math.abs(activeChunks.get(i).getCenter().getY()
								- mobManager.getPlayer().getY()) < 32) {
					Constants.chunksFrustum++;
					activeChunks.get(i).render();

				}
			}
			
		}
		mobManager.render();
	}// end render

	public void skyBoxRender() {
		WorldRender.render();
		
	}
	
	public void logic() {
		// Reset the model-view matrix.

		// glLoadIdentity();
		getMobManager().getPlayer().getCamera().applyTranslations();
		/*Vector3f playerPosition = getMobManager().getPlayer().getCamera()
				.getPosition();
		Transform transform = new Transform(new Matrix4f(physicsWorld.DEFAULT_QUAT, playerPosition, 1.0f));
		physicsWorld.playerBody.setWorldTransform(transform);
		physicsWorld.step(1 / Constants.FPS);
		playerPosition = physicsWorld.playerBody
				.getWorldTransform(new Transform()).origin;
		getMobManager().getPlayer().getCamera().applyPhysics(playerPosition);*/

		/*
		 * // If the attraction between the green ball and the camera is enabled
		 * // ... if (applyForce) { // Retrieve the controllable ball's
		 * location. Transform controlBallTransform = new Transform();
		 * controlBall.getMotionState()
		 * .getWorldTransform(controlBallTransform); Vector3f
		 * controlBallLocation = controlBallTransform.origin; Vector3f
		 * cameraPosition = new Vector3f(camera.x(), camera.y(), camera.z()); //
		 * Calculate the force that is applied to the controllable ball as //
		 * following: // force = cameraPosition - controlBallLocation Vector3f
		 * force = new Vector3f(); force.sub(cameraPosition,
		 * controlBallLocation); // Wake the controllable ball if it is
		 * sleeping. controlBall.activate(true); // Apply the force to the
		 * controllable ball. controlBall.applyCentralForce(force); }
		 */
		/*
		 * // If a new shape should be created ... if (createNewShape) { //
		 * Create the collision shape (sphere with radius of 3 metres).
		 * CollisionShape shape = new SphereShape(3); // Create the motion state
		 * (x and z are the same as the camera's). DefaultMotionState
		 * motionState = new DefaultMotionState( new Transform(new Matrix4f(new
		 * Quat4f(0, 0, 0, 1), new Vector3f(camera.x(), 35, camera.z()),
		 * 1.0f))); // Calculate the inertia (resistance to movement) using the
		 * ball's // mass of 1 kilogram. Vector3f inertia = new Vector3f(0, 0,
		 * 0); shape.calculateLocalInertia(1.0f, inertia);
		 * RigidBodyConstructionInfo constructionInfo = new
		 * RigidBodyConstructionInfo( 1, motionState, shape, inertia);
		 * constructionInfo.restitution = 0.75f; RigidBody rigidBody = new
		 * RigidBody(constructionInfo); balls.add(rigidBody);
		 * dynamicsWorld.addRigidBody(rigidBody); createNewShape = false; } //
		 * If the controllable ball's position and orientation should be reset
		 * // ... if (resetControlBall) { // Set the position of the ball to (0,
		 * 50, 0). controlBall.setCenterOfMassTransform(new Transform(new
		 * Matrix4f( new Quat4f(0, 0, 0, 1), new Vector3f(0, 50, 0), 1.0f))); //
		 * Reset the angular velocity (spinning movement) of the ball.
		 * controlBall.setAngularVelocity(new Vector3f(0, 0, 0)); // Reset the
		 * linear velocity (x,y,z movement) of the ball.
		 * controlBall.setLinearVelocity(new Vector3f(0, 0, 0));
		 * resetControlBall = false; }
		 */
	}

	public void saveChunk(Chunk c) {
//		BufferedWriter bw = null;
//		try {
//			bw = new BufferedWriter(new FileWriter("/000chunk.dat"));
//			bw.write(c.getPos().getX() + " " + c.getPos().getY() + " "
//					+ c.getPos().getZ());
//			for (int x = (int) c.getPos().getX(); x < (int) c.getPos().getX()
//					+ Constants.CHUNKSIZE; x++) {
//				for (int y = (int) c.getPos().getY(); y < (int) c.getPos()
//						.getY() + Constants.CHUNKSIZE; y++) {
//					for (int z = (int) c.getPos().getZ(); z < (int) c.getPos()
//							.getZ() + Constants.CHUNKSIZE; z++) {
//						bw.write(String.valueOf(c.getTileID(x, y, z)));
//					}
//				}
//			}
//			bw.close();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}

	}

	/*
	 * private void loadChunksTemp() { for (int i = 0; i < activeChunks.size();
	 * i++) { loadChunk(activeChunks.get(i)); } }
	 * 
	 * public void loadChunk(Chunk c) { BufferedReader br = null; String[]
	 * tokens; String line; int sdat, length; try { br = new BufferedReader(new
	 * FileReader("/000chunk.dat")); line = br.readLine(); if (line == null) {
	 * break; } else { tokens = line.split(""); length = tokens.length; sdat =
	 * Integer.parseInt(tokens [0]);
	 * 
	 * br.write(c.getPos().getX() + " " + c.getPos().getY() + " " +
	 * c.getPos().getZ()); for (int x = (int) c.getPos().getX(); x < (int)
	 * c.getPos().getX() + Constants.CHUNKSIZE; x++) { for (int y = (int)
	 * c.getPos().getY(); y < (int) c.getPos().getY() + Constants.CHUNKSIZE;
	 * y++) { for (int z = (int) c.getPos().getZ(); z < (int) c.getPos().getZ()
	 * + Constants.CHUNKSIZE; z++) { br.write(String.valueOf(c.getTileID(x, y,
	 * z))); } } }
	 * 
	 * br.close(); } } catch (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	public MobManager getMobManager() {
		return mobManager;
	}



}
