package application;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import kernel.CrossRoad;

/**
 * Slider in GUI
 *
 * @author minarth
 */
public class LightSliderListener implements ChangeListener {

    private CrossRoad cr;

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int delay = (int) source.getValue();
            cr.setDelay(delay);
        }
    }

    public LightSliderListener(CrossRoad cr) {
        this.cr = cr;
    }
}
