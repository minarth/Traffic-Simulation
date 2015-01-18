package kernel;

import utils.Position;

/**
 * Building class
 * each building has size (height and width) and position 
 *
 * @author munchmar
 */
public class Building {

    private Position position;
    private int height;
    private int width;

    /**
     * constructor for new building
     *
     * @param position position of upper left corner
     * @param height of a building
     * @param width of a building
     */
    public Building(Position position, int height, int width) {
        this.position = position;
        this.height = height;
        this.width = width;
    }

    /**
     * returns position of upper left corner
     */
    public Position getPosition() {
        return position;
    }

    /**
     * returns coordinate X of upper left corner
     */
    public int getPositionX() {
        return position.getPosX();
    }

    /**
     * returns coordinate Y of upper left corner
     */
    public int getPositionY() {
        return position.getPosY();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
