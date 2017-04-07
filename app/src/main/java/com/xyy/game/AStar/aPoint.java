package com.xyy.game.AStar;

import android.support.annotation.NonNull;

/**
 * A*寻路算法专用节点
 */
public class aPoint implements Comparable<aPoint> {

	int index;

	aPoint parent;

	int F, G;

	/*public aPoint() {
		super();
	}*/

	public void initial(int x){
		this.index = x;
		
		this.parent = null;
		
		this.F = 0;

		this.G = 0;

	}

	public int compareTo(@NonNull aPoint o) {

		return this.F - o.F;

	}

	@Override
	public boolean equals(Object obj) {

		aPoint point = (aPoint) obj;

		return point.index == this.index;

	}

	public boolean equals(int x) {

		return x == this.index;

	}

	/*@Override
	public String toString() {

		return "(" + this.index +")";

	}*/
 
}
