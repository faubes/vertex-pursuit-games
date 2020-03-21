package vpg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import vpg.graphgenerators.SampleGraphGenerator;

public class ZombiesAndSurvivorsBasic {
	
	public static void main(String[] args) {

		final int MAXROUNDS = 15;

		Graph<Integer, Integer> g;

		int zombieNumber = 0;

		Integer[] zombiePositions;

		Scanner scanner = new Scanner(System.in);

		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges

		g = SampleGraphGenerator.generateOuterPlanarGraph5();

		DijkstraShortestPath<Integer, Integer> alg = new DijkstraShortestPath<Integer, Integer>(g);

		
		boolean won = false;
		
		String input;

		System.out.println("Graph: ");
		System.out.println(g);

		boolean valid = false;

		while (!valid) {
			try {
				System.out.print("Enter the zombie number: ");
				input = scanner.nextLine();
				int tmp = Integer.parseInt(input);
				if (tmp <= 0) {
					System.out.println("You need at least one zombie to play.");
				} else if (tmp >= g.getVertexCount()) {
					System.out.println("That's probably too many zombies.");
				} else {
					zombieNumber = tmp;
					valid = true;
				}
			} catch (java.lang.NumberFormatException e) {
				System.out.println("zombie number must be a number");
			}
		}

		zombiePositions = new Integer[zombieNumber]; // vertices are strings of
													// form "V"+k

		System.out.println("Zombies start by selecting " + zombieNumber + " vertices.");

		for (int i = 0; i < zombieNumber; i++) {
			valid = false;
			while (!valid) {
				try {
					System.out.print("Enter start vertex for zombie " + i + ": ");
					input = scanner.nextLine();
					int pos = Integer.parseInt(input);
					zombiePositions[i] = pos;
					valid = true;

				} catch (java.lang.NumberFormatException e) {
					System.out.println("Vertex must be a number.");
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Invalid vertex.");
				}
			}
		}

		System.out.println("Zombies are occupying vertices " + Arrays.toString(zombiePositions));

		Integer survivorPosition = 0;

		System.out.println("Survivor selects start vertex.");
		valid = false;
		while (!valid) {
			try {
				HashSet<Integer> survivorTerritory = new HashSet<>();
				survivorTerritory.addAll(g.getVertices());
				for (Integer i : zombiePositions) {
					if (survivorTerritory.contains(i)) {	
						survivorTerritory.remove(i);
					}
					for (Integer j : g.getNeighbors(i)) {
						if (survivorTerritory.contains(j)) {
							survivorTerritory.remove(j);
						}
					}
				}
				System.out.println("Safe choices are " + survivorTerritory.toString());

				System.out.print("Enter start vertex for survivor: ");

				input = scanner.nextLine();

				int pos = Integer.parseInt(input);
				
				if (survivorTerritory.contains(pos)) {
					survivorPosition = pos;
					valid = true;
				} else
				{
					System.out.println("Not a valid start vertex.");
				}
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Vertex must be a number.");
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Invalid vertex.");
			}
		}

		int round = 0;

		while (!won && round < MAXROUNDS) {
			round++;
			System.out.println();
			;
			System.out.println("Round " + round);
			for (int i = 0; i < zombieNumber; i++) {
				Integer currentPos = zombiePositions[i];
				HashSet<Integer> neighbours = (HashSet<Integer>) g.getNeighbors(currentPos);
				System.out.println();
				System.out.println("Zombie " + i + " occupies vertex " + zombiePositions[i]);
				System.out.println("Possible moves are : " + neighbours.toString());
				List<Integer> l = alg.getPath(currentPos, survivorPosition);
				Integer firstEdge = l.get(0);
				Integer newPosition = g.getOpposite(currentPos, firstEdge);
				System.out.println("Zombie " + i + " moves to vertex " + newPosition);
				zombiePositions[i] = newPosition;
				if (newPosition.equals(survivorPosition)) {
					System.out.println("zombie " + i + " catches the robber!");
					won = true;
				}
			}

			valid = false;
			while (!valid & !won)

			{
				try {
					HashSet<Integer> neighbours = (HashSet<Integer>) g.getNeighbors(survivorPosition);
					HashSet<Integer> zombies = new HashSet<Integer>(Arrays.asList(zombiePositions));
					neighbours.removeAll(zombies);
					if (neighbours.isEmpty()) {
						System.out.println("The survivor is surrounded!");
						valid = true;
						won = true;
					} else {
						System.out.println("Survivor occupies vertex " + survivorPosition);
						System.out.println("Possible moves are : " + neighbours.toString());
						System.out.print("Enter move for survivor: ");
						input = scanner.nextLine();
						int newPosition = Integer.parseInt(input);
						if (neighbours.contains(newPosition)) {
							survivorPosition = newPosition;
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
			System.out.println("Zombies win in round " + round + "!");
		} else {
			System.out.println("Survivor wins!");
		}
		scanner.close();
	}

}
