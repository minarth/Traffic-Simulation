package kernel;

import application.LightSliderListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

/**
 * Roadsite is class that encapsulate roads, crossroads and turns.
 * It is the traffic part of the city
 * @author minarth
 */
public class RoadSite {

    private Road[] roads;
    private CrossRoad[] crossings;
    private Turn[] turns;
    private double lastWaitStats = 0;

    public RoadSite(ArrayList<Road> r, ArrayList<CrossRoad> cr, ArrayList<Turn> t) {
        // Loading crossroads
        crossings = new CrossRoad[cr.size()];
        crossings = cr.toArray(crossings);

        // Loading turns
        turns = new Turn[t.size()];
        turns = t.toArray(turns);

        // Loading roads
        roads = new Road[r.size()];
        roads = r.toArray(roads);

        runCross();

    }

    // Run crossings and turns in separate threads
    private void runCross() {
        for (CrossRoad c : crossings) {
            new Thread(c).start();
        }
        
        for (Turn t : turns) {
            new Thread(t).start();
        }
    }

    // Stop all crossings and turns
    public void stopAll() {
        for (Turn tx : turns) {
            tx.stop();
        }

        for (CrossRoad cr : crossings) {
            cr.stop();
        }
    }

    /**
     * Adding vehicle on the start of the road
     *
     * @param road id of road, where to push vehicle
     * @param v pointer to vehicle, which we push on the road
     */
    public void addToStart(int road, Vehicle v) {
        roads[road].addToStart(v);
    }

    /**
     * Adding vehicle on the end of the road
     *
     * @param road id of road, where to push vehicle
     * @param v pointer to vehicle, which we push on the road
     */
    public void addToEnd(int road, Vehicle v) {
        roads[road].addToEnd(v);
    }

    /**
     * Returns JPanel with statistics
     */
    public JPanel getStats() {
        return new StatPanel();
    }

    /**
     * Returns last average waiting time on lights
     *
     * @return average waiting time on lights
     */
    public double getLastWaitStats() {
        return lastWaitStats;
    }

    /**
     * JPanel for rendering statistics of lights
     */
    public class StatPanel extends JPanel {

        private JLabel[][] crossL = new JLabel[crossings.length][5];
        private JSlider[] sliders = new JSlider[crossings.length];
        public StatPanel() {
            this.setLayout(new GridLayout(0, 2));
            Timer timer = new Timer(400, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();

                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();
            for (int i = 0; i < crossings.length; i++) {
                crossL[i][0] = new JLabel("Lights ID: " + i);
                crossL[i][1] = new JLabel();
                crossL[i][2] = new JLabel();
                crossL[i][3] = new JLabel("Change delay of lights:");
                crossL[i][4] = new JLabel();
                sliders[i] = new JSlider(200, 1000, crossings[i].getDelay());
                sliders[i].addChangeListener(new LightSliderListener(crossings[i]));
                sliders[i].setMajorTickSpacing(200);
                sliders[i].setMinorTickSpacing(50);
                sliders[i].setPaintTicks(true);
                sliders[i].setPaintLabels(true);
                
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            double tmp = 0;
            for (int i = 0; i < crossings.length; i++) {
                tmp += crossings[i].avgWaitTime();
                crossL[i][1].setText(String.format("Avg. wait time: %.3f", crossings[i].avgWaitTime()));
                crossL[i][2].setText(String.format("Delay of switching: %d", crossings[i].getDelay()));
                //sliders[i].setValue(crossings[i].getDelay());
                add(crossL[i][0]);
                add(crossL[i][4]);
                add(crossL[i][1]);
                add(crossL[i][2]);
                add(crossL[i][3]);
                add(sliders[i]);
            }
            lastWaitStats = tmp / crossings.length;
            

        }
    }
}