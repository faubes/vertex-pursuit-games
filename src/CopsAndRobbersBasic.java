package cnr;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.google.common.base.Supplier;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.algorithms.generators.*;
import edu.uci.ics.jung.algorithms.generators.random.*;
import edu.uci.ics.jung.graph.util.*;

public class CopsAndRobbersBasic {


//	public static boolean checkForWin(Integer[] cops, Integer robber) {
//		boolean win = false;
//		for (int element : cops) {
//			if (element == robber) {
//				win = true;
//			}
//		}
//		return win;
//	}

//	public CopsAndRobbersBasic() {
//		copNumber = 1;
//		copPositions = new HashSet<String>();
//	}
	


	// public static Graph<String, Integer> generateFunkyGraph() {
	// // Create basic graph
	// Forest<String,Integer> graph;
	//
	// Supplier<UndirectedGraph<String, Integer>> graphFactory =
	// new Supplier<UndirectedGraph<String,Integer>>() {
	//
	// public UndirectedGraph<String, Integer> get() {
	// return new UndirectedSparseGraph<String,Integer>();
	// }
	// };
	//
	// Supplier<Integer> edgeFactory = new Supplier<Integer>() {
	// int i=0;
	// public Integer get() {
	// return i++;
	// }
	// };
	//
	// Supplier<String> vertexFactory = new Supplier<String>() {
	// int i=0;
	// public String get() {
	// return "V"+i++;
	// }
	// };
	//
	// Set<String> seedVertices = new HashSet<String>();
	//
	// BarabasiAlbertGenerator<String, Integer> generator =
	// new BarabasiAlbertGenerator<String, Integer>(graphFactory, vertexFactory,
	// edgeFactory, 10, 4, (Set)seedVertices);
	// generator.evolveGraph(50);
	// return generator.get();
	// }

	public static void main(String[] args) {

		final int MAXROUNDS = 15;
		
		Graph<Integer, Integer> g;
		
		int copNumber = 0;
		
		Integer[] copPositions;
		
		Scanner scanner = new Scanner(System.in);

		g = GraphGenerator.generateOuterPlanarGraph4();
		
		boolean won = false;
		
		// Graph<String, Integer> g2 = generateFunkyGraph();

		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges

		String input;

		System.out.println("Graph: ");
		System.out.println(g);

		boolean valid = false;

		while (!valid) {
			try {
				System.out.print("Enter the cop number: ");
				input = scanner.nextLine();
				int tmp = Integer.parseInt(input);
				if (tmp <= 0) {
					System.out.println("You need at least one cop to play.");
				} else if (tmp >= g.getVertexCount()) {
					System.out.println("That's probably too many cops.");
				} else {
					copNumber = tmp;
					valid = true;
				}
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Cop number must be a number");
			}
		}
		
		copPositions = new Integer[copNumber]; // vertices are strings of form "V"+k

		System.out.println("Cops start by selecting " + copNumber + " vertices.");

		for (int i = 0; i < copNumber; i++) {
			valid = false;
			while (!valid) {
				try {
					System.out.print("Enter start vertex for cop " + i + ": ");
					input = scanner.nextLine();
					int pos = Integer.parseInt(input);
					copPositions[i] = pos;
					valid = true;

				} catch (java.lang.NumberFormatException e) {
					System.out.println("Vertex must be a number.");
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Invalid vertex.");
				}
			}
		}

		System.out.println("Cops are occupying vertices " + Arrays.toString(copPositions));

		Integer robberPosition = 0;

		System.out.println("Robber selects start vertex.");
		valid = false;
		while (!valid) {
			try {
				HashSet<Integer> robberTerritory = new HashSet<Integer>();
				robberTerritory.addAll(g.getVertices());
				for (Integer i : copPositions) {
					if (robberTerritory.contains(i)) {
						robberTerritory.remove(i);
					}
				}
				System.out.println("Safe choices are "+ robberTerritory.toString());
				
				System.out.print("Enter start vertex for robber: ");
				
				input = scanner.nextLine();

				int pos = Integer.parseInt(input);
				robberPosition = pos;
				valid = true;

			} catch (java.lang.NumberFormatException e) {
				System.out.println("Vertex must be a number.");
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Invalid vertex.");
			}
		}

		int round = 0;
		
		while (!won && round < MAXROUNDS) {
			round++;
			System.out.println();;
			System.out.println("Round " + round);
			for (int i = 0; i < copNumber; i++) {
				valid = false;
				while (!valid & !won) {
					try {
						int currentPos = copPositions[i];
						HashSet<Integer> neighbours = (HashSet<Integer>) g.getNeighbors(currentPos);
						System.out.println();
						System.out.println("Cop " + i + " occupies vertex " + copPositions[i]);
						System.out.println("Possible moves are : " + neighbours.toString());
						System.out.print("Enter move for cop " + i + ": ");
						input = scanner.nextLine();
						int newPosition = Integer.parseInt(input);
						if (neighbours.contains("V"+newPosition)) {
							copPositions[i] = newPosition;
							valid = true;
							if (newPosition == robberPosition) {
								System.out.println("Cop " + i + " catches the robber!");
								won = true;
							}
						} else {
							System.out.println("Not a valid move. Must choose adjacent vertex");
						}

					} catch (java.lang.NumberFormatException e) {
						System.out.println("Vertex must be a number");
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Invalid vertex");
					}
				}
			}

			valid = false;
			while (!won && !valid) {
				try {
					HashSet<Integer> neighbours = (HashSet<Integer>) g.getNeighbors(robberPosition);
					HashSet<Integer> cops = new HashSet<Integer>(Arrays.asList(copPositions));
					neighbours.removeAll(cops);
					if (neighbours.isEmpty()) {
						System.out.println("The robber is surrounded!");
						valid = true;
						won = true;
					} else {

						System.out.println("Robber occupies vertex " + robberPosition);
						System.out.println("Possible moves are : " + neighbours.toString());
						System.out.print("Enter move robber: ");
						input = scanner.nextLine();
						int newPosition = Integer.parseInt(input);
						if (neighbours.contains("V"+newPosition)) {
							robberPosition = newPosition;
							valid = true;
						} else {
							System.out.println("Not a valid move. Must choose adjacent vertex");
						}

					}
				} catch (java.lang.NumberFormatException e) {
					System.out.println("Vertex must be a number");
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Invalid vertex");
				}
			}
		}
		if (won) {
			System.out.println("Cops win in round " + round + "!");
		} else {
			System.out.println("Robber wins!");
		}
		scanner.close();
	}

	private void setCopNumber(int tmp) {
		// TODO Auto-generated method stub
		
	}
}
