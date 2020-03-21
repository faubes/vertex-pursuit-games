package vpg.graphgenerators;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.geom.PathIterator.*;

public class VisibilityGraphGenerator {

    public static Polygon readIpePath(String pathStr) {
        Polygon p = new Polygon();

        String regex = "([0-9]+) ([0-9]+) [ml]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pathStr);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            p.addPoint(x, y);
            result.append("[");
            result.append(x);
            result.append(":");
            result.append(y);
            result.append("]");
        }

        System.out.println(result.toString());
        return p;
    }


    public static Graph<Integer, Integer> VisibilityGraph(Polygon polygon) {
        // is sparse the right choice here? Not really...
        Graph<Integer, Integer> g = new UndirectedSparseGraph<>();


        // Use Polygon's pathIterator to create a list of Line2D to test edge intersections
        // simultaneously start to construct G by adding vertices and boundary edges
        PathIterator pathIterator = polygon.getPathIterator(null);
        ArrayList<Line2D> boundary = new ArrayList<Line2D>();
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        Point2D p;
        while (!pathIterator.isDone()) {
            float[] coords = new float[6];
            int seg_type = pathIterator.currentSegment(coords);
            switch (seg_type) {
                case SEG_MOVETO:
                    g.addVertex(points.size());
                    p = new Point2D.Double(coords[0], coords[1]);
                    points.add(p);
                    break;
                case SEG_LINETO:
                    p = new Point2D.Double(coords[0], coords[1]);
                    Line2D edge = new Line2D.Double(points.get(points.size() - 1), p);
                    boundary.add(edge);
                    g.addVertex(points.size());
                    points.add(p);
                    g.addEdge(g.getEdgeCount(), points.size() - 1, points.size());
                    break;
                case SEG_CLOSE:
                    edge = new Line2D.Double(points.get(points.size() - 1), points.get(0));
                    boundary.add(edge);
                    g.addEdge(g.getEdgeCount(), points.size() - 1, 0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + seg_type);
            }
            //System.out.println(count + " " + coords[0] + ", " + coords[1] + " type: " + seg_type);
            pathIterator.next();
        }

        double epsilon = 0.0001; // precision for intersection testing

        // Now for each vertex
        for (int i = 0; i < points.size(); i++) {
            //  test visibility of all vertices (except boundary neighbours +/- 1 which are already added)
            for (int j = 0; j < points.size(); j++) {
                if (i == j || j == Math.floorMod(i - 1, points.size()) || j == Math.floorMod(i + 1, points.size())) {
                    continue;
                }
                // System.out.print(i + ", " + j + " ");
                // check to see if visibility line intersects any boundary edge
                Line2D visibilityLine = new Line2D.Double(points.get(i), points.get(j));



                boolean blocked = false;
                for (Line2D edge : boundary) {
                    if ((edge.getP1().equals(points.get(i))) || (edge.getP2().equals(points.get(i)))) {
                        continue;
                    }

                    if (visibilityLine.intersectsLine(edge)) {
                        Point2D intersection = lineLineIntersection(visibilityLine, edge);

                        if ((intersection.distance(edge.getP1()) < epsilon) ||
                                (intersection.distance(edge.getP2()) < epsilon))
                            continue; // intersection v
                    }
                    else {
                        continue;
                    }
                    blocked = true;
                    break;
                }
                if (!blocked) {
                    g.addEdge(g.getEdgeCount(), i, j);
                }
            }
        }
        return g;

    }

    // copied from https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
    // could be nicer
    private static Point2D lineLineIntersection(Line2D l1, Line2D l2) {
        Point2D A = l1.getP1();
        Point2D B = l1.getP2();
        Point2D C = l2.getP1();
        Point2D D = l2.getP2();

        // Line AB represented as a1x + b1y = c1
        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1 * (A.getX()) + b1 * (A.getY());

        // Line CD represented as a2x + b2y = c2
        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2 * (C.getX()) + b2 * (C.getY());

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point2D.Double(x, y);
        }
    }

    public static void main(String[] args) {
        String contents;
        try {
            contents = new String(Files.readAllBytes(Paths.get("polygon.txt")));

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Polygon p = readIpePath(contents);
        System.out.println("Polygon: ");
        System.out.println("Bounding Area: " + p.getBounds2D().getHeight() * p.getBounds2D().getWidth());
        System.out.println("Number of points: " + p.npoints);
        Graph<Integer, Integer> g = VisibilityGraph(p);

        System.out.println("Resulting graph: ");
        System.out.println(g.toString());
        System.out.println("Neighbours of 0:" + g.getNeighbors( 0));
        System.out.println("Neighbours of 2:" + g.getNeighbors(2));
    }
}
