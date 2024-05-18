package mainPkg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Maze {
	
	public MazePixel[][] map;
	BufferedImage mazeImage;
	int mazePixSize;
	
	int[] entryCoords;
	int[] exitCoords;
	
	public ArrayList<MazePixel> mainRoute = new ArrayList<>();
	private ArrayList<MazePixel> availablePaths = new ArrayList<>();
	
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
		
		int entryCoordImgPix = 0;
		
		int width = 0;
		for(int x = 0; x < mazeImage.getWidth(); x++) {
			if(new Color(mazeImage.getRGB(x, 0)).getRed() > 128) {
				if(width == 0) {
					entryCoordImgPix = x;
				}
				width++;
			} else if(new Color(mazeImage.getRGB(x, 0)).getRed() < 128 && width > 0) {
				break;
			}
		}
		
		entryCoords = new int[] {entryCoordImgPix/width, 0};
		
		return(width);
	}
	
	
	private void getMazeMap() {
		
		map = new MazePixel[mazeImage.getWidth()/mazePixSize][mazeImage.getHeight()/mazePixSize];
		
		for(int x = 0; x < mazeImage.getWidth()/mazePixSize; x++) {
			
			for(int y = 0; y < mazeImage.getHeight()/mazePixSize; y++) {
				
				MazePixel tmpPix = new MazePixel();
				
				tmpPix.rgb = new int[] {new Color(mazeImage.getRGB(x*mazePixSize, y*mazePixSize)).getRed(),
						new Color(mazeImage.getRGB(x*mazePixSize, y*mazePixSize)).getGreen(),
						new Color(mazeImage.getRGB(x*mazePixSize, y*mazePixSize)).getBlue()};
				
				tmpPix.color = new Color(mazeImage.getRGB(x*mazePixSize, y*mazePixSize));
				
				tmpPix.coords = new int[] {x, y};
				
				if(tmpPix.rgb[0] > 128 && tmpPix.rgb[1] > 128 && tmpPix.rgb[2] > 128) {
					tmpPix.setBooleans(false, false, false, true, false);
				}
				
				if(tmpPix.rgb[0] < 128 && tmpPix.rgb[1] < 128 && tmpPix.rgb[2] < 128) {
					tmpPix.setBooleans(false, false, true, false, false);
				}
				
				if(y == 0 && tmpPix.rgb[0] > 128 && tmpPix.rgb[1] > 128 && tmpPix.rgb[2] > 128) {
					tmpPix.setBooleans(true, false, false, true, false);
				}
				
				if(y+1 == mazeImage.getHeight()/mazePixSize && y == 0 && tmpPix.rgb[0] > 128 && tmpPix.rgb[1] > 128 && tmpPix.rgb[2] > 128) {
					tmpPix.setBooleans(false, true, false, true, false);
					exitCoords = new int[] {x, y};
				}
				
				map[x][y] = tmpPix;
				
			}
			
		}
		
	}
	
	public void solve(MazeWindow win) {
		
		boolean solved = false;
		
		mainRoute.add(map[entryCoords[0]][entryCoords[1]]);
		map[entryCoords[0]][entryCoords[1]].indexInPath = 0;
		
		while(!solved) {
			
			mainRoute.get(mainRoute.size()-1).isPathUsed = true;
			mainRoute.get(mainRoute.size()-1).indexInPath = mainRoute.size()-1;
			win.drawMazePixel(mainRoute.get(mainRoute.size()-1).coords[0], mainRoute.get(mainRoute.size()-1).coords[1], getGradient(mainRoute.get(mainRoute.size()-1).indexInPath));
			
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) == 1) {
				availablePaths.add(getAvailableNeighbors(mainRoute.get(mainRoute.size()-1))[0]);
			}
			
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) > 1) {
				for(MazePixel neighb : getAvailableNeighbors(mainRoute.get(mainRoute.size()-1))) {
					availablePaths.add(neighb);
				}
			}
			
			
			// No available paths for the current cursor but some left behind
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) == 0 && availablePaths.size() > 0) {
				
				// Index of the crossing pixel in the list
				int lastCrossingIndex = 0;
				for(MazePixel neighb : getNeighbors(availablePaths.get(availablePaths.size()-1))) {
					if(neighb.isPathUsed && neighb.indexInPath > lastCrossingIndex) lastCrossingIndex = neighb.indexInPath;
				}
				
				final int lastCrossingIndexFinal = lastCrossingIndex;
				/*for(int i = lastCrossingIndex+1; i < mainRoute.size(); i++) {
					win.drawMazePixel(mainRoute.get(i).coords[0], mainRoute.get(i).coords[1], Color.red);
				}*/
				erasePathPixels(lastCrossingIndexFinal);
				mainRoute.removeIf(pix -> pix.indexInPath > lastCrossingIndexFinal);
				
			}
			
			if(availablePaths.size() > 0) { 
				mainRoute.add(availablePaths.get(availablePaths.size()-1));
				availablePaths.remove(availablePaths.size()-1);
			}
			
			if(mainRoute.get(mainRoute.size()-1).coords[1] == map[0].length-1)
			{
				solved = true;
				Main.win.drawMazePixelNoSleep(mainRoute.get(mainRoute.size()-1).coords[0], mainRoute.get(mainRoute.size()-1).coords[1], getGradient(mainRoute.size()-1));
			}
			
		}
	}
	
	private void erasePathPixels(int lastCrossingIndex)
	{
		for(int i = mainRoute.size()-1; i > lastCrossingIndex; i--)
		{
			Main.win.drawMazePixel(mainRoute.get(i).coords[0], mainRoute.get(i).coords[1], Color.white);
		}
	}
	
	private Color getGradient(int index) {
		
		int[] limits = {60, 204};
		
		return Color.getHSBColor((float)index/(float)360., (float)1.0, (float)0.9);
		//return(new Color(0xff0000));
		/*int isMoreThan255 = index > 255 ? 1 : 0;
		int isMoreThan255255 = index > 255*255 ? 1 : 0;
		int color = 0;
		return(new Color(color));*/
	}
	
	private int countAvailableNeighbors(MazePixel pix) {
		
		int count = 0;
		
		if(hasLeftNeighborAvailable(pix)) count++;
		if(hasRightNeighborAvailable(pix)) count++;
		if(hasTopNeighborAvailable(pix)) count++;
		if(hasBottomNeighborAvailable(pix)) count++;
		
		return(count);
	}
	
	private MazePixel[] getNeighbors(MazePixel pix) {
		
		ArrayList<MazePixel> tmpNeighb = new ArrayList<>();
		if(hasLeftNeighbor(pix)) tmpNeighb.add(getLeftNeighbor(pix));
		if(hasRightNeighbor(pix)) tmpNeighb.add(getRightNeighbor(pix));
		if(hasTopNeighbor(pix)) tmpNeighb.add(getTopNeighbor(pix));
		if(hasBottomNeighbor(pix)) tmpNeighb.add(getBottomNeighbor(pix));
		
		MazePixel[] neighb = new MazePixel[tmpNeighb.size()];
		
		for(int i = 0; i < tmpNeighb.size(); i++) {
			neighb[i] = tmpNeighb.get(i);
		}
		
		return(neighb);
	}
	
	private MazePixel[] getAvailableNeighbors(MazePixel pix) {
		
		MazePixel[] neighborPixels = new MazePixel[countAvailableNeighbors(pix)];
		
		int neighbFilled = 0;
		for(MazePixel neighb : getNeighbors(pix)) {
			if(neighb.isAvailable()) {
				neighborPixels[neighbFilled] = neighb;
					neighbFilled++;
			}
		}
		
		return(neighborPixels);
	}
	
	
	private boolean hasLeftNeighbor(MazePixel pix) {
		return(pix.coords[0] > 0);
	}
	private boolean hasLeftNeighborAvailable(MazePixel pix) {
		return(hasLeftNeighbor(pix) && getLeftNeighbor(pix).isAvailable());
	}
	
	
	
	private boolean hasRightNeighbor(MazePixel pix) {
		return(pix.coords[0] < map.length-1);
	}
	private boolean hasRightNeighborAvailable(MazePixel pix) {
		return(hasRightNeighbor(pix) && getRightNeighbor(pix).isAvailable());
	}
	
	
	
	private boolean hasTopNeighbor(MazePixel pix) {
		return(pix.coords[1] > 0);
	}
	private boolean hasTopNeighborAvailable(MazePixel pix) {
		return(hasTopNeighbor(pix) && getTopNeighbor(pix).isAvailable());
	}
	
	private boolean hasBottomNeighbor(MazePixel pix) {
		return(pix.coords[1] < map[0].length-1);
	}
	private boolean hasBottomNeighborAvailable(MazePixel pix) {
		return(hasBottomNeighbor(pix) && getBottomNeighbor(pix).isAvailable());
	}
	
	
	
	private MazePixel getLeftNeighbor(MazePixel pix) {
		return(map[pix.coords[0]-1][pix.coords[1]]);
	}
	
	private MazePixel getRightNeighbor(MazePixel pix) {
		return(map[pix.coords[0]+1][pix.coords[1]]);
	}
	
	private MazePixel getTopNeighbor(MazePixel pix) {
		return(map[pix.coords[0]][pix.coords[1]-1]);
	}
	
	private MazePixel getBottomNeighbor(MazePixel pix) {
		return(map[pix.coords[0]][pix.coords[1]+1]);
	}


}