package utils;

/**
 * Dimensions of objects in the city
 *  vehicles, buildings, roads
 *
 * @author munchmar
 */
public class Dimensions {
    private int height;     // vyska
    private int width;      // sirka

    public Dimensions(int height, int width) {
        this.height = height;
        this.width = width;
    }
    
    /**
     * @return height of an object
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * @return width of an object
     */
    public int getWidth() {
        return width;
    }

    /**
     * Switches height with the width
     * important for rotating vehicles
     */
    public void switchDim() {
        int tmp = height;
        height = width;
        width = tmp;
    }
    
    /**
     * @return copy of an Dimensions
     */
    public Dimensions copy() {
        return new Dimensions(height, width);
    }
}
