package mainPkg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Maze {
	
	public MazePixel[][] map;
	BufferedImage mazeImage;
	int mazePixSize;
	
	int[] entryCoords;
	int[] exitCoords;
	
	Maze(BufferedImage mazeImage) {
		this.mazeImage = mazeImage;
		
		mazePixSize = findMazePixelSize();
		
		getMazeMap();
	}
	
	
	/**
	 * this function assumes the maze got square pixels and returns finds the size of maze pixels
	 * @return the size of the maze pixels
	 */
	private int findMazePixelSize() {
		Color pixColor = new Color(mazeImage.getRGB(0, 0));
		
		int width = 0;
		for(int x = 0; x < mazeImage.getWidth(); x++) {
			if(mazeImage.getRGB(x, 0) != pixColor.getRGB()) {
				width++;
			} else if(mazeImage.getRGB(x, 0) == pixColor.getRGB() && width > 0) {
				break;
			}
		}
		return(width);
	}
	
	
	private void getMazeMap() {
		
		map = new MazePixel[mazeImage.getWidth()/mazePixSize][mazeImage.getHeight()/mazePixSize];
		
		for(int x = 0; x < mazeImage.getWidth()/mazePixSize; x++) {
			
			for(int y = 0; y < mazeImage.getHeight()/mazePixSize; y++) {
				
				MazePixel tmpPix = new MazePixel();
				
				tmpPix.color = new Color(mazeImage.getRGB(x*mazePixSize, y*mazePixSize));
				tmpPix.coords = new int[] {x, y};
				
				if(tmpPix.color == Color.white) {
					tmpPix.setBooleans(false, false, false, true, false);
				}
				
				if(tmpPix.color == Color.black) {
					tmpPix.setBooleans(false, false, true, false, false);
				}
				
				if(y == 0 && tmpPix.color == Color.white) {
					tmpPix.setBooleans(true, false, false, true, false);
					entryCoords = new int[] {x, y};
				}
				
				if(y+1 == mazeImage.getHeight()/mazePixSize && tmpPix.color == Color.white) {
					tmpPix.setBooleans(false, true, false, true, false);
					exitCoords = new int[] {x, y};
				}
				
				map[x][y] = tmpPix;
				
			}
			
		}
		
	}

}