package com.kyry.voxel.world.physics;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class PhysicsWorld {
	public static DynamicsWorld dynamicsWorld;
	public static RigidBody playerBody;
	public static ArrayList<RigidBody> bodies;
	Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
	Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);

	public static final Vector3f DEFAULT_VECTOR = new Vector3f(4, 10, 4);

	public static final Quat4f DEFAULT_QUAT = new Quat4f(0, 0, 0, 1);

	public static final Matrix4f DEFAULT_MATRIX = new Matrix4f(DEFAULT_QUAT,
			DEFAULT_VECTOR, 1.0f);

	public static final Transform DEFAULT_TRANSFORM = new Transform(
			new Matrix4f(DEFAULT_QUAT, DEFAULT_VECTOR, 1.0f));

	public PhysicsWorld() {
		setUpPhysics();
		bodies = new ArrayList<RigidBody>();
	}

	public DynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}

	public RigidBody getPlayerBody() {
		return playerBody;
	}
	/*
	 * int cpu = Runtime.getRuntime().availableProcessors(); if(cpu > 1){
	 */
	public void setUpPhysics() {
		setUpDynamicsWorld();
		setUpGround();
		setUpPlayer();
	}
	private void setUpDynamicsWorld() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin,
				worldAabbMax);
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase,
				solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -9.80665f, 0));	//Setting gravity (aka fall speed);	
	}
	private void setUpGround() {
		CollisionShape groundShape = new StaticPlaneShape(
				new Vector3f(0, 1, 0), 0.1f);
		MotionState groundMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0),
						1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		groundBodyConstructionInfo.restitution = 0.0f;
		groundBodyConstructionInfo.friction = 35.5f; //was 1.5f
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		dynamicsWorld.addRigidBody(groundRigidBody);
	}
	private void setUpPlayer(){
		CollisionShape playerShape = new BoxShape(new Vector3f(1f, 1f, 1f));
		MotionState playerMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0),  0)));
		Vector3f playerInertia = new Vector3f(0, 0, 0);
		RigidBodyConstructionInfo playerConstructionInfo = new RigidBodyConstructionInfo(
				60f, playerMotionState, playerShape, playerInertia);
		playerConstructionInfo.restitution = 0.0f;
		playerConstructionInfo.angularDamping = 0.99f;
		playerConstructionInfo.friction = 1f;
		playerBody = new RigidBody(playerConstructionInfo);
		playerBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		dynamicsWorld.addRigidBody(playerBody);
	}
	public static void newBlock(float x, float y, float z) {
		newBlock(new Vector3f(x, y, z));
	}

	public static void newBlock(Vector3f positionVector) {
		float boxSize = 0.25f;
		CollisionShape blockShape = new BoxShape(new Vector3f(boxSize, boxSize, boxSize));
		MotionState blockMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), positionVector, 1.0f)));
		RigidBodyConstructionInfo blockConstructionInfo = new RigidBodyConstructionInfo(
				0, blockMotionState, blockShape, new Vector3f(0, 0, 0));
		blockConstructionInfo.restitution = 0.0f; //New block buoyancy
		blockConstructionInfo.angularDamping = 1.0f;
		blockConstructionInfo.friction = 35.5f; //Block friction *was 1.5f
		RigidBody blockRigidBody = new RigidBody(blockConstructionInfo);
		addBody(blockRigidBody);
	}
	public static void addBody(RigidBody r) {
		int id = bodies.size();
		bodies.add(r);
		dynamicsWorld.addRigidBody(bodies.get(id));
	}

	public void deleteBody(RigidBody r) {
		bodies.remove(r);
		dynamicsWorld.removeRigidBody(r);
	}

	public void step(float timeStep) {
		dynamicsWorld.stepSimulation(timeStep);
	}

	public static void moveCharacter(Vector3f vector3f) {
		//playerBody.applyCentralForce(vector3f);
		//playerBody.applyCentralImpulse(vector3f);
		playerBody.setLinearVelocity(vector3f);
	}

	public static void clearForcesOnPlayer() {
		playerBody.clearForces();
	}
	public static void rotatePlayer(){
		playerBody.getWorldTransform(DEFAULT_TRANSFORM).setRotation(null);
	}
	public static Quat4f getRotation(){
		return playerBody.getWorldTransform(DEFAULT_TRANSFORM).getRotation(new Quat4f(0, 0, 0, 1));
	}
	public static boolean checkCollision(){
        for(RigidBody r : PhysicsWorld.bodies){
        	if(playerBody.checkCollideWith(r)){
        		return true;
        	}
        }
		return false;
		
	}
}
