package kernel;

import utils.Direction;
import utils.Position;

/**
 * Lights on the crossroad
 *
 * @author munchmar
 */
public class Light {
    
    private static int countOfLights = 0;
    private int id;
    private int typeOfCrossing;
    private Direction greenDirection;
    private Position position;

    /**
     * Constructor
     * @param typeOfCrossing type of crossing, 0-3 is for 3way crossroads, 4 is for full
     * 0 - missing northen street
     * 1 - missing western street
     * 2 - missing southern street
     * 3 - missing eastern street
     * 4 - full 4 way crossing
     * @param initDelay initial time for switching light in [ms]
     */
    public Light(int typeOfCrossing, Position position) {
        this.typeOfCrossing = typeOfCrossing;
        this.position = position;
        this.greenDirection = Direction.EAST; // initialize green on the east
        id = countOfLights++; // id of the lights
        switchLight();
    }
    
    // position, where to write ID in the render
    public Position positionOfId() {
        return new Position(position.getPosX()+8, position.getPosY()+18);
    }
    
    public int getId() {
        return id;
    }

    /**
     * returns (direction) street which can go
     */
    public Direction getGreenDirection() {
        return greenDirection;
    }

    /**
     * Switches to next direction with respect to type of CrossRoad
     */
    public void switchLight() {

        nextDirection();
        switch (typeOfCrossing) {
            case 0:
                if (greenDirection == Direction.NORTH) {
                    nextDirection();
                }
                break;
            case 1:
                if (greenDirection == Direction.WEST) {
                    nextDirection();
                }
                break;
            case 2:
                if (greenDirection == Direction.SOUTH) {
                    nextDirection();
                }
                break;
            case 3:
                if (greenDirection == Direction.EAST) {
                    nextDirection();
                }
                break;
        }
    }

    private void nextDirection() {
        switch (greenDirection) {
            case NORTH:
                greenDirection = Direction.WEST;
                break;
            case WEST:
                greenDirection = Direction.SOUTH;
                break;
            case SOUTH:
                greenDirection = Direction.EAST;
                break;
            case EAST:
                greenDirection = Direction.NORTH;
                break;

        }
    }

    /**
     * Returns position of green dot, which signalizes open street
     * this dot is rendered by City object
     */
    public Position positionOfDot() {
        Position pos;
        switch (greenDirection) {
            case NORTH:
                pos = new Position(this.position.getPosX() + 9, this.position.getPosY());
                break;
            case WEST:
                pos = new Position(this.position.getPosX(), this.position.getPosY() + 9);
                break;
            case SOUTH:
                pos = new Position(this.position.getPosX() + 9, this.position.getPosY() + 24);
                break;
            case EAST:
                pos = new Position(this.position.getPosX() + 24, this.position.getPosY() + 9);
                break;
            default:
                pos = new Position(this.position.getPosX(), this.position.getPosY());

                break;
        }

        return pos;
    }
}
