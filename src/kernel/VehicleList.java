package kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton list keeping all vehicles
 * it is important for managing all vehicles in the city
 *
 * @author munchmar
 */
public class VehicleList implements Iterable<Vehicle> {
    
    private static VehicleList instance;
    private List vehicleList;
    
    private VehicleList() {
        vehicleList = Collections.synchronizedList(new ArrayList<Vehicle>(7));
    }
    
    public static VehicleList getInstance() {
        if (instance == null) {
            instance = new VehicleList();
        }
        return instance;
    }

    @Override
    public synchronized Iterator<Vehicle> iterator() {
        return vehicleList.iterator();
    }
    
    public boolean isEmpty() {
        return vehicleList.isEmpty();
    }
    
    public void add(Vehicle v) {
        vehicleList.add(v);
    }
    
    public void remove(int x) {
        vehicleList.remove(x);
    }
    
    public void clear() {
        vehicleList.clear();
    }
    
    public int size() {
        return vehicleList.size();
    }

    public void remove(Vehicle v) {
        vehicleList.remove(v);
    }
}
