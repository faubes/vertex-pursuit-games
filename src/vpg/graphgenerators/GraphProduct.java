package vpg.graphgenerators;

import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import vpg.graphgenerators.SampleGraphGenerator;

public class GraphProduct<V,E> {

	public static Graph<Pair<Integer>,Integer> StrongProduct(Graph<Integer, Integer> g, Graph<Integer, Integer> h) {
		Graph<Pair<Integer>,Integer> gh = new SparseMultigraph<Pair<Integer>, Integer>();
		
		for (Integer u : g.getVertices()) {
			for (Integer v : h.getVertices()) {
				gh.addVertex(new Pair<Integer>(u, v));
			}
		}

		HashSet<Pair<Integer>> Q = new HashSet<Pair<Integer>>(gh.getVertices());
		Integer n = 0;
		for (Pair<Integer> u : gh.getVertices()) {
			Q.remove(u);
			for (Pair<Integer> v: Q) {
				if ((u.getFirst().equals(v.getFirst()) || (g.findEdge(u.getFirst(), v.getFirst()) != null))
					&& ((u.getSecond().equals(v.getSecond()) || (h.findEdge(u.getSecond(), v.getSecond()) != null)))) {
					gh.addEdge(++n, u, v);
				}
				
			}
		}
		
		return gh;
	}


public static void main(String[] argz) {
	Graph<Integer, Integer> g = SampleGraphGenerator.generateOuterPlanarGraph5();
	Graph<Integer, Integer> h = SampleGraphGenerator.generateOuterPlanarGraph5();
	
	Graph<Pair<Integer>, Integer> gh = (Graph<Pair<Integer>, Integer>)StrongProduct(g, h);
	System.out.println(gh);
	}
}