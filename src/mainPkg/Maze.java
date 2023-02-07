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
		Color pixColor = new Color(mazeImage.getRGB(0, 0));
		
		int entryCoordImgPix = 0;
		
		int width = 0;
		for(int x = 0; x < mazeImage.getWidth(); x++) {
			if(mazeImage.getRGB(x, 0) != pixColor.getRGB()) {
				if(width == 0) {
					entryCoordImgPix = x;
				}
				width++;
			} else if(mazeImage.getRGB(x, 0) == pixColor.getRGB() && width > 0) {
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
				
				if(tmpPix.rgb[0] > 0 && tmpPix.rgb[1] > 0 && tmpPix.rgb[2] > 0) {
					tmpPix.setBooleans(false, false, false, true, false);
				}
				
				if(tmpPix.rgb[0] == 0 && tmpPix.rgb[1] == 0 && tmpPix.rgb[2] == 0) {
					tmpPix.setBooleans(false, false, true, false, false);
				}
				
				if(y == 0 && tmpPix.rgb[0] > 0 && tmpPix.rgb[1] > 0 && tmpPix.rgb[2] > 0) {
					tmpPix.setBooleans(true, false, false, true, false);
					System.out.println("found entry coords");
				}
				
				if(y+1 == mazeImage.getHeight()/mazePixSize && y == 0 && tmpPix.rgb[0] > 0 && tmpPix.rgb[1] > 0 && tmpPix.rgb[2] > 0) {
					tmpPix.setBooleans(false, true, false, true, false);
					exitCoords = new int[] {x, y};
				}
				
				map[x][y] = tmpPix;
				
			}
			
		}
		
	}
	
	public void solve() {
		
		boolean solved = false;
		
		mainRoute.add(map[entryCoords[0]][entryCoords[1]]);
		map[entryCoords[0]][entryCoords[1]].indexInPath = 0;
		
		while(!solved) {
			
			System.out.println("current coords : " + mainRoute.get(mainRoute.size()-1).coords[0] + ", " + mainRoute.get(mainRoute.size()-1).coords[1]);
			
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) == 1) {
				availablePaths.add(getAvailableNeighbors(mainRoute.get(mainRoute.size()-1))[0]);
			}
			
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) > 1) {
				for(MazePixel neighb : getAvailableNeighbors(mainRoute.get(mainRoute.size()-1))) {
					availablePaths.add(neighb);
				}
			}
			
			if(countAvailableNeighbors(mainRoute.get(mainRoute.size()-1)) == 0 && availablePaths.size() > 0) {
				int lastCrossingIndex = 0;
				for(MazePixel neighb : getNeighbors(availablePaths.get(availablePaths.size()-1))) {
					if(neighb.isPathUsed && neighb.indexInPath > lastCrossingIndex) lastCrossingIndex = neighb.indexInPath;
				}
				
				final int lastCrossingIndexFinal = lastCrossingIndex;
				mainRoute.removeIf(n -> n.indexInPath >= lastCrossingIndexFinal);
				
			}
			
			if(availablePaths.size() > 0) { 
				mainRoute.add(availablePaths.get(availablePaths.size()-1));
				mainRoute.get(mainRoute.size()-1).isPathUsed = true;
				mainRoute.get(mainRoute.size()-1).indexInPath = mainRoute.size()-1;
				availablePaths.remove(availablePaths.size()-1);
			}
			
			if(mainRoute.get(mainRoute.size()-1).coords[1] == map[0].length-1) solved = true;
			
		}
		
	}
	
	
	private boolean pixHasNeighborsAvailable(MazePixel pix) {
		
		int x = pix.coords[0];
		int y = pix.coords[1];

		if(hasLeftNeighborAvailable(pix)) return(true); //left neighbor available
		if(hasRightNeighborAvailable(pix)) return(true); //right neighbor available
		
		if(hasTopNeighborAvailable(pix)) return(true); //top neighbor available
		if(hasBottomNeighborAvailable(pix)) return(true); //bottom neighbor available
		
		return(false);
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