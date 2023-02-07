package mainPkg;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


/**
 * The class handling the displayed part of the maze
 * @author 418cat
 *
 */
public class MazeWindow {
	
	JFrame mazeFrame;
	Graphics g;
	
	int[] frameSize;
	int[] mazeRes;
	
	int mazePixelSize;
	
	/**
	 * Generate a window to display the maze
	 * @param sizeX the horizontal size of the frame on screen
	 * @param sizeY the vertical size of the frame on screen
	 * @param mazeResX the horizontal resolution of the maze itself
	 * @param mazeResY the vertical resolution of the maze itself
	 */
	public MazeWindow(int sizeX, int sizeY, int mazePixelSize) {
		
		mazeFrame = new JFrame("Maze solver");
		mazeFrame.setSize(sizeX, sizeY);
		mazeFrame.setLocation(0, 1500);
		
		mazeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mazeFrame.setUndecorated(true);
		mazeFrame.setVisible(true);
		
		g = mazeFrame.getGraphics();
		
		//the jframe needs some time or it might not display the first few pixels correctly
		try {
			Thread.sleep(150);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		frameSize = new int[] {sizeX, sizeY};
		
		this.mazePixelSize = mazePixelSize;
	}
	
	
	/**
	 * Draw a pixel of the maze on the window
	 * @param x x coordinate of the maze pixel, not of the input image pixel
	 * @param y y coordinate of the maze pixel, not of the input image pixel
	 * @param color color to display
	 */
	public void drawMazePixel(int x, int y, Color color) {
		g.setColor(color);
		g.fillRect(x*mazePixelSize, y*mazePixelSize, mazePixelSize, mazePixelSize);
	}

}
