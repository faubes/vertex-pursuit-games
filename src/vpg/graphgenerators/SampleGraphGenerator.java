package vpg.graphgenerators;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class SampleGraphGenerator {


	public static Graph<String, Integer> generateSimpleGraph() {
		// Create basic graph
		Graph<String,Integer> g = new SparseMultigraph<String, Integer>();

		for (int i = 0; i < 10; i++) {
			g.addVertex("V"+i);
		}

		for (int j = 0; j < 10; j++) {
			g.addEdge(j, "V"+j, "V"+((j + 1) % 10));
		}

		g.addEdge(g.getEdgeCount() + 1, "V0", "V2");
		g.addEdge(g.getEdgeCount() + 1, "V0", "V3");
		g.addEdge(g.getEdgeCount() + 1, "V4", "V7");
		return g;
	}
	
	public static Graph<Integer, Integer> generateOuterPlanarGraph5() {
		
		Graph<Integer, Integer> g = new SparseMultigraph<Integer, Integer>();
		
		for (int i = 0; i < 30; i++) {
			g.addVertex(i);
		}
		
		g.addEdge(g.getEdgeCount() + 1, 0, 1);
		g.addEdge(g.getEdgeCount() + 1, 1, 2);
		g.addEdge(g.getEdgeCount() + 1, 2, 3);
		g.addEdge(g.getEdgeCount() + 1, 3, 4);
		g.addEdge(g.getEdgeCount() + 1, 4, 0);
		
		for (int j = 0; j < 4; j++) {
			g.addEdge(g.getEdgeCount() + 1, j+5,  (j+6));
			g.addEdge(g.getEdgeCount() + 1, (j+10), (j+11));
			g.addEdge(g.getEdgeCount() + 1, (j+15), (j+16));
			g.addEdge(g.getEdgeCount() + 1, (j+20), (j+21));
			g.addEdge(g.getEdgeCount() + 1, (j+25), (j+26));
		}
		
		g.addEdge(g.getEdgeCount() + 1, 0, 5);
		g.addEdge(g.getEdgeCount() + 1, 1, 9);
		g.addEdge(g.getEdgeCount() + 1, 1, 10);
		g.addEdge(g.getEdgeCount() + 1, 2, 14);
		g.addEdge(g.getEdgeCount() + 1, 2, 15);
		g.addEdge(g.getEdgeCount() + 1, 3, 19);
		g.addEdge(g.getEdgeCount() + 1, 3, 20);
		g.addEdge(g.getEdgeCount() + 1, 4, 24);
		g.addEdge(g.getEdgeCount() + 1, 4, 25);
		g.addEdge(g.getEdgeCount() + 1, 0, 29);
		
		g.addEdge(g.getEdgeCount() +1,  6, 28);
		g.addEdge(g.getEdgeCount() +1,  8, 11);
		g.addEdge(g.getEdgeCount() +1,  13, 16);
		g.addEdge(g.getEdgeCount() +1,  18, 21);
		g.addEdge(g.getEdgeCount() +1,  23, 26);
		
		
		return g;
	}

	public static Graph<Integer, Integer> generateOuterPlanarGraph4() {
		
		Graph<Integer, Integer> g = new SparseMultigraph<Integer, Integer>();
		
		for (int i = 0; i < 24; i++) {
			g.addVertex(i);
		}
		
		g.addEdge(g.getEdgeCount() + 1, 0, 1);
		g.addEdge(g.getEdgeCount() + 1, 1, 2);
		g.addEdge(g.getEdgeCount() + 1, 2, 3);
		g.addEdge(g.getEdgeCount() + 1, 3, 0);
		
		for (int j = 4; j < 8; j++) {
			g.addEdge(g.getEdgeCount() + 1, j,  (j+1));
			g.addEdge(g.getEdgeCount() + 1, (j+5), (j+6));
			g.addEdge(g.getEdgeCount() + 1, (j+10), (j+11));
			g.addEdge(g.getEdgeCount() + 1, (j+15), (j+16));
		}
		
		g.addEdge(g.getEdgeCount() + 1, 0, 4);
		g.addEdge(g.getEdgeCount() + 1, 1, 8);
		g.addEdge(g.getEdgeCount() + 1, 1, 9);
		g.addEdge(g.getEdgeCount() + 1, 2, 13);
		g.addEdge(g.getEdgeCount() + 1, 2, 14);
		g.addEdge(g.getEdgeCount() + 1, 3, 18);
		g.addEdge(g.getEdgeCount() + 1, 3, 19);
		g.addEdge(g.getEdgeCount() + 1, 0, 23);
		
		return g;
	}
	
public static Graph<Integer, Integer> generateExampleGraph() {
		
		Graph<Integer, Integer> g = new SparseMultigraph<Integer, Integer>();
		
		for (int i = 0; i < 6; i++) {
			g.addVertex(i);
		}
		
		g.addEdge(g.getEdgeCount() + 1, 0, 1);
		g.addEdge(g.getEdgeCount() + 1, 1, 2);
		g.addEdge(g.getEdgeCount() + 1, 1, 3);
		g.addEdge(g.getEdgeCount() + 1, 1, 4);
		g.addEdge(g.getEdgeCount() + 1, 3, 4);
		g.addEdge(g.getEdgeCount() + 1, 4, 5);
		
		return g;
	}
	
	
}
