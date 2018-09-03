package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renderer extends GUI {
	public static final float ROTATION_VALUE = 0.8f;
	public static final int TRANSLATION_VALUE = 10;

	private Scene scene;

	@Override
	protected void onLoad(File file) {
		// TODO fill this in.
		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			boolean firstLine = true;
			Vector3D lightPos = null;
			List<Scene.Polygon> polygons = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				float[] points = new float[9];
				int[] color = new int[3];
				String[] stringValues = line.split(" ");
				Float[] floatValues = Arrays.stream(stringValues).map(Float::valueOf).toArray(Float[]::new);
				if (firstLine) {
					lightPos = new Vector3D(floatValues[0], floatValues[1], floatValues[2]);
					firstLine = false;
				} else {
					for (int i = 0; i < 12; i++) {
						if (i < 9) {
							points[i] = floatValues[i];
						} else {
							color[i-9] = Math.round(floatValues[i]);
						}
					}
					polygons.add(new Scene.Polygon(points, color));
				}
			}
			this.scene = new Scene(polygons, lightPos);
			System.out.println(scene);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */

//		Rotation
		switch (ev.getKeyChar()) {
			case 'a':
				scene = Pipeline.rotateScene(scene, 0, ROTATION_VALUE);
				scene = Pipeline.translateScene(scene);
				break;
			case 'd':
				scene = Pipeline.rotateScene(scene, 0, -ROTATION_VALUE);
				scene = Pipeline.translateScene(scene);
				break;
			case 's':
				scene = Pipeline.rotateScene(scene, ROTATION_VALUE, 0);
				scene = Pipeline.translateScene(scene);
				break;
			case 'w':
				scene = Pipeline.rotateScene(scene, -ROTATION_VALUE, 0);
				scene = Pipeline.translateScene(scene);
				break;
		}
	}

	@Override
	protected BufferedImage render() {
		// TODO fill this in.

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		Color[][] renderedImg = new Color[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
		double[][] zDepth = new double[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
		for (double [] row : zDepth) {
			Arrays.fill(row, Double.POSITIVE_INFINITY);
		}

		for (Scene.Polygon poly : this.scene.getPolygons()) {
//			calculate the x and z EdgeList (EL) of this polygon;

		}
		return null;
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
