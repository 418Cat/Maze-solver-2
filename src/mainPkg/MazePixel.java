package mainPkg;

import java.awt.Color;

public class MazePixel {
	
	public int[] coords;
	public Color color;
	public int[] rgb;
	
	public boolean isEntry;
	public boolean isExit;
	public boolean isWall;
	public boolean isPath;
	public boolean isPathUsed;
	public int indexInPath;
	
	public void setBooleans(boolean isEntry, boolean isExit, boolean isWall, boolean isPath, boolean isPathUsed) {
		this.isEntry = isEntry;
		this.isExit = isExit;
		this.isWall = isWall;
		this.isPath = isPath;
		this.isPathUsed = isPathUsed;
	}
	
	public boolean isAvailable() {
		return(isPath && !isPathUsed);
	}
	
}