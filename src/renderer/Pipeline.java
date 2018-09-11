package renderer;

import renderer.Scene.Polygon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * <p>
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

    /**
     * Returns true if the given polygon is facing away from the camera (and so
     * should be hidden), and false otherwise.
     */
    public static boolean isHidden(Polygon poly) {
        Vector3D v1 = poly.getVertices()[0];
        Vector3D v2 = poly.getVertices()[1];
        Vector3D v3 = poly.getVertices()[2];

        Vector3D v2DiffV1 = v2.minus(v1);
        Vector3D v3DiffV2 = v3.minus(v2);

        Vector3D zCheck = v2DiffV1.crossProduct(v3DiffV2);

        return zCheck.z > 0;
    }

    /**
     * Computes the colour of a polygon on the screen, once the lights, their
     * angles relative to the polygon's face, and the reflectance of the polygon
     * have been accounted for.
     *
     * @param lightDirection The Vector3D pointing to the directional light read in from
     *                       the file.
     * @param lightColor     The color of that directional light.
     * @param ambientLight   The ambient light in the scene, i.e. light that doesn't depend
     *                       on the direction.
     */
    public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
        Vector3D a = poly.getVertices()[1].minus(poly.getVertices()[0]);
        Vector3D b = poly.getVertices()[2].minus(poly.getVertices()[1]);
        Vector3D n = a.crossProduct(b);
        Vector3D unitNormal = n.unitVector();
        float cosTheta = unitNormal.cosTheta(lightDirection);
        int red, green, blue;

        if (cosTheta > 0) {
            red = (int) ((ambientLight.getRed() + lightColor.getRed() * cosTheta) * poly.getReflectance().getRed() / 255);
            green = (int) ((ambientLight.getGreen() + lightColor.getGreen() * cosTheta) * poly.getReflectance().getGreen() / 255);
            blue = (int) ((ambientLight.getBlue() + lightColor.getBlue() * cosTheta) * poly.getReflectance().getBlue() / 255);
        } else {
            red = (ambientLight.getRed() * poly.getReflectance().getRed() / 255);
            green = (ambientLight.getGreen()) * poly.getReflectance().getGreen() / 255;
            blue = (ambientLight.getBlue()) * poly.getReflectance().getBlue() / 255;
        }

        red = red > 255 ? 255 : red;
        red = red < 0 ? 0 : red;
        green = green > 255 ? 255 : green;
        green = green < 0 ? 0 : green;
        blue = blue > 255 ? 255 : blue;
        blue = blue < 0 ? 0 : blue;

        return new Color(red, green, blue);
    }

    /**
     * This method should rotate the polygons and light such that the viewer is
     * looking down the Z-axis. The idea is that it returns an entirely new
     * Scene object, filled with new Polygons, that have been rotated.
     *
     * @param scene The original Scene.
     * @param xRot  An angle describing the viewer's rotation in the YZ-plane (i.e
     *              around the X-axis).
     * @param yRot  An angle describing the viewer's rotation in the XZ-plane (i.e
     *              around the Y-axis).
     * @return A new Scene where all the polygons and the light source have been
     * rotated accordingly.
     */
    public static Scene rotateScene(Scene scene, float xRot, float yRot) {
        Transform xPolyRotation = Transform.newXRotation(xRot);
        Transform yPolyRotation = Transform.newYRotation(yRot);

        Transform polyRotation = xPolyRotation.compose(yPolyRotation);
        List<Polygon> polygonBuffer = new ArrayList<>();
        applyTransformation(scene, polyRotation, polygonBuffer);
        Vector3D lightRotation = polyRotation.multiply(scene.getLight());
        return new Scene(polygonBuffer, lightRotation);
    }

    /**
     * This should translate the scene by the appropriate amount.
     *
     * @param scene - scene to apply translated scene
     * @return - translated scene
     */
    public static Scene translateScene(Scene scene) {
        float[] bounds = scene.getBoundingBox();
        float xCenter = (GUI.CANVAS_WIDTH - (bounds[1] - bounds[0])) / 2;
        float yCenter = (GUI.CANVAS_HEIGHT - (bounds[3] - bounds[2])) / 2;
        float zCenter = (GUI.CANVAS_WIDTH - (bounds[5] - bounds[4])) / 2;

        Transform polyTranslation = Transform.newTranslation((xCenter - bounds[0]), (yCenter - bounds[2]), (zCenter - bounds[4]));
        List<Polygon> polyBuffer = new ArrayList<>();
        applyTransformation(scene, polyTranslation, polyBuffer);
        return new Scene(polyBuffer, scene.getLight());
    }

    /**
     * This should scale the scene.
     *
     * @param scene - scene to apply scale to
     * @return - the scaled scene
     */
    public static Scene scaleScene(Scene scene) {
        float[] bounds = scene.getBoundingBox();
        float width = bounds[0] - bounds[1];
        float height = bounds[2] - bounds[3];
        float horizontalScale = GUI.CANVAS_WIDTH / width / 2;
        float verticalScale = GUI.CANVAS_HEIGHT / height / 2;
        float scale = Math.abs(Math.min(horizontalScale, verticalScale));

        Transform polyScale = Transform.newScale(scale, scale, scale);
        List<Polygon> polyBuffer = new ArrayList<>();
        applyTransformation(scene, polyScale, polyBuffer);
        return new Scene(polyBuffer, scene.getLight());
    }

    /**
     * Applies the transformation on the given scene and adds them to the buffer
     *
     * @param scene          - scene to apply transformation to
     * @param transformation - transformation to apply
     * @param polyBuffer     - buffer for the translated polygons
     */
    private static void applyTransformation(Scene scene, Transform transformation, List<Polygon> polyBuffer) {
        Vector3D[] vectorBuffer;
        for (Polygon poly : scene.getPolygons()) {
            vectorBuffer = new Vector3D[3];
            for (int i = 0; i < poly.getVertices().length; i++) {
                vectorBuffer[i] = transformation.multiply(poly.getVertices()[i]);
            }
            polyBuffer.add(new Polygon(vectorBuffer[0], vectorBuffer[1], vectorBuffer[2], poly.getReflectance()));
        }
    }

    /**
     * Computes the edge list of a single provided polygon, as per the lecture
     * slides.
     */
    public static EdgeList computeEdgeList(Polygon poly) {
        Vector3D v1 = poly.getVertices()[0];
        Vector3D v2 = poly.getVertices()[1];
        Vector3D v3 = poly.getVertices()[2];

        int startY = (int) Math.min(v1.y, Math.min(v2.y, v3.y));
        int endY = (int) Math.max(v1.y, Math.max(v2.y, v3.y));
        EdgeList edgeList = new EdgeList(startY, endY);

        Vector3D[][] vectorPairs = new Vector3D[3][2];
        vectorPairs[0] = new Vector3D[]{v1, v2};
        vectorPairs[1] = new Vector3D[]{v2, v3};
        vectorPairs[2] = new Vector3D[]{v3, v1};
        for (Vector3D[] pair : vectorPairs) {
            float xSlope = (pair[1].x - pair[0].x) / (pair[1].y - pair[0].y);
            float zSlope = (pair[1].z - pair[0].z) / (pair[1].y - pair[0].y);

            float x = pair[0].x;
            int y = Math.round(pair[0].y);
            float z = pair[0].z;
            if (pair[0].y < pair[1].y) {
                while (y <= Math.round(pair[1].y)) {
                    edgeList.addRow(y, x, z, EdgeList.LEFT);
                    x += xSlope;
                    z += zSlope;
                    y++;
                }
            } else {
                while (y >= Math.round(pair[1].y)) {
                    edgeList.addRow(y, x, z, EdgeList.RIGHT);
                    x -= xSlope;
                    z -= zSlope;
                    y--;
                }
            }
        }
        return edgeList;
    }

    /**
     * Fills a zbuffer with the contents of a single edge list according to the
     * lecture slides.
     * <p>
     * The idea here is to make zbuffer and zdepth arrays in your main loop, and
     * pass them into the method to be modified.
     *
     * @param zBuffer      A double array of colours representing the Color at each pixel
     *                     so far.
     * @param zDepth       A double array of floats storing the z-value of each pixel
     *                     that has been coloured in so far.
     * @param polyEdgeList The edgelist of the polygon to add into the zbuffer.
     * @param polyColor    The colour of the polygon to add into the zbuffer.
     */
    public static void computeZBuffer(Color[][] zBuffer, float[][] zDepth, EdgeList polyEdgeList, Color polyColor) {
        for (int y = polyEdgeList.getStartY(); y <= polyEdgeList.getEndY(); y++) {
            float slope = (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y)) / (polyEdgeList.getRightX(y) / polyEdgeList.getLeftX(y));
            int x = Math.round(polyEdgeList.getLeftX(y));
            float z = polyEdgeList.getLeftZ(y) + slope * (x - polyEdgeList.getLeftX(y));

            while (x < Math.round(polyEdgeList.getRightX(y))) {
                if (x < 0 || x >= zBuffer.length) {
                    z += slope;
                    x++;
                    continue;
                }

                if (x < zDepth.length && y < zDepth[0].length && z < zDepth[x][y]) {
                    zBuffer[x][y] = polyColor;
                    zDepth[x][y] = z;
                }
                z += slope;
                x++;
            }
        }
    }
}

// code for comp261 assignments
