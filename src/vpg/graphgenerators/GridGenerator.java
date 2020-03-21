package vpg.graphgenerators;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class GridGenerator {


        // Build grid graph
    // m rows, n cols
        /*
        5 * 5
        10 ..  24
        5 6 7 8 9
        0 1 2 3 4
        */

    public static Graph<Integer, Integer> generateGridGraph(int m, int n) {
        Graph<Integer, Integer> g = new SparseMultigraph<Integer, Integer>();

        // generate vertices
        for (int i = 0; i < m*n; i++) {
            g.addVertex(i);
        }

        int edgeCount = 0;

        // iterate over each vertex now to connect to neighbours

        for (int i = 0; i < n*m; i++) {

                // if on second row or higher, connect to bottom row
               if (i > n) {
                   g.addEdge(edgeCount, i, i-n);
                   ++edgeCount;
               }
               // if not on last row and below, connect to top row
                if (i < n*(m - 1)) {
                   g.addEdge(edgeCount, i, i + n);
                    ++edgeCount;
                }
                // if not on left border, connect to left neighbour
                if (i % n != 0) {
                   g.addEdge(edgeCount, i, i-1);
                ++edgeCount;
            }
                // if not on right border connect to right neighbour
            if (i % n != n-1) {
                g.addEdge(edgeCount, i, i+1);
                ++edgeCount;
            }

        }
        return g;
    }

}
