package application;

import java.util.ArrayList;
import java.util.List;
import kernel.*;

/**
 * Main class of the assignment
 * it runs input loading dialog
 * @author munchmar
 */
public class Main {

    public static void main(String[] args) {
        Reader r = new Reader();
        
    }
    
    /**
     * Method for running the simulation
     * @param width of the city
     * @param height of the city
     * @param b - list of buildings
     * @param v - list of vehicles
     * @param r - list of roads
     * @param cr - list of crossroads
     * @param t - list of turns
     */
    public static void simulate(int width, int height, ArrayList<Building> b,
            ArrayList<Road> r, 
            ArrayList<CrossRoad> cr, ArrayList<Turn> t) {
        City c = new City("Semestralni mesto", width, height, b, r, cr, t);
    }
}
