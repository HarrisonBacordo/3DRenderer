package renderer;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 * <p>
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private int startY, endY;
    private float[] leftX, rightX, leftZ, rightZ;

    public EdgeList(int startY, int endY) {
        if (startY < endY) {
            this.startY = startY;
            this.endY = endY;
        } else {
            this.startY = endY;
            this.endY = startY;
        }
        int heightDifference = (endY - startY) + 1;
        leftX = new float[heightDifference];
        rightX = new float[heightDifference];
        leftZ = new float[heightDifference];
        rightZ = new float[heightDifference];
    }

    //Handles adding a row given a height/index, an x location, z location and the direction in which its going
    public void addRow(int y, float x, float z, int direction) {
        if (y < 0 || y > endY) return;

//		avoid IOB exception
        y -= startY;

        if (direction == LEFT) {
            this.leftX[y] = x;
            this.leftZ[y] = z;
            return;
        }

        if (direction == RIGHT) {
            this.rightX[y] = x;
            this.rightZ[y] = z;
        }
    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    public float getLeftX(int y) {
        return leftX[y - startY];
    }

    public float getRightX(int y) {
        return rightX[y - startY];
    }

    public float getLeftZ(int y) {
        return leftZ[y - startY];
    }

    public float getRightZ(int y) {
        return rightZ[y - startY];
    }
}

// code for comp261 assignments
