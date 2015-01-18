package application;

import exceptions.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.*;
import kernel.*;
import utils.*;

/**
 * class for loading, reading and validating input files
 *
 * @author munchmar
 */
public class Reader {

    private File vehicles = null, buildings = null, roads = null,
            crosses = null, turns = null;
    private ArrayList<Building> buildingList = new ArrayList<>(4);
    private VehicleList vehiclesList = VehicleList.getInstance();
    private ArrayList<Road> roadList = new ArrayList<>(7);
    private ArrayList<CrossRoad> crossingList = new ArrayList<>(7);
    private ArrayList<Turn> turnList = new ArrayList<>(7);
    private int width, height;
    private boolean vehicleOk, buildingOk, roadOk, crossOk, turnOk;
    private JFrame frame;

    public Reader() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("Selecting file");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new Selector();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setLayout(new GridLayout(0, 2));
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private int[] readIntFromString(String[] args) {
        int[] tmp = new int[args.length];
        boolean cleanLine = true;
        for (int i = 0; i < args.length; i++) {
            try {
                tmp[i] = Integer.parseInt(args[i]);

            } catch (NumberFormatException x) {
                cleanLine = false;
                ExceptionShower.showExceptions(x, frame);
            }
        }
        if (cleanLine) {
            return tmp;

        } else {
            return null;
        }

    }

