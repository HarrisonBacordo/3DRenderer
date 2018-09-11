package renderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renderer extends GUI {
    private static final float ROTATION_VALUE = 0.8f;
    private boolean init = true;
    private Scene scene;

    @Override
    protected void onLoad(File file) {
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
                            color[i - 9] = Math.round(floatValues[i]);
                        }
                    }
                    polygons.add(new Scene.Polygon(points, color));
                }
            }
            this.scene = new Scene(polygons, lightPos);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onKeyPress(KeyEvent ev) {
        switch (ev.getKeyCode()) {
            case KeyEvent.VK_W:
                this.scene = Pipeline.rotateScene(this.scene, -ROTATION_VALUE, 0);
                this.scene = Pipeline.translateScene(this.scene);
                break;
            case KeyEvent.VK_A:
                this.scene = Pipeline.rotateScene(this.scene, 0, ROTATION_VALUE);
                this.scene = Pipeline.translateScene(this.scene);
                break;
            case KeyEvent.VK_S:
                scene = Pipeline.rotateScene(scene, ROTATION_VALUE, 0);
                scene = Pipeline.translateScene(scene);
                break;
            case KeyEvent.VK_D:
                this.scene = Pipeline.rotateScene(this.scene, 0, -ROTATION_VALUE);
                this.scene = Pipeline.translateScene(this.scene);
                break;
            default:
                break;

        }
    }

    @Override
    protected BufferedImage render() {
        if (this.scene == null) {
            return null;
        }

//		variable init
        Color[][] renderedImg = new Color[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
        float[][] zDepth = new float[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
        for (int i = 0; i < GUI.CANVAS_WIDTH; i++) {
            Arrays.fill(zDepth[i], Float.POSITIVE_INFINITY);
            Arrays.fill(renderedImg[i], Color.GRAY);
        }

//		scale scene if first render
        if (this.init) {
            this.scene = Pipeline.scaleScene(this.scene);
            this.init = false;
        }

//		center scene
        this.scene = Pipeline.translateScene(this.scene);

//		render visible polygons
        EdgeList edgeList;
        Color shading;
        Color ambientLight = new Color(this.getAmbientLight()[0], this.getAmbientLight()[1], this.getAmbientLight()[2]);
        for (Scene.Polygon poly : this.scene.getPolygons()) {
            if (Pipeline.isHidden(poly)) {
                continue;
            }
            edgeList = Pipeline.computeEdgeList(poly);
            shading = Pipeline.getShading(poly, this.scene.lightPos, poly.getReflectance(), ambientLight);
            Pipeline.computeZBuffer(renderedImg, zDepth, edgeList, shading);
        }
        return this.convertBitmapToImage(renderedImg);
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
