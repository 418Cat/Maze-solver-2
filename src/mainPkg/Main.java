package mainPkg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.err.println("You must provide the maze file path as a program argument.\nYou can generate a maze image here : \nhttps://keesiemeijer.github.io/maze-generator/#generate\n\n/!\\ Don't forget to set the maze entries to top and bottom");
			System.exit(0);
		}
		
		File mazeFile = new File(args[0]);
		BufferedImage mazeImage = null;
		try {
			mazeImage = ImageIO.read(mazeFile);
		} catch (IOException e) {
			System.err.println(e);
		}
		
		Maze maze = new Maze(mazeImage);
		
		MazeWindow win = new MazeWindow(mazeImage.getWidth(), mazeImage.getHeight(), maze.mazePixSize);
		
		for(MazePixel[] pixRow : maze.map) {
			for(MazePixel pix : pixRow) {
				win.drawMazePixel(pix.coords[0], pix.coords[1], pix.color);
			}
		}
		
		maze.solve();
		
		maze.mainRoute.forEach(pix -> win.drawMazePixel(pix.coords[0], pix.coords[1], Color.red));

	}

}
