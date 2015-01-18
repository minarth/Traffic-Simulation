package utils;

/**
 * Enum for direction in the city
 * directions of the streets and vehicles 
 *
 * @author munchmar
 */
public enum Direction {
    NORTH(0), WEST(1), SOUTH(2), EAST(3);
    private int value;
    
    private Direction(int value) {
        this.value = value;
    }

    /**
     * @return direction transalted into integer
     */
    public int getValue() {
    	return value;
    }
    
}
