package uk.lewdev.standmodels.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum Axis {

	NORTH(new Vector(0, 0, -1)), //Negative Z
	EAST(new Vector(1, 0, 0)),   //Positive X
	SOUTH(new Vector(0, 0, 1)),  //Positive Z
	WEST(new Vector(-1, 0, 0));  //Negative X
	
	Vector direction;
	
	Axis(Vector direction) {
		this.direction = direction;
	}
	
	/**
	 * @return Vector direction Axis faces
	 */
	public Vector getDirection() {
		return this.direction;
	}
	
	/**
	 * Gets the axis the location faces
	 * 
	 * @param loc
	 * @return Axis
	 */
	public static Axis getAxis(Location loc) {
		Axis[]  axis = new Axis[] {Axis.SOUTH, Axis.WEST, Axis.NORTH, Axis.EAST};
		
		float yaw = loc.getYaw();
		//Calculation pulled from https://bukkit.org/threads/get-the-direction-a-player-is-facing.105314/
		return axis[Math.round(yaw / 90f) & 0x3];
	}
	
	/**
	 * Get the opposite of an axis.
	 * East = West
	 * West = East
	 * North = South
	 * South = North
	 * 
	 * @param axis
	 * @return Opposite of axis
	 */
	public static Axis opposite(Axis axis) {
		switch (axis) {
			case EAST: return Axis.WEST;
			case NORTH: return Axis.SOUTH;
			case SOUTH: return Axis.NORTH;
			case WEST: return Axis.EAST;
		}
		return axis;
	}
	
	public static float calcRightRotationAngle(Axis currentFacing, Axis desiredFacing) {
		int rotation = 0;
		
		while(currentFacing != desiredFacing) {
			int curPos = currentFacing.ordinal();
			
			if(curPos + 1 >= Axis.values().length) {
				currentFacing = Axis.values()[0];
			} else {
				currentFacing = Axis.values()[currentFacing.ordinal() + 1];
			}
			
			rotation += 90;
		}
		
		return rotation;
	}
	
	/**
	 * Rotates a point around the center by a number of degrees.
	 * 
	 * @param center
	 * @param point
	 * @param degrees
	 * @return Rotated location
	 */
	public static Location rotateRight(Location center, Location point, float degrees) {
		center = center.clone();
		point = point.clone();
		
        double rad = Math.toRadians(degrees);
        double sinus = Math.sin(rad);
        double cosinus = Math.cos(rad);
        
        point = point.subtract(center);
        
        double x = point.getX() * cosinus - point.getZ() * sinus;
        double y = point.getZ() * cosinus + point.getX() * sinus;
        point.setX(x);
        point.setZ(y);
        point.setY(point.getY());
        point = point.add(center);
        point.setYaw(point.getYaw() + degrees);
        point.setPitch(point.getPitch());
        
        return point;
    }
}
