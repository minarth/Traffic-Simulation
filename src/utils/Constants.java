package utils;

/**
 * Statical class for important constants of program
 *
 * @author munchmar
 */
public class Constants {
    
    private static final int STREET_WIDTH = 24; // Sirka ulice
    private static final Dimensions VEHICLE_DIMENSIONS = new Dimensions(16, 8); // Rozmery auta

    /**
     * Width of the street
     */
    public static int getSTREET_WIDTH() {
        return STREET_WIDTH;
    }

    /**
     * Half of the street width
     */
    public static int getHALF_OF_STREET() {
    	return 14;
    }
    
    /**
     * Margin of the street
     * so the vehicles don't run exactly on edges
     */
    public static int getSTREET_MARGIN() {return 2;}
    
    
    /**
     * Dimensions of the vehicles
     */
    public static Dimensions getVEHICLE_DIMENSIONS() {
        return VEHICLE_DIMENSIONS;
    }
    
    /**
     * Width of the vehicle
     */
    public static int getVEHICLE_WIDTH() {
    	return VEHICLE_DIMENSIONS.getWidth();
    }
    
    /**
     * Height of the vehicle
     */
    public static int getVEHICLE_HEIGHT() {
    	return VEHICLE_DIMENSIONS.getHeight();
    }
    
}
