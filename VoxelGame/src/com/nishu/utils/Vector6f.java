package com.nishu.utils;

public class Vector6f {
	
	private float x1, y1, z1, x2, y2, z2;
	
	public Vector6f(float x1, float y1, float z1, float x2, float y2, float z2){
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z2;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public float length(){
		return (float) Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1 + x2 * x2 + y2 * y2 + z2 * z2);
	}
	
	public float dot(Vector6f r){
		return x1 * r.getX1() + y1 * r.getY1() + z1 * r.getZ1() +  x2 * r.getX2() + y2 * r.getY2() + z2 * r.getZ2();
	}
	
	public Vector6f normalize(){
		float length = length();
		
		x1 /= length;
		y1 /= length;
		z1 /= length;
		x2 /= length;
		y2 /= length;
		z2 /= length;
		
		return this;
	}
	
	public Vector6f rotate(){
		return null;
	}
	
	public Vector6f add(Vector6f r){
		return new Vector6f(x1 + r.getX1(), y1 + r.getY1(), z1 + r.getZ1(), x2 + r.getX2(), y2 + r.getY2(), z2 + r.getZ2());
	}

	public Vector6f add(float r){
		return new Vector6f((x1 + r), (y1 + r), (z1 + r), x2 + r, y2 + r, z2 + r);
	}
	
	public Vector6f sub(Vector6f r){
		return new Vector6f(x1 - r.getX1(), y1 - r.getY1(),z1 - r.getZ1(), x2 - r.getX2(), y2 - r.getY2(), z2 - r.getZ2());
	}

	public Vector6f sub(float r){
		return new Vector6f((x1 - r), (y1 - r),(z1 - r), x2 - r, y2 - r, z2 - r);
	}
	
	public Vector6f mul(Vector6f r){
		return new Vector6f(x1 * r.getX1(), y1 * r.getY1(), z1 * r.getZ1(), x2 * r.getX2(), y2 * r.getY2(), z2 * r.getZ2());
	}

	public Vector6f mul(float r){
		return new Vector6f((x1 * r), (y1 * r),(z1 * r), x2 * r, y2 * r, z2 * r);
	}
	
	public Vector6f div(Vector6f r){
		return new Vector6f(x1 / r.getX1(), y1 / r.getY1(), z1 / r.getZ1(), x2 / r.getX2(), y2 / r.getY2(), z2 / r.getZ2());
	}

	public Vector6f div(float r){
		return new Vector6f((x1 / r), (y1 / r),(z1 / r), x2 / r, y2 / r, z2 / r);
	}
	
	public void setPosition(float x1, float y1, float z1, float x2, float y2, float z2){
		this.x1 = x1; 
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2; 
		this.y2 = y2;
		this.z2 = z2;
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getZ1() {
		return z1;
	}

	public void setZ1(float z1) {
		this.z1 = z1;
	}
	
	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public float getZ2() {
		return z2;
	}

	public void setZ2(float z2) {
		this.z2 = z2;
	}

}
