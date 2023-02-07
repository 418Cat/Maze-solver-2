package mainPkg;

import java.awt.Color;

public class MazePixel {
	
	public int[] coords;
	public Color color;
	
	public boolean isEntry;
	public boolean isExit;
	public boolean isWall;
	public boolean isPath;
	public boolean isPathUsed;
	
	public void setBooleans(boolean isEntry, boolean isExit, boolean isWall, boolean isPath, boolean isPathUsed) {
		this.isEntry = isEntry;
		this.isExit = isExit;
		this.isWall = isWall;
		this.isPath = isPath;
		this.isPathUsed = isPathUsed;
	}
	
}