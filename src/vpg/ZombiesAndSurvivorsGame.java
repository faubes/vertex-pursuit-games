package vpg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import vpg.graphgenerators.SampleGraphGenerator;

public class ZombiesAndSurvivorsGame extends javax.swing.JApplet {

	private int maxRounds = 20;
	private int round = 0;
	private int zombieNumber = 1;
	private int survivorNumber = 1;
	private Integer[] zombiePositions;
	private Integer[] survivorPositions;
	private Graph<Integer, Integer> g;
	private DijkstraShortestPath<Integer, Integer> alg;
	private boolean won = false;
	private VisualizationViewer<Integer,Integer> vv = null;
    private AbstractLayout<Integer,Integer> layout = null;
    public static final int EDGE_LENGTH = 100;
    

	public ZombiesAndSurvivorsGame() {
		this.zombieNumber = 3;
		this.survivorNumber = 1;
		this.zombiePositions = new Integer[zombieNumber];
		this.survivorPositions = new Integer[survivorNumber];
	}
	
	public ZombiesAndSurvivorsGame(int zombieNumber, int survivorNumber) {
		this.zombieNumber = zombieNumber;
		this.survivorNumber = survivorNumber;
		this.zombiePositions = new Integer[zombieNumber];
		this.survivorPositions = new Integer[survivorNumber];
	}
	
	public int getZombieNumber() { 
		return zombieNumber;
	}
	
	public void setZombieNumber(int n) {
		zombieNumber = n;
		zombiePositions = new Integer[n];
	}
	
	public int getSurvivorNumber(int n) {
		return survivorNumber;
	}
	
	public void setSurvivorNumber(int n) {
		survivorNumber = n;
		survivorPositions = new Integer[n];
	}
	
	
	public int getMaxRounds() {
		return maxRounds;
	}

    @Override
    public void init() {

        //create a graph
    	Graph<Integer,Integer> ig = SampleGraphGenerator.generateOuterPlanarGraph5();

        ObservableGraph<Integer,Integer> og = new ObservableGraph<Integer,Integer>(ig);
        og.addGraphEventListener(new GraphEventListener<Integer,Integer>() {

			public void handleGraphEvent(GraphEvent<Integer, Integer> evt) {
				System.err.println("got "+evt);

			}});
        this.g = og;
        //create a graphdraw
        // Options for layout are AggregateLayout, BalloonLayout, CircleLayout, DAGLayout, FRLayout, FRLayout2,
        // ISOMLayout, KKLayout, ...
        layout = new KKLayout<Integer, Integer>(g);
        // layout = new KKLayout<Integer,Integer>(g);
//        ((FRLayout)layout).setMaxIterations(200);


        vv = new VisualizationViewer<Integer, Integer>(layout, new Dimension(600,600));

        JRootPane rp = this.getRootPane();
        rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(java.awt.Color.lightGray);
        getContentPane().setFont(new Font("Serif", Font.PLAIN, 12));

        // vv.getModel().getRelaxer().setSleepTime(500);
        vv.setGraphMouse(new DefaultModalGraphMouse<Integer, Integer>());

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.setForeground(Color.white);
        getContentPane().add(vv);
       
    }

    @Override
    public void start() {
        validate();
        vv.repaint();
    }
	
    public static void main(String[] args) {
    	ZombiesAndSurvivorsGame zasg = new ZombiesAndSurvivorsGame(3, 1);
    	zasg.setZombieNumber(3);
    	zasg.setSurvivorNumber(1);
    	
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(zasg);

    	zasg.init();
    	zasg.start();
    	frame.pack();
    	frame.setVisible(true);
    }
	
}
