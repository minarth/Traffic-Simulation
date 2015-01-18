package kernel;

import java.util.Objects;
import utils.*;

/**
 * Class for node representing vehicle in the linked list manages movement and thread of vehicle
 * each node keeps pointer on the node before and behind itself
 * @author munchmar
 */
public class VehicleNode implements Runnable {

    private long totalTime = 0;
    private Position initPosition;
    private double waitTime = 0;
    private Position endPosition;
    private Vehicle vehicle;
    private Position position;
    private VehicleNode inFront;
    private VehicleNode behind;
    private int delay;
    private int currentSpeed;
    private Direction directionOfMove;
    private boolean isRunning;

    /**
     * Constructor for node
     */
    public VehicleNode(Vehicle vehicle, Position position, VehicleNode inFront,
            Direction directionOfMove, Position endPosition) {

        this.vehicle = vehicle;
        this.isRunning = true;
        this.position = position.copy();
        this.initPosition = position.copy();
        this.inFront = inFront;
        this.directionOfMove = directionOfMove;

        this.behind = null;
        this.delay = vehicle.getDelay();
        this.currentSpeed = 1;
        this.endPosition = endPosition;

        if (directionOfMove == Direction.SOUTH || directionOfMove == Direction.NORTH) {
            this.vehicle.setPosition(position.copy());
            if (!this.vehicle.facingNorthSouth()) {
                this.vehicle.rotate();
            }
        } else {
            this.vehicle.setPosition(position.copy());
            if (this.vehicle.facingNorthSouth()) {
                this.vehicle.rotate();

            }
        }
    }

    /**
     * @return pointer to the vehicle of the node
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Sets pointer to the vehicle behind this one
     */
    public void setBehind(VehicleNode v) {
        behind = v;
    }

    /**
     *
     * @return pointer to the vehicle behind this one
     */
    public VehicleNode getBehind() {
        return behind;
    }

    /**
     * Sets pointer to the vehicle before this one
     *
     */
    public void setInFront(VehicleNode inFront) {
        this.inFront = inFront;
    }

    /**
    * This method manages changes on speed of vehicle due to acceleration
    */
    private void accelerate() {
        if (currentSpeed + vehicle.getAcceleration() <= vehicle.getMaxSpeed()) {
            currentSpeed += vehicle.getAcceleration();
        } else if (currentSpeed < vehicle.getMaxSpeed()) {
            currentSpeed = vehicle.getMaxSpeed();
        }
    }

    /**
    * This method manages movement of vehicle
    */
    private void move() {
        if (inFront != null) {
            if (getDistanceOfNextVehicle() > vehicle.getMinDistance()) {
                accelerate();
            } else if (getDistanceOfNextVehicle() == vehicle.getMinDistance()) {
                currentSpeed = getCurrentSpeedOfNextVehicle();
            } else {
                currentSpeed = 0;
            }
        } else {

            if (isEnd()) {
                currentSpeed = 0;
            } else if (nearTheEnd()) {
                currentSpeed = distanceFromEnd();
            } else {
                accelerate();
            }
        }

        if (currentSpeed == 0) {
            waitTime++;
        }
        totalTime++;
        position.moveInDirection(directionOfMove, currentSpeed);
        vehicle.setPosition(position);
    }

    /**
     * Returns time of waiting in queue
     */
    public double getWaitTime() {
        waitTime *= delay;
        // System.out.println(waitTime/1000);
        return waitTime / 1000;
    }

    /**
    * Method for running VehicleNode in a separated thread
    */
    @Override
    public void run() {
        while (isRunning) {
            try {
                move();
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println("Failed to go");
            }
        }
    }

    /**
     * Returns actual position of the vehicle
     */
    public Position getPosition() {
        return position.copy();
    }

    /**
     * Returns actual speed of the vehicle
     */
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    /**
    * Returns distance from the behicle in front of this
    */
    private int getDistanceOfNextVehicle() {
        if (inFront != null) {
            Position nextVehPosition = inFront.getPosition();
            if (directionOfMove == Direction.EAST) {
                return nextVehPosition.getPosX() - Constants.getVEHICLE_HEIGHT() - position.getPosX();

            } else if (directionOfMove == Direction.WEST) {
                return position.getPosX() - (nextVehPosition.getPosX() + Constants.getVEHICLE_HEIGHT());

            } else if (directionOfMove == Direction.NORTH) {
                return position.getPosY() - (nextVehPosition.getPosY() + Constants.getVEHICLE_HEIGHT());
            } else {
                return nextVehPosition.getPosY() - Constants.getVEHICLE_HEIGHT() - position.getPosY();
            }
        } else {
            return Integer.MAX_VALUE;
        }

    }

    /**
    *  @return current speed of the vehicle in front of this one
    */
    private int getCurrentSpeedOfNextVehicle() {
        if (inFront != null) {
            return inFront.currentSpeed;
        } else {
            return 0;
        }
    }

    /**
    * Important, so the vehicle doesn't overrun the crossroad
    * @return Is the vehicle near the end of street
    */
    private boolean nearTheEnd() {
        int distance = position.difference(directionOfMove, endPosition);
        if (distance > currentSpeed && distance >= 0) {
            return false;
        }
        return true;
    }

    /**
    * @return is the vehicle on the end of the street
    */
    private boolean isEnd() {
        vehicle.addDistance(initPosition.difference(directionOfMove, endPosition), (totalTime));
        return (position.difference(directionOfMove, endPosition) == 0);
    }

    /**
    * @return distance from the end of the street
    */
    private int distanceFromEnd() {
        return position.difference(directionOfMove, endPosition);
    }

    /**
     * Ends the thrad of this node
     */
    public void end() {
        isRunning = false;
    }

    @Override
    public String toString() {
        return "VehicleNode{" + "vehicle=" + vehicle + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.initPosition);
        hash = 79 * hash + Objects.hashCode(this.vehicle);
        hash = 79 * hash + (this.directionOfMove != null ? this.directionOfMove.hashCode() : 0);
        hash = 79 * hash + (this.isRunning ? 1 : 0);
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
        final VehicleNode other = (VehicleNode) obj;
        if (!Objects.equals(this.initPosition, other.initPosition)) {
            return false;
        }
        if (!Objects.equals(this.vehicle, other.vehicle)) {
            return false;
        }
        if (this.directionOfMove != other.directionOfMove) {
            return false;
        }
        if (this.isRunning != other.isRunning) {
            return false;
        }
        return true;
    }
}
