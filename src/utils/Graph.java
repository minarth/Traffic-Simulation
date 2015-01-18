package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class for helping with the rendering the graphs
 * taken from SO
 *
 * @author hovercraft-full-of-eels
 * from http://stackoverflow.com/questions/8693342/drawing-a-simple-line-graph-in-java
 */
public class Graph extends JPanel {

    private int MAX_SCORE = 20;
    private final int PREF_W = 300;
    private final int PREF_H = 200;
    private final int BORDER_GAP = 40;
    private final Color GRAPH_COLOR = Color.black;
    private final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    private final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private final int GRAPH_POINT_WIDTH = 4;
    private final int Y_HATCH_CNT = 10;
    private List<Double> scores;
    private float progress = 0f;
    private float direction = 0.1f;
    private final int time = 2000;
    private double scale;
    private String label;

    /**
     * @return interval of re-rendering
     */
    public int getTime() {
        return time;
    }

    /**
     * Method for adding another point to the graph
     * this assumes keeping same interval on X-axis
     * @param x value of the next point
     */
    public void addScore(double x) {
        if (scores.size() > 9) {
            scores.remove(0);
        }
        scores.add(x);
    }

    public Graph(double scale, int max, String label) {
        scores = new ArrayList<>(2);
        this.label = label;
        this.scale = scale;
        MAX_SCORE = max;
        Timer timer = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += direction;
                if (progress > 1f) {
                    direction *= -1;
                    progress = 1f;
                } else if (progress < 0) {
                    direction *= -1;
                    progress = 0f;
                }
                repaint();
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        removeAll();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
        double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

        // render label
        g2.drawString(this.label, 10, 10);

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            int x1 = (int) (i * xScale + BORDER_GAP);
            int y1 = (int) ((MAX_SCORE - scores.get(i)) * yScale + BORDER_GAP);
            graphPoints.add(new Point(x1, y1));
        }

        // create x and y axes 
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        // create hatch marks for y axis. 
        for (int i = 0; i < Y_HATCH_CNT; i++) {
            int x0 = BORDER_GAP;
            int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString(String.valueOf((i+1)*scale), x0 - 30, y0 + 5);
        }

        // and for x axis
        for (int i = 0; i < scores.size() - 1; i++) {
            int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = y0 - GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(GRAPH_COLOR);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
}
