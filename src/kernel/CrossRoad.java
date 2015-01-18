package kernel;

import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Direction;
import utils.Position;

/**
 * Crossroad class
 * it has pointers to its own lights and roads leading to and from this crossroad
 * 
 * @author munchmar
 */
public class CrossRoad implements Runnable, Cross {

    private Light light;
    private Road[] roads;
    private Position position;
    private int typeOfCrossing;
    private int delay;
    private int loopDelay = 50;
    private boolean isRunning = true;
    private double[] avgWaitTime;

    /**
     * Constructor
     * @param position Position of upper left corner of crossroad
     * @param typeOfCrossing type of crossing, 0-3 is for 3way crossroads, 4 is for full
     * 0 - missing northen street
     * 1 - missing western street
     * 2 - missing southern street
     * 3 - missing eastern street
     * 4 - full 4 way crossing
     * @param initDelay initial time for switching light in [ms]
     */
    public CrossRoad(Position position, int typeOfCrossing, int initDelay) {
        this.avgWaitTime = new double[4];
        this.light = new Light(typeOfCrossing, position);
        this.position = position;
        this.typeOfCrossing = typeOfCrossing;
        this.delay = initDelay;
        roads = new Road[4];
    }

    /**
     * Returns pointer to lights of crossroad
     */
    public Light getLight() {
        return light;
    }

    /**
     * Returns speed of switching the lights
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Method for setting speed of switching lights
     */
    public void setDelay(int d) {
        delay = d;
    }

    /**
     * returns type of crossing
     * 0 - missing northen street
     * 1 - missing western street
     * 2 - missing southern street
     * 3 - missing eastern street
     * 4 - full 4 way crossing
     */
    public int getTypeOfCrossing() {
        return typeOfCrossing;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setRoad(Direction direction, Road r) {
        roads[direction.getValue()] = r;
    }

    // Each cross road runs in separate thread
    @Override
    public void run() {
        int t = 0;
        while (isRunning) {
            transferVehicle();
            
            try {
                Thread.sleep(loopDelay);
                t++;
            } catch (InterruptedException ex) {
                System.out.println("Crossing interrupted");
            }
            
            if (t * loopDelay >= delay) {
                light.switchLight();
                t=0;
            }
        }
    }

    /**
     * Stopping the crossroad thread
     */
    public void stop() {
        isRunning = false;
    }

    /**
    * Method for pulling vehicle from one street and putting
    * it onto second street
    **/
    private void transferVehicle() {
        Vehicle v = null;
        // get street, which has green
        Direction green = light.getGreenDirection();


        /* Picking another vehicle to go through crossing 
        * if green is on NORTH or EAST street, than get
        * vehicle from their south or west end respectively
        * also checks if the target street is empty enough, to put 
        * there another vehicle
        */
        if (green == Direction.NORTH || green == Direction.EAST) {
            if (roads[green.getValue()].vehicleOnSWEnd()
                    && isNextDirectionFree(roads[green.getValue()].getSWFirstDirection())) {
                v = roads[green.getValue()].getVehicleEnd();
            }
        } 
        /*
        * Otherwise, pick vehicle from north or east end
        * check if target street is empty enough to put there another vehicle
        */
        else {
            if (roads[green.getValue()].vehicleOnNEEnd()
                    && isNextDirectionFree(roads[green.getValue()].getNEFirstDirection())) {

                v = roads[green.getValue()].getVehicleStart();
            }
        }

        // if we picked some vehicle to transfer
        if (v != null) {
            // if the vehicle is on the end of its planned route end it
            if (v.onEnd()) {
                Position tmp = new Position(-8, -8);
                v.setPosition(tmp);
            } 
            // else transfer vehicle to its next step of route
            else {
                Direction toDirection = v.goDirection();
                actualize();
                if (roads[toDirection.getValue()] != null) {
                    if (toDirection == Direction.NORTH || toDirection == Direction.EAST) {
                        roads[toDirection.getValue()].addToStart(v);
                    } else {
                        roads[toDirection.getValue()].addToEnd(v);
                    }
                }
            }
        }
    }

    // actualizing crossroad statistics
    private void actualize() {
        if (roads[0] != null) {
            avgWaitTime[0] = roads[0].getAvgEndWaitTime();
        }
        if (roads[1] != null) {
            avgWaitTime[1] = roads[1].getAvgEndWaitTime();
        }
        if (roads[2] != null) {
            avgWaitTime[2] = roads[2].getAvgStartWaitTime();
        }
        if (roads[3] != null) {
            avgWaitTime[3] = roads[3].getAvgStartWaitTime();
        }

        if (delay >= 220 && avgWaitTime() >= 0.4) {
            delay -= 20;
        } else if (delay <= 680 && avgWaitTime() < 0.1) {
            delay += 20;
        }

    }

    /**
     *
     * @return average waiting time on the crossroad's lights
     */
    public double avgWaitTime() {
        double tmp = 0;
        for (double d : avgWaitTime) {
            tmp += d;
        }

        if (typeOfCrossing == 4) {
            return tmp / 4;
        }

        return tmp / 3;
    }

    /*
    * Checks if the target street is free
    */
    private boolean isNextDirectionFree(Direction d) {
        if (d != null) {
            if (d == Direction.NORTH || d == Direction.EAST) {
                return !roads[d.getValue()].vehicleOnNEStart();
            } else {
                return !roads[d.getValue()].vehicleOnSWStart();
            }
        }
        return true;
    }
}
