package kernel;

import utils.*;

/**
 * Interface for unifying turns and cross-roads
 *
 * @author munchmar
 */
public interface Cross {

    public Position getPosition();

    public void setRoad(Direction d, Road r);
}
