package renderer;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
	private int startY, endY;
	private float[] leftX, rightX, leftZ, rightZ;

	public EdgeList(int startY, int endY) {
		if(startY < endY) {
			this.startY = startY;
			this.endY = endY;
		}else{
			this.startY = endY;
			this.endY = startY;
		}

		int heightDifference = (endY - startY) + 1; //Accomodates the equal too end y value

		leftX 	= new float[heightDifference];
		rightX 	= new float[heightDifference];
		leftZ 	= new float[heightDifference];
		rightZ 	= new float[heightDifference];
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
