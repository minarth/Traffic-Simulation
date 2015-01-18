package kernel;

import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Direction;
import utils.Position;

/**
 * Turns
 * Very similar to cross roads, except they connect only
 * 2 streets, so they always transfer vehicles from one street
 * to another - if there is space for another vehicle
 *
 * @author munchmar
 */
public class Turn implements Runnable, Cross {

    private Position position;
    private Road westEastRoad;
    private Direction westEastDirection;
    private Road northSouthRoad;
    private Direction northSoutDirection;
    private boolean isRunning = true;

    /**
     * Setting position of upper left corner of turn
     */
    public Turn(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    // transfer vehicle from one street to another
    private void transferVehicle() {
        Vehicle v1;
        Vehicle v2;
        if (northSoutDirection.equals(Direction.SOUTH)) {
            v1 = northSouthRoad.getVehicleStart();
        } else {
            v1 = northSouthRoad.getVehicleEnd();
        }

        if (westEastDirection.equals(Direction.WEST)) {
            v2 = westEastRoad.getVehicleStart();
        } else {
            v2 = westEastRoad.getVehicleEnd();
        }

        if (v1 != null) {
            if (westEastDirection == Direction.EAST) {
                westEastRoad.addToStart(v1);
            } else {
                westEastRoad.addToEnd(v1);
            }
        }

        if (v2 != null) {
            if (northSoutDirection == Direction.NORTH) {
                northSouthRoad.addToStart(v2);
            } else {
                northSouthRoad.addToEnd(v2);
            }
        }

    }

    /**
     * Set road to given direction
     */
    @Override
    public void setRoad(Direction direction, Road r) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            westEastDirection = direction;
            westEastRoad = r;
        } else {
            northSoutDirection = direction;
            northSouthRoad = r;
        }
    }

    // this is the run method of turn thread
    @Override
    public void run() {
        while (isRunning) {
            transferVehicle();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Turn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Stopping turn thread
     */
    public void stop() {
        isRunning = false;
    }
}
