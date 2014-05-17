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
			new Vector3f(4, 10, 4), 1.0f);

	public static final Transform DEFAULT_TRANSFORM = new Transform(
			new Matrix4f(DEFAULT_QUAT, new Vector3f(4, 10, 4), 1.0f));

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
		dynamicsWorld.setGravity(new Vector3f(0, -10f, 0));
		CollisionShape groundShape = new StaticPlaneShape(
				new Vector3f(0, 1, 0), 0.1f);
		CollisionShape playerShape = new BoxShape(new Vector3f(0.5f, 1f, 0.5f));

		MotionState groundMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0),
						1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		groundBodyConstructionInfo.restitution = 0.0f;
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		dynamicsWorld.addRigidBody(groundRigidBody);
		MotionState ballMotion = new DefaultMotionState(new Transform(
				DEFAULT_TRANSFORM));

		Vector3f ballInertia = new Vector3f(0, 0, 0);
		playerShape.calculateLocalInertia(2.5f, ballInertia);
		RigidBodyConstructionInfo ballConstructionInfo = new RigidBodyConstructionInfo(
				2.5f, ballMotion, playerShape, ballInertia);
		ballConstructionInfo.restitution = 0.0f;
		ballConstructionInfo.angularDamping = 0.95f;

		playerBody = new RigidBody(ballConstructionInfo);
		playerBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		dynamicsWorld.addRigidBody(playerBody);
	}

	public static void newBlock(float x, float y, float z) {
		newBlock(new Vector3f(x, y, z));
	}

	public static void newBlock(Vector3f positionVector) {
		CollisionShape blockShape = new BoxShape(new Vector3f(1, 1, 1));
		MotionState blockMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), positionVector, 1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(
				0, blockMotionState, blockShape, new Vector3f(0, 0, 0));
		RigidBody blockRigidBody = new RigidBody(groundBodyConstructionInfo);
		addBody(blockRigidBody);

	}

	public static void addBody(RigidBody r) {
		bodies.add(r);
		dynamicsWorld.addRigidBody(r);
	}

	public void deleteBody(RigidBody r) {
		bodies.remove(r);
		dynamicsWorld.removeRigidBody(r);
	}

	public void step(float timeStep) {
		dynamicsWorld.stepSimulation(timeStep);
	}
}
