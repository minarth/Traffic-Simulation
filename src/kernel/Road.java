package kernel;

import utils.Direction;
import utils.Position;

/**
 * Road
 * usually consists of two queues representing
 * two directions of driving
 *
 * @author munchmar
 */
public class Road {
    // pointers to crossroads on the ends of the street
    private Cross start;  // North or West
    private Cross end; // South or East
    private Direction toEnd;
    private Direction toStart;
    private Position initPosStart;
    private Position initPosEnd;
    private Queue fromNorthEast;
    private Queue fromSouthWest;
    private double avgStartWaitTime = 0;
    private double avgEndWaitTime = 0;

    /**
     * Constructor
     *
     * @param start Crossroad or turn on the start 
     * @param end Crossroad or turn on the end
     * @param isNorthSouth Boolean if the street is vertical
     */
    public Road(Cross start, Cross end, boolean isNorthSouth) {
        this.start = start;
        this.end = end;
        if (isNorthSouth) {
            toStart = Direction.SOUTH;
            toEnd = Direction.NORTH;
        } else {
            toEnd = Direction.EAST;
            toStart = Direction.WEST;
        }
        this.initPosStart = start.getPosition();
        this.initPosEnd = end.getPosition();

        this.fromNorthEast = new Queue(initPosStart.copy(), toEnd, initPosEnd.copy());
        this.fromSouthWest = new Queue(initPosEnd.copy(), toStart, initPosStart.copy());
    }

    /**
     * Add vehicle on the start of street
     * so on the southern or the eastern end
     */
    public void addToStart(Vehicle v) {
        if (end instanceof CrossRoad) {
            CrossRoad tmp = (CrossRoad) end;
            v.setWrongDir(translateIntToDir(tmp.getTypeOfCrossing()));
        }

        fromNorthEast.addVehicle(v);
    }

    /**
     * Add vehicle on the end of street
     * so on the northern or the western end
     */
    public void addToEnd(Vehicle v) {
        if (start instanceof CrossRoad) {
            CrossRoad tmp = (CrossRoad) start;
            v.setWrongDir(translateIntToDir(tmp.getTypeOfCrossing()));
        }
        fromSouthWest.addVehicle(v);
    }

    /**
     * Removes vehicle from the start
     * so from the souther or eastern end
     */
    public Vehicle getVehicleStart() {
        if (fromNorthEast.vehicleOnEnd()) {
            actualize();
            return fromNorthEast.removeVehicle();
        }
        return null;
    }

    /**
     * removes vehicle from the end
     * so from the northern or western end
     */
    public Vehicle getVehicleEnd() {
        if (fromSouthWest.vehicleOnEnd()) {
            actualize();
            return fromSouthWest.removeVehicle();
        }
        return null;
    }

    // actualize waiting time statistics
    private void actualize() {
        avgStartWaitTime = fromNorthEast.getAvgWaitTime();
        avgEndWaitTime = fromSouthWest.getAvgWaitTime();
    }

    // translat integer into direction
    private Direction translateIntToDir(int n) {
        Direction d;
        switch (n) {
            case 0:
                d = Direction.NORTH;
                break;
            case 1:
                d = Direction.WEST;
                break;
            case 2:
                d = Direction.SOUTH;
                break;
            case 3:
                d = Direction.EAST;
                break;
            default:
                d = null;
                break;
        }
        return d;
    }

    /**
     * Is the vehicle on Northern/eastern end
     */
    public boolean vehicleOnNEEnd() {
        return fromNorthEast.vehicleOnEnd();
    }

    /**
     * Is the vehicle on the Southern/Western end
     */
    public boolean vehicleOnSWEnd() {
        return fromSouthWest.vehicleOnEnd();

    }

    /**
     * Is the vehicle on Northern/eastern start
     */
    public boolean vehicleOnNEStart() {
        return fromNorthEast.vehicleOnStart();
    }

    /**
     * Is the vehicle on the Southern/Western start
     */
    public boolean vehicleOnSWStart() {
        return fromSouthWest.vehicleOnStart();
    }

    /**
     * Returns average waiting time on the start
     */
    public double getAvgStartWaitTime() {
        return avgStartWaitTime;
    }

    /**
     * Returns average waiting time on the end
     */
    public double getAvgEndWaitTime() {
        return avgEndWaitTime;
    }

    /**
     * Returns next direction for the vehicle on the end of Northern/Eastern directin
     */
    public Direction getNEFirstDirection() {
        return fromNorthEast.getFirstDirection();
    }

    /**
     * Returns next direction for the vehicle on the end of Southern/Western directin
     */
    public Direction getSWFirstDirection() {
        return fromSouthWest.getFirstDirection();
    }

    @Override
    public String toString() {
        return "NW:" + fromNorthEast + ", SE:" + fromSouthWest;
    }
}