    // reading and validating vehicle file
    // if ok, saves vehicles into vehicleList
    private void readVehicles() {
        try {
            FileInputStream fstream = new FileInputStream(vehicles.getAbsolutePath());
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                Dimensions d = Constants.getVEHICLE_DIMENSIONS();
                Vehicle v;
                vehiclesList.clear();
                while ((strLine = br.readLine()) != null) {
                    if (!strLine.equals("")) {
                        int[] args = readIntFromString(strLine.split(" "));
                        if (args == null) {
                        } else if (args.length < 5) {
                            throw new WrongNumberOfArguments("Vehicles must be defined with at least 5 arguments");
                        } else if (args[0] <= 0 || args[0] >= 20) {
                            throw new NotInRangeException(args[0] + " max. speed isn't properly set.");
                        } else if (args[1] <= 2 || args[1] >= 10) {
                            throw new NotInRangeException(args[1] + " min. distance isn't properly set.");
                        } else if (args[1] <= 0 || args[1] >= 10) {
                            throw new NotInRangeException(args[2] + " acceleration isn't properly set.");
                        } else {
                            v = new Vehicle(d.copy(), 75, args[0],
                                    args[1], args[2], args[3]);
                            vehiclesList.add(v);
                            if (args[4] != -1) {
                                int[] tmp = new int[(args.length - 4)];
                                boolean correctRoute = true;
                                for (int i = 0; i < tmp.length; i++) {
                                    if (args[i + 4] >= 0 && args[i + 4] < 4) {
                                        tmp[i] = args[i + 4];
                                    } else {
                                        correctRoute = false;
                                        throw new NotInRangeException(args[i + 4] + " directions are not properly set.");
                                    }
                                }

                                if (correctRoute) {
                                    v.setRoute(tmp);
                                }
                            }
                        }
                    } else {
                        throw new EmptyLineException("Empty line in the vehicle file");
                    }
                }
                vehicleOk = true;
            }
        } catch (IOException | WrongNumberOfArguments | NotInRangeException | EmptyLineException e) {
            ExceptionShower.showExceptions(e, frame);
            vehicleOk = false;
        }
    }

    // reading and validating buildings file
    // if ok, saves buildings into buildingList
    private void readBuildings() {
        try {
            FileInputStream fstream = new FileInputStream(buildings.getAbsolutePath());
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                int line = 0;
                buildingList.clear();
                while ((strLine = br.readLine()) != null) {
                    if (!strLine.equals("")) {
                        int[] args = readIntFromString(strLine.split(" "));
                        if (line == 0) {
                            line++;
                            if (args == null) {
                            } else if (args.length != 2) {
                                throw new WrongNumberOfArguments("Size of the city must be defined with exactly 2 parameters");
                            } else if (args[0] <= 0) {
                                throw new NotInRangeException(args[0] + " width of the city is wrongly set.");
                            } else if (args[1] <= 0) {
                                throw new NotInRangeException(args[1] + " height of the city is wrongly set.");
                            } else {
                                width = args[0];
                                height = args[1];
                            }
                        } else {
                            if (args == null) {
                            } else if (args.length != 4) {
                                throw new WrongNumberOfArguments("Building must be defined with exactly 4 parameters.");
                            } else if (args[0] < 0 && args[0] >= width) {
                                throw new NotInRangeException(args[0] + " X-coordinate of building isn't properly set.");
                            } else if (args[1] < 0 && args[1] >= height) {
                                throw new NotInRangeException(args[1] + " Y-coordinate of building isn't properly set.");
                            } else if (args[2] < 0 && (args[0] + args[2]) >= width) {
                                throw new NotInRangeException(args[2] + " width of the building is wrongly set. (building is exceeding city size)");
                            } else if (args[3] < 0 && (args[1] + args[3]) >= height) {
                                throw new NotInRangeException(args[3] + " height of the building is wrongly set. (building is exceeding city size)");
                            } else {
                                buildingList.add(new Building(
                                        new Position(args[0], args[1]),
                                        args[3], args[2]));
                            }
                        }

                    } else {
                        throw new EmptyLineException("Empty line in the buildings file.");
                    }

                }
                buildingOk = true;
            }
        } catch (IOException | WrongNumberOfArguments | NotInRangeException | EmptyLineException e) {
            ExceptionShower.showExceptions(e, frame);
            buildingOk = false;
        }
    }

    // reading and validating roads file
    // if ok, saves roads into roadList
    private void readRoads() {
        roadList.clear();
        try {
            FileInputStream fstream = new FileInputStream(roads.getAbsolutePath());
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                int first, second;
                Cross start, end;
                boolean northSouth;
                Road r;
                while ((strLine = br.readLine()) != null) {
                    if (!strLine.equals("")) {
                        int[] args = readIntFromString(strLine.split(" "));
                        if (args == null) {
                        } else if (args.length != 3) {
                            throw new WrongNumberOfArguments("Road must be defined with exactly 3 parameters");
                        } else if (args[0] > crossingList.size() || ((-1) * args[0] - 1) > turnList.size()) {
                            throw new NotInRangeException(args[0] + " index of first crossroad/turn is wrongly set.");
                        } else if (args[1] > crossingList.size() || ((-1) * args[1] - 1) > turnList.size()) {
                            throw new NotInRangeException(args[1] + " index of second crossroad/turn is wrongly set.");
                        } else if (args[2] != 0 && args[2] != 1) {
                            throw new NotInRangeException(args[2] + " direction of road is wrongly set.");

                        } else {
                            first = args[0];
                            second = args[1];
                            northSouth = (args[2] == 1);
                            if (first >= 0) {
                                start = crossingList.get(first);

                            } else {
                                start = turnList.get((first * (-1)) - 1);
                            }

                            if (second >= 0) {
                                end = crossingList.get(second);
                            } else {
                                end = turnList.get((second * (-1)) - 1);
                            }
                            r = new Road(start, end, northSouth);
                            if (northSouth) {
                                start.setRoad(Direction.NORTH, r);
                                end.setRoad(Direction.SOUTH, r);
                            } else {
                                start.setRoad(Direction.EAST, r);
                                end.setRoad(Direction.WEST, r);
                            }

                            roadList.add(r);
                        }
                    } else {
                        throw new EmptyLineException("Empty line in the file with roads.");
                    }
                }
                roadOk = true;
            }
        } catch (IOException | WrongNumberOfArguments | NotInRangeException | EmptyLineException e) {
            ExceptionShower.showExceptions(e, frame);
            roadOk = false;
        }
    }

    // reads and checks the crossings
    // if ok, saves it into crossingList
    private void readCrosses() {
        try {
            FileInputStream fstream = new FileInputStream(crosses.getAbsolutePath());
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                crossingList.clear();
                while ((strLine = br.readLine()) != null) {
                    if (!strLine.equals("")) {
                        int[] args = readIntFromString(strLine.split(" "));
                        if (args == null) {
                        } else if (args.length != 4) {
                            throw new WrongNumberOfArguments("Crossroads must be defined with exactly 4 parameters.");
                        } else if (args[0] < 0 && (args[0] + Constants.getSTREET_WIDTH()) >= width) {
                            throw new NotInRangeException(args[0] + " X-coordinate of crossroad isn't properly set.");
                        } else if (args[1] < 0 && (args[1] + Constants.getSTREET_WIDTH()) >= height) {
                            throw new NotInRangeException(args[1] + " Y-coordinate of crossroad isn't properly set.");
                        } else if (args[2] < 0 && args[2] > 4) {
                            throw new NotInRangeException(args[1] + " type of crossing is wrongly set.");
                        } else if (args[3] <= 0) {
                            throw new NotInRangeException(args[1] + " initial delay is wrongly set.");
                        } else {
                            crossingList.add(new CrossRoad(new Position(args[0], args[1]),
                                    args[2], args[3]));
                        }
                    } else {
                        throw new EmptyLineException("Empty line in the CrossRoad file.");
                    }
                }
                crossOk = true;
            }
        } catch (IOException | WrongNumberOfArguments | NotInRangeException | EmptyLineException e) {
            ExceptionShower.showExceptions(e, frame);
            crossOk = false;
        }

    }

    // reads and checks the turns
    // if ok, saves it into turnList
    private void readTurns() {
        try {
            FileInputStream fstream = new FileInputStream(turns.getAbsolutePath());
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                turnList.clear();
                while ((strLine = br.readLine()) != null) {
                    if (!strLine.equals("")) {
                        int[] args = readIntFromString(strLine.split(" "));

                        if (args == null) {
                        } else if (args.length != 2) {
                            throw new WrongNumberOfArguments("Turns must be defined by exactly 2 parameters.");
                        } else if (args[0] < 0 || (args[0] + Constants.getSTREET_WIDTH()) > width) {
                            throw new NotInRangeException(args[0] + " X-coordinate of turn isn't properly set.");
                        } else if (args[1] < 0 || (args[1] + Constants.getSTREET_WIDTH()) > height) {
                            throw new NotInRangeException(args[1] + " Y-coordinate of turn isn't properly set");
                        } else {
                            turnList.add(new Turn(new Position(args[0], args[1])));
                        }

                    } else {
                        throw new EmptyLineException("Empty line in the turns file.");
                    }
                }
                turnOk = true;
            }
        } catch (IOException | WrongNumberOfArguments | NotInRangeException | EmptyLineException e) {
            ExceptionShower.showExceptions(e, frame);
            turnOk = false;
        }
    }

    private void prerequisityEmpty(String s) {
        JOptionPane.showMessageDialog(frame, "Missing loading of the file above", "Missing file above for reading this file correctly " + s, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Loading dialog
     */
    public class Selector extends JPanel implements ActionListener {

        private JButton vehicleButton, buildingButton, roadButton, crossButton, turnButton, simulate;
        private JLabel vehicleLabel, buildingLabel, roadLabel, crossLabel, turnLabel;
        private JFileChooser fc;

        public Selector() {

            vehicleLabel = new JLabel("Vehicles");
            buildingLabel = new JLabel("Buildings");
            roadLabel = new JLabel("Roads");
            crossLabel = new JLabel("CrossRoads");
            turnLabel = new JLabel("Turns");


            buildingButton = new JButton("Buildings");
            buildingButton.addActionListener(this);
            crossButton = new JButton("Crossroads");
            crossButton.addActionListener(this);
            turnButton = new JButton("Turns");
            turnButton.addActionListener(this);
            roadButton = new JButton("Roads");
            roadButton.addActionListener(this);
            vehicleButton = new JButton("Vehicles");
            vehicleButton.addActionListener(this);

            simulate = new JButton("Simulate");
            simulate.setEnabled(false);
            simulate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.simulate(width, height, buildingList, roadList, crossingList, turnList);
                }
            });

            fc = new JFileChooser(".");


            add(buildingLabel);
            add(buildingButton);
            add(crossLabel);
            add(crossButton);
            add(turnLabel);
            add(turnButton);
            add(roadLabel);
            add(roadButton);
            add(vehicleLabel);
            add(vehicleButton);
            add(simulate);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == vehicleButton) {
                if (roadOk) {
                    int returnVal = fc.showOpenDialog(Selector.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        vehicles = fc.getSelectedFile();
                        vehicleLabel.setText(vehicles.getName());
                        readVehicles();
                    }
                } else {
                    prerequisityEmpty("vehicles");
                }
                setSimulateButton();

            } else if (e.getSource() == buildingButton) {
                int returnVal = fc.showOpenDialog(Selector.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    buildings = fc.getSelectedFile();
                    buildingLabel.setText(buildings.getName());
                    readBuildings();
                }

            } else if (e.getSource() == roadButton) {
                if (turnOk && crossOk) {
                    int returnVal = fc.showOpenDialog(Selector.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        roads = null;
                        roads = fc.getSelectedFile();
                        roadLabel.setText(roads.getName());
                        readRoads();
                    }
                } else {
                    prerequisityEmpty("roads");
                }

            } else if (e.getSource() == crossButton) {
                if (buildingOk) {
                    int returnVal = fc.showOpenDialog(Selector.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        crosses = fc.getSelectedFile();
                        crossLabel.setText(crosses.getName());
                        readCrosses();
                    }
                } else {
                    prerequisityEmpty("crossroads");
                }

            } else if (e.getSource() == turnButton) {
                if (buildingOk) {
                    int returnVal = fc.showOpenDialog(Selector.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        turns = fc.getSelectedFile();
                        turnLabel.setText(turns.getName());
                        readTurns();
                    }
                } else {
                    prerequisityEmpty("turns");
                }
            }
        }

        private boolean isAllSelected() {
            return (vehicleOk && buildingOk && roadOk && crossOk && turnOk);
        }

        private void setSimulateButton() {
            simulate.setEnabled(isAllSelected());
        }
    }
}
