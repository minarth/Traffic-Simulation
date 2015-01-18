package kernel;

import utils.*;

/**
 * Implementation of linked list (queue)
 * One list for one direction of driving in the street
 * @author munchmar
 */
public class Queue {

    private int countOfVehicles = 0;
    private VehicleNode first;
    private VehicleNode last;
    private Position initPosition;
    private Position endPosition;
    private Direction direction;
    private long vehiclesThrough = 0;
    private double totalWaitTime = 0;

    /**
     * Queue initialization
     *
     * @param initPosition initial position of vehicle on the street
     * @param direction direction of movement of vehicle in this queuq
     * @param endPosition end position of vehicle in this queue
     */
    public Queue(Position initPosition, Direction direction, Position endPosition) {
        this.first = null;
        this.last = null;
        this.direction = direction;
        this.initPosition = initPosition;
        this.endPosition = endPosition;
        switch (this.direction) {
            case NORTH:
                this.initPosition.changePosition(Constants.getHALF_OF_STREET(), -Constants.getVEHICLE_WIDTH() / 2);
                this.endPosition.changePosition(Constants.getHALF_OF_STREET(), Constants.getSTREET_WIDTH());
                break;
            case EAST:
                this.initPosition.changePosition(Constants.getSTREET_WIDTH() - Constants.getVEHICLE_WIDTH() / 2, Constants.getHALF_OF_STREET());
                this.endPosition.changePosition(-Constants.getVEHICLE_HEIGHT(), Constants.getHALF_OF_STREET());
                break;
            case SOUTH:
                this.initPosition.changePosition(Constants.getSTREET_MARGIN(), Constants.getVEHICLE_WIDTH() / 2 + Constants.getSTREET_WIDTH());
                this.endPosition.changePosition(Constants.getSTREET_MARGIN(), -Constants.getVEHICLE_HEIGHT());
                break;
            case WEST:
                this.initPosition.changePosition(-Constants.getVEHICLE_WIDTH() / 2, Constants.getSTREET_MARGIN());
                this.endPosition.changePosition(Constants.getSTREET_WIDTH(), Constants.getSTREET_MARGIN());
                break;
        }
    }

    /**
     * Adding vehicle to the end of queue
     *
     * @param v vehicle to be add
     */
    public void addVehicle(Vehicle v) {
        VehicleNode tmp = new VehicleNode(v, initPosition.copy(), null, direction, endPosition.copy());
        if (first == null || last == null) {
            first = tmp;
            last = first;

        } else {
            VehicleNode l = last;
            last.setBehind(tmp);
            last = tmp;
            tmp.setInFront(l);
        }
        new Thread(tmp).start();
        countOfVehicles++;
        vehiclesThrough++;
    }

    /**
     * Remove vehicle from the end of queue and return pointer to it
     */
    public Vehicle removeVehicle() { 
        if (first == null) {
            return null;
        }
        
        Vehicle tmp = first.getVehicle();
        totalWaitTime += first.getWaitTime();
        vehiclesThrough++;
        VehicleNode v = first.getBehind();
        first.end();
        
        if (first.equals(last) || v == null) {
            first = null;
            last = null;
        } else  {
            first = v;
            first.setInFront(null);
        } 
        
        countOfVehicles--;
        return tmp;

    }

    /**
     * Returns average waiting time for vehicles passed in this queue
     */
    public double getAvgWaitTime() {
        if (vehiclesThrough == 0) {
            return 0;
        } else {
            double tmp = totalWaitTime / vehiclesThrough;
            totalWaitTime = 0;
            vehiclesThrough = 0;
            return tmp;
        }
    }

    /**
     * Returns if the queue is empty
     */
    public boolean isEmpty() {
        return (first == null);
    }

    /**
     * Checks if the vehicle is on the beggining of street
     */
    public boolean vehicleOnStart() {
        if (first == null) {
            return false;
        } else {
            Position tmp = last.getPosition();
            if (direction == Direction.SOUTH) {
                return (tmp.getPosY() - Constants.getVEHICLE_HEIGHT()
                        - initPosition.getPosY()) < 0;
            } else if (direction == Direction.NORTH) {
                return (initPosition.getPosY() - Constants.getVEHICLE_HEIGHT()
                        - tmp.getPosY()) < 0;
            } else if (direction == Direction.EAST) {
                return (initPosition.getPosX() - Constants.getVEHICLE_HEIGHT()
                        - last.getPosition().getPosX()) < 0;
            } else {
                return (tmp.getPosX() - Constants.getVEHICLE_HEIGHT()
                        - initPosition.getPosX()) < 0;
            }
        }
    }

    /**
     * Checks if the vehicle is on the beggining of street
     */
    public boolean vehicleOnEnd() {
        if (first == null) {
            return false;
        }

        if (first.getPosition().equals(endPosition)) {

            return true;
        }
        return false;
    }

    /**
     * Get next direction of first vehicle
     */
    public Direction getFirstDirection() {
        if (first != null) {
            return first.getVehicle().nextDirection();
        }
        return null;
    }

    @Override
    public String toString() {
        return "" + countOfVehicles;
    }
}