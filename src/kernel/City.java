package kernel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import utils.Dimensions;
import utils.Graph;
import utils.Position;

/**
 * The simulated city, contains and keeps city disposition,
 * vehicles and crossroads with lights
 * This class also contains class cityPanel, which draws city and statistics
 * @author munchmar
 */

public class City extends JFrame {

    private City thisCity = this;
    // size of the city
    private int height;
    private int width;
    private int t = 75; // time step for statistics
    private ArrayList<Building> buildings;
    private VehicleList vehicles = VehicleList.getInstance();
    private ArrayList<CrossRoad> cross;
    private ArrayList<Light> lights = new ArrayList<>(7);
    private RoadSite rs;
    private JFrame frame;
    // graphs for statistics
    private Graph waitGraph = new Graph(0.5, 10, "Average waiting time"); // Average waiting time on lights
    private Graph speedGraph = new Graph(2, 20, "Average speed");  // Average speed of all vehicles
    // variable where vehicle is ported when finishes its task, then its removed
    private final Position endPos = new Position(-8, -8);
    // max speed in the city
    private int maxSpeed;
    private JSlider maxSpeedSlider = new JSlider(1, 19, 19);
    private JPanel graphs = new JPanel(new GridLayout(0, 1));

    public City(String name, int width, int height, ArrayList<Building> b,
            ArrayList<Road> r,
            ArrayList<CrossRoad> cr, ArrayList<Turn> turns) {
        waitGraph.addScore(0);
        buildings = b;
        cross = cr;
        for (CrossRoad crossRoad : cr) {
            addLight(crossRoad.getLight());
        }

        rs = new RoadSite(r, cr, turns);

        for (Vehicle veh : vehicles) {
            rs.addToStart(veh.getStartRoad(), veh);
        }
        this.width = width;
        this.height = height;

        maxSpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int speed = (int) source.getValue();
                    thisCity.setMaxSpeed(speed);
                }
            }
        });
        maxSpeedSlider.setMajorTickSpacing(5);
        maxSpeedSlider.setMinorTickSpacing(1);
        maxSpeedSlider.setPaintTicks(true);
        maxSpeedSlider.setPaintLabels(true);

        
        graphs.add(waitGraph);
        graphs.add(speedGraph);

        frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(new CityPanel(), BorderLayout.CENTER);
        frame.add(rs.getStats(), BorderLayout.WEST);
        frame.add(graphs, BorderLayout.EAST);
        //frame.add(new JLabel("Slide"), BorderLayout.SOUTH);
        frame.add(maxSpeedSlider, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Adds street lights into list
    // needed for rendering
    private void addLight(Light l) {
        lights.add(l);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }

    /**
     * Setting max speed in the city
     * It has to be done for every vehicle
     */
    public void setMaxSpeed(int x) {
        maxSpeed = x;
        for (Vehicle v : vehicles) {
            v.setCityMaxSpeed(maxSpeed);
        }
    }

    /**
     * Returns list of all crossroads
     */
    public ArrayList<CrossRoad> getCross() {
        return cross;
    }

    /**
     * JPanel rendering traffic in the city
     */
    public class CityPanel extends JPanel {

        private long timeLast = 0, timeLast1 = 0;
        private double avgSpeed;
        private int activeVehicles;

        public CityPanel() {
            Timer timer = new Timer(t, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();
            
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Position tmp;
            Dimensions d;
            timeLast += t;
            timeLast1 += t;
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            for (Building building : buildings) {
                g2d.setColor(Color.GRAY);
                g2d.fillRect(building.getPositionX(),
                        building.getPositionY(), building.getWidth(),
                        building.getHeight());
            }
            avgSpeed = 0;
            activeVehicles = 0;
            for (Vehicle v : vehicles) {
                tmp = v.getPosition();
                if (!endPos.equals(tmp)) {
                    avgSpeed += v.getAvgSpeed();
                    activeVehicles++;
                    g2d.setColor(v.getColor());

                    d = v.getDimensions();
                    g2d.fillRect(tmp.getPosX(), tmp.getPosY(), d.getWidth(), d.getHeight());
                }

            }

            if (timeLast > waitGraph.getTime()) {
                waitGraph.addScore((rs.getLastWaitStats() * 10) + 2);
                timeLast = 0;
            }

            if (timeLast1 > speedGraph.getTime()) {
                if (activeVehicles > 0) {
                    speedGraph.addScore((avgSpeed / activeVehicles) - 3);
                } else {
                    speedGraph.addScore(0);
                }

                timeLast1 = 0;
            }
            for (Light l : lights) {
                g2d.setColor(Color.GREEN);
                tmp = l.positionOfDot();
                g2d.fillRect(tmp.getPosX(), tmp.getPosY(), 6, 6);
                tmp = l.positionOfId();
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(l.getId()), tmp.getPosX(), tmp.getPosY());
            }
            g2d.dispose();
        }
    }
}