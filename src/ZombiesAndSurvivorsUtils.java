package cnr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class ZombiesAndSurvivorsUtils {
	
	public static void print2DArray(int[][] array) {
		for (int i=0; i < array.length; i++) {
			System.out.println(Arrays.toString(array[i]));
		}
	}
	
	public static int[][] FloydWarshall(Graph<Integer, Integer> g) {
		
		// need order of graph to size array
		int n = g.getVertexCount();
		
		// building n*n matrix A = a_ij = distance from i to j
		int[][] d = new int[n][n];
		
		// distance from x to x is zero
		// presume x not connected to y if x != y
		for (int i = 0; i < n * n; i++) {
			if (i % n == i /n) {
				d[i % n][ i / n] = 0;
			}
			else {
				d[i % n][i / n ] = Integer.MAX_VALUE;	
			}
		}
		
		// for every edge e = uv, the distance from u to v = a_uv = a_vu =  1
		for (Integer e : g.getEdges()) {
			Pair<Integer> endpoints = g.getEndpoints(e);
			d[endpoints.getFirst()][endpoints.getSecond()] = 1;
			d[endpoints.getSecond()][endpoints.getFirst()] = 1;
		}
		
		// Floyd Warshall
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (d[i][k] != Integer.MAX_VALUE 
							&& d[k][j] != Integer.MAX_VALUE 
							&& d[i][j] > d[i][k] + d[k][j]) {
						d[i][j] = d[i][k] + d[k][j];
					}
				}
			}
		}
		
		return d;
	}
	
	
	public static Set<Integer> getNextZombieMove(int[][] d, Integer z, Integer s) {
		
		int n = d.length;
		
		HashSet<Integer> nextMoves = new HashSet<>();
		
		int shortestLength = Integer.MAX_VALUE;
		
		// go down the zombie's row of Floyd Warshall to check its neighbours
		for (int i = 0; i < n; i++) {
			// if i is a neighbour of z
			if ((d[z][i] == 1) 
					// and the distance from i to s is the shortest seen yet
					&& (d[i][s] < shortestLength)) {
					// then the shortest distance from z to is is z,i, ..., s
					shortestLength = d[i][s];
				}
			}
		System.out.println(shortestLength);
		// now that we know the shortest distance, include those neighbours of z which have a shortest path
		for (int i = 0; i < n; i++) {
			if (d[z][i] == 1 && d[i][s] == shortestLength) {
				nextMoves.add(i);
			}
		}

		return nextMoves;
	}
	
	
	public static void main(String[] args) {
	
		Graph<Integer, Integer> g = GraphGenerator.generateExampleGraph();
		
		int[][] d = FloydWarshall(g);
		
		print2DArray(d);
	}
}
