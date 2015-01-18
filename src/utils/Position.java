package utils;

/**
 * Positions of objects in the city
 *
 * @author munchmar
 */
public class Position {

    private int posX;
    private int posY;

    public Position(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Returns copy of an position
     */
    public Position copy() {
        return new Position(posX, posY);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    /**
     * Recalculate position in the direction for the given number of pixels
     *
     * @param direction direction of movement
     * @param delta length of the shift
     */
    public void moveInDirection(Direction direction, int delta) {
        switch (direction) {
            case NORTH:
                posY -= delta;
                break;

            case SOUTH:
                posY += delta;
                break;

            case WEST:
                posX -= delta;
                break;

            case EAST:
                posX += delta;
                break;
            default:
                System.out.println(direction);
                break;
        }
    }

    @Override
    public String toString() {
        return "[" + posX + ", " + posY + "]";
    }

    /**
     * Function for calculating distance between two points
     *
     * @param d direction in which we calculate difference
     * @param p2 position of the second point
     * @return distance of points
     */
    public int difference(Direction d, Position p2) {
        if (d == Direction.NORTH) {
            return (posY - p2.getPosY());
        } else if (d == Direction.SOUTH) {
            return (p2.getPosY() - posY);
        } else if (d == Direction.EAST) {
            return (p2.getPosX() - posX);
        }
        return (posX - p2.getPosX());
    }

    /**
     * Changes position for given x and y
     *
     * @param x integer which is added to X-coordinate
     * @param y integer which is added to Y-coordinate
     */
    public void changePosition(int x, int y) {
        posX += x;
        posY += y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.posX;
        hash = 89 * hash + this.posY;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        if (this.posX != other.posX) {
            return false;
        }
        if (this.posY != other.posY) {
            return false;
        }
        return true;
    }
}
