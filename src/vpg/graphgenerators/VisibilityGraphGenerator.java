package vpg.graphgenerators;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.jung.graph.util.Pair;

import static java.awt.geom.PathIterator.*;
import static java.util.Objects.isNull;
import static vpg.ZombiesAndSurvivorsUtils.FloydWarshall;


public class VisibilityGraphGenerator {

    public static int[][] M; // make all-pairs distance matrix global

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
                    g.addEdge(g.getEdgeCount(), points.size() - 1, points.size());
                    points.add(p);
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

        final double epsilon = 0.0001; // precision for intersection testing

        // Now for each vertex
        //  test visibility of all vertices
        for (int i = 0; i < points.size(); i++) {

            for (int j = 0; j < points.size(); j++) {
                //(except boundary neighbours +/- 1 which are already added)
                if (i == j || j == Math.floorMod(i - 1, points.size()) || j == Math.floorMod(i + 1, points.size())) {
                    continue;
                }
                //System.out.println("Checking visibility " + i + ", " + j);
                // check to see if visibility line intersects any boundary edge
                Line2D visibilityLine = new Line2D.Double(points.get(i), points.get(j));

                boolean blocked = false;
                for (Line2D edge : boundary) {
                    if (edge.getP1().equals(points.get(j)) || edge.getP2().equals(points.get(j))
                    || edge.getP1().equals(points.get(i)) || edge.getP2().equals(points.get(i))) {
                        // System.out.println("Skip edge (" + edge.getP1() +"," + edge.getP2());
                        continue;
                    }

                    if (visibilityLine.intersectsLine(edge)) {
                        Point2D intersection = lineLineIntersection(visibilityLine, edge);

                        if ((intersection.distance(edge.getP1()) < epsilon) ||
                                (intersection.distance(edge.getP2()) < epsilon)) {
                            System.out.println("Visibility edge (" + visibilityLine.getP1() +"," + visibilityLine.getP2());
                            System.out.println("Intersection with edge (" + edge.getP1() +"," + edge.getP2());
                            System.out.println("Intersection at endpoint. Colinear?");
                            //continue; // intersection at a vertex
                        }
                        blocked = true;
                        break;

                    }
                }

                double mid_x = ((visibilityLine.getX1() + visibilityLine.getX2()) / 2.0);
                double mid_y = ((visibilityLine.getY1() + visibilityLine.getY2()) / 2.0);

                Point2D midpoint = new Point2D.Double(mid_x, mid_y);

                if (!blocked && polygon.contains(midpoint)) {
                    //System.out.println("Adding edge (" + i + ", "+ j+")");
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

    private static void findBFSVisibilityAugmentingEdge(Graph<Integer, Integer> g) {
        if (isNull(M))
            return;
        for (int i =0; i < g.getVertexCount(); i++ ) {
            System.out.println("Visibility increasing edges for BFS rooted at " + i);
            for (int e  : g.getEdges()) {
                Pair<Integer> endpoints = g.getEndpoints(e);
                if (Math.abs(M[i][endpoints.getFirst()] - M[i][endpoints.getSecond()]) == 1) {
                    int u = endpoints.getFirst();
                    int v = endpoints.getSecond();
                    if (M[i][u] > M[i][v]) {
                        u = endpoints.getSecond();
                        v = endpoints.getFirst();
                    }
                    Set<Integer> neighbors_u = new HashSet<Integer>(g.getNeighbors(u));
                    neighbors_u.add(u);
                    Set<Integer> neighbors_v = new HashSet<Integer>(g.getNeighbors(v));
                    neighbors_v.add(v);

                    if (neighbors_u.containsAll(neighbors_v)) {
                        System.out.println("Visibility increasing edge: [" + u + ", " + v + "]");
                    }

                }

            }
        }

    }

    private static ArrayList<Pair<Integer>> findDetailedBFSDismantling(Graph<Integer, Integer> g, int root, ArrayList<Pair<Integer>> dismantling) {
        if (isNull(M)) {
            System.err.println("Need distance matrix to find BFS dismantling");
            return null;
        }

        if (g.getVertexCount() == 1) {
            Collection<Integer> vertices = g.getVertices();
            int last = vertices.iterator().next();
            dismantling.add(new Pair<Integer>(last, last));
            System.out.println("Dismantling found: " + dismantling.toString());
            return dismantling;
        }

        for (int e  : g.getEdges()) {
            Pair<Integer> endpoints = g.getEndpoints(e);
            if (Math.abs(M[root][endpoints.getFirst()] - M[root][endpoints.getSecond()]) == 1) {
                int u = endpoints.getFirst();
                int v = endpoints.getSecond();
                if (M[root][u] > M[root][v]) {
                    u = endpoints.getSecond();
                    v = endpoints.getFirst();
                }
                Set<Integer> neighbors_u = new HashSet<Integer>(g.getNeighbors(u));
                neighbors_u.add(u);
                Set<Integer> neighbors_v = new HashSet<Integer>(g.getNeighbors(v));
                neighbors_v.add(v);

                if (neighbors_u.containsAll(neighbors_v)) {
//                    System.out.println("BFS Visibility increasing edge: [" + u + ", " + v + "]");
//                    System.out.println("Level of u: " + M[root][u]);
//                    System.out.println("Level of v: " + M[root][v]);

                    ArrayList<Pair<Integer>> dismantling_next = new ArrayList<>(dismantling);
                    Graph<Integer, Integer> graph_next = new UndirectedSparseGraph<>();
                    // copy the graph
                    for (Integer edge : g.getEdges()) {
                        graph_next.addEdge(edge, g.getIncidentVertices(edge));
                    }
                    // but remove dismantled vertex from copy
                    graph_next.removeVertex(v);
                    // and add to dismantling
                    dismantling_next.add(new Pair<>(v, u));
                    // recurse
                    findDetailedBFSDismantling(graph_next, root, dismantling_next);
                }

            }

        }
        return null;
    }

    private static ArrayList<Integer> findBFSDismantling(Graph<Integer, Integer> g, int root, ArrayList<Integer> dismantling) {
        if (isNull(M)) {
            System.err.println("Need distance matrix to find BFS dismantling");
            return null;
        }

        if (g.getVertexCount() == 1) {
            Collection<Integer> vertices = g.getVertices();
            int last = vertices.iterator().next();
            dismantling.add(last);
            System.out.println("Dismantling found: " + dismantling.toString());
            return dismantling;
        }

        for (int e  : g.getEdges()) {
            Pair<Integer> endpoints = g.getEndpoints(e);
            if (Math.abs(M[root][endpoints.getFirst()] - M[root][endpoints.getSecond()]) == 1) {
                int u = endpoints.getFirst();
                int v = endpoints.getSecond();
                if (M[root][u] > M[root][v]) {
                    u = endpoints.getSecond();
                    v = endpoints.getFirst();
                }
                Set<Integer> neighbors_u = new HashSet<Integer>(g.getNeighbors(u));
                neighbors_u.add(u);
                Set<Integer> neighbors_v = new HashSet<Integer>(g.getNeighbors(v));
                neighbors_v.add(v);

                if (neighbors_u.containsAll(neighbors_v)) {
//                    System.out.println("BFS Visibility increasing edge: [" + u + ", " + v + "]");
//                    System.out.println("Level of u: " + M[root][u]);
//                    System.out.println("Level of v: " + M[root][v]);

                    ArrayList<Integer> dismantling_next = new ArrayList<>(dismantling);
                    Graph<Integer, Integer> graph_next = new UndirectedSparseGraph<>();
                    // copy the graph
                    for (Integer edge : g.getEdges()) {
                        graph_next.addEdge(edge, g.getIncidentVertices(edge));
                    }
                    // but remove dismantled vertex from copy
                    graph_next.removeVertex(v);
                    // and add to dismantling
                    dismantling_next.add(v);
                    // recurse
                    findBFSDismantling(graph_next, root, dismantling_next);
                }

            }

        }
        return null;
    }

    public static void main(String[] args) {
        String contents;
        try {
            contents = new String(Files.readAllBytes(Paths.get("v10.txt")));

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Polygon p = readIpePath(contents);
        System.out.println("Polygon: ");
        System.out.println("Bounding Area: " + p.getBounds2D().getHeight() * p.getBounds2D().getWidth());
        System.out.println("Number of points: " + p.npoints);
        Graph<Integer, Integer> g = VisibilityGraph(p);

        //System.out.println("Resulting graph: ");
        //System.out.println(g.toString());
        //System.out.println("Neighbours of 0:" + g.getNeighbors( 0));
        //System.out.println("Neighbours of 2:" + g.getNeighbors(2));

        M = FloydWarshall(g);


        //print2DArray(M);

        //System.out.println(M[0][8] + ", " + M[0][6]);


        for (int root : g.getVertices()) {
            System.out.println(root + " : " + g.getNeighbors(root).toString());
        }

//
//        findBFSVisibilityAugmentingEdge(g);
//
//        ArrayList<Pair<Integer>> result = new ArrayList<>();
//
//
//        for (int root : g.getVertices()) {
//            System.out.println("Search for BFS dismantling from root " + root);
//             result = findDetailedBFSDismantling(g, root, new ArrayList<>());
//        }
//
//        if (isNull(result)) {
//            System.out.println("No BFS dismantling found");
//        }


//        ArrayList<Integer> result = new ArrayList<>();
//
//
//        for (int root : g.getVertices()) {
//            System.out.println("Search for BFS dismantling from root " + root);
//            result = findBFSDismantling(g, root, new ArrayList<>());
//        }
//
//        if (isNull(result)) {
//            System.out.println("No BFS dismantling found");
//        }
//


        findBFSDismantling(g, 14, new ArrayList<>());
        //System.out.println(g.getNeighbors(2).toString());
        //System.out.println(g.getNeighbors(3).toString());

    }
}
