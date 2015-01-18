package kernel;

import java.awt.*;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import utils.*;

/**
 * Vehicle class
 * this keeps all the important info about vehicle like position, movement parameters
 * and directions for planned route
 * object from this class is nextly packed in VehicleNode object, which is runnable and
 * services each vehicle as separated thread in the city
 *
 * @author munchmar
 */
public class Vehicle {

    private static int vehicleCount = 0;
    private int id;
    private Position position;
    private Dimensions dimensions;
    private int delay;
    private Color color;
    private int maxSpeed;
    private int cityMaxSpeed;
    private boolean northSouth;
    private int minDistance;
    private int acceleration;
    private Direction wrongDir = null;  // it is set to not null, when vehicle should end its journey on next crossroad
    private Direction nextRandDir = null; // used for routing vehicle with random path
    private boolean randomVehicle = true;
    private int[] directions;
    private int routeCounter;
    private int startRoad;
    private long distance = 0;  // distance traveled by vehicle
    private long time = 0;      // time for which vehicle is on the road
    private boolean onEnd = false;

    public Vehicle(Dimensions dimensions, int delay, int maxSpeed, int minDistance, int acceleration, int startRoad) {
        this.dimensions = dimensions;
        this.delay = delay;
        this.maxSpeed = maxSpeed;
        this.cityMaxSpeed = maxSpeed;
        this.minDistance = minDistance;
        this.acceleration = acceleration;
        this.id = vehicleCount++;
        Random rand = new Random();
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        this.startRoad = startRoad;
    }

    /**
     * Adding up distance and time for crossing another cross-road
     * it is actualized every cross-road
     *
     * @param distance traveled distance so far
     * @param time travelled time so far
     */
    public void addDistance(long distance, long time) {
        this.distance += distance;
        this.time += time;
    }

    /**
     * Returns average speed of vehicle
     */
    public double getAvgSpeed() {
        if (time != 0) {
            return distance / time;
        }
        return 0;
    }

    /**
     * Sets maximal speed in the city
     * each vehicle keeps this variable on its own
     */
    public void setCityMaxSpeed(int max) {
        cityMaxSpeed = max;
    }

    /**
     * Returns id of the origin street
     *
     * @return id of the street where vehicle is spawned
     */
    public int getStartRoad() {
        return startRoad;
    }

    /**
     *
     * @return actuall position of vehicle
     */
    public synchronized Position getPosition() {
        return position;
    }

    /**
     * sets position of a vehicle
     */
    public synchronized void setPosition(Position pos) {
        position = pos;

        // this variable is important for rotation of vehicle
        this.northSouth = (dimensions.getHeight() > dimensions.getWidth());
    }

    /**
     * Returns time in which the vehicle is actualized
     */
    public int getDelay() {
        return delay;
    }

    /**
     *
     * @return returns maximal possible speed for vehicle
     */
    public int getMaxSpeed() {
        return Math.min(maxSpeed, cityMaxSpeed);
    }

    /**
     *
     * @return returns minimal possible distance between this vehicle and the next
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * @return returns color of this vehicle
     */
    public Color getColor() {
        return color;
    }

    /**
     *
     * @return returns acceleration of vehicle
     */
    public int getAcceleration() {
        return acceleration;
    }

    /**
     * Each vehicle is represented as a rectangle
     * and this method is for rotating vehicle's rectangle when needed
     */
    public void rotate() {
        northSouth = !northSouth;
        dimensions.switchDim();
    }

    /**
    * Function translating integers from route plan into direction
    */
    private Direction nextTurn(int route) {
        Direction d;
        switch (route) {
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
                d = Direction.SOUTH;
                break;
        }

        return d;
    }

    /**
     * Sets route plan for vehicle
     */
    public void setRoute(int[] route) {
        directions = route;
        randomVehicle = false;
    }

    /**
     * Returns if the vehicle is on the end of its journey
     */
    public boolean onEnd() {
        return onEnd;
    }

    /**
     * Returns next direction from vehicle plan and steps on the next part of plan
     * for randomly drived vehicles this method generates next random possible turn
     */
    public synchronized Direction goDirection() {
        if (!randomVehicle) {
            Direction next = randomDirection();
            if (nextTurn(directions[routeCounter]).equals(wrongDir)) {
                onEnd = true;
            } else {
                next = nextTurn(directions[routeCounter++]);
            }
            if (routeCounter >= directions.length) {
                onEnd = true;
            }
            return next;
        } else {
            return nextRandDir;
        }
    }

    /**
     * Returns next directions, dont do step on another, as the method goDirection()
     */
    public synchronized Direction nextDirection() {
        if (!randomVehicle) {
            if (onEnd) {
                return randomDirection();
            }
            Direction next = randomDirection();
            if (nextTurn(directions[routeCounter]).equals(wrongDir)) {
                onEnd = true;
            } else {
                next = nextTurn(directions[routeCounter]);
            }
            return next;
        } else {
            return randomDirection();
        }
    }

    /**
    * computes random possible direction for vehicle
    */
    private Direction randomDirection() {
        if (wrongDir == null) {
            nextRandDir = nextTurn((int) (Math.random() * 4));
            return nextRandDir;
        } else {
            Direction tmp = nextTurn((int) (Math.random() * 4));
            while (tmp == wrongDir) {
                tmp = nextTurn((int) (Math.random() * 4));
            }
            nextRandDir = tmp;
            return tmp;
        }
    }

    /**
     * Sets next direction, which is not possible - so the vehicle will end on next crossroad
     */
    public void setWrongDir(Direction wrongDir) {
        this.wrongDir = wrongDir;
    }

    /**
     * @return id of vehicle
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return if the vehicle is going on the vertical street, than returns true
     */
    public boolean facingNorthSouth() {
        return northSouth;
    }

    /**
     * Returns size of vehicle
     */
    public Dimensions getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return "Vehicle" + "id=" + id + ", color=" + color;
    }
}
