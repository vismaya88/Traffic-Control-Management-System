package dsa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

class Graph {
    private Map<String, Map<String, Integer>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addPlace(String place) {
        adjacencyList.put(place, new HashMap<>());
    }

    public void addConnection(String place1, String place2, int distance) {
        adjacencyList.get(place1).put(place2, distance);
        adjacencyList.get(place2).put(place1, distance);
    }

    public Map<String, Integer> getConnections(String place) {
        return adjacencyList.get(place);
    }

    public List<String> dijkstra(String source, String destination) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        for (String place : adjacencyList.keySet()) {
            distance.put(place, Integer.MAX_VALUE);
            previous.put(place, null);
        }

        distance.put(source, 0);
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            String currentPlace = priorityQueue.poll();

            for (Map.Entry<String, Integer> neighbor : adjacencyList.get(currentPlace).entrySet()) {
                int newDistance = distance.get(currentPlace) + neighbor.getValue();

                if (newDistance < distance.get(neighbor.getKey())) {
                    distance.put(neighbor.getKey(), newDistance);
                    previous.put(neighbor.getKey(), currentPlace);
                    priorityQueue.add(neighbor.getKey());
                }
            }
        }

        List<String> path = new ArrayList<>();
        String current = destination;
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);

        return path;
    }

    public int getPathCost(List<String> path) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            cost += adjacencyList.get(path.get(i)).get(path.get(i + 1));
        }
        return cost;
    }

    public Map<String, Integer> simulateTraffic(Map<String, Map<String, Integer>> trafficInfo, List<String> path) {
        Map<String, Integer> trafficCosts = new HashMap<>();
        int totalTrafficCost = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            String currentPlace = path.get(i);
            String nextPlace = path.get(i + 1);

            int trafficValue = trafficInfo.getOrDefault(currentPlace, new HashMap<>()).getOrDefault(nextPlace, 0);
            trafficCosts.put(currentPlace + " to " + nextPlace, trafficValue);

            totalTrafficCost += trafficValue;
        }

        System.out.println("\nTraffic on each segment:");
        for (Map.Entry<String, Integer> entry : trafficCosts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " minutes");
        }

        System.out.println("\nTotal traffic cost: " + totalTrafficCost + " minutes");
        return trafficCosts;
    }
}

public class TrafficControlSystem extends JFrame {
    private Graph graph;
    
    public TrafficControlSystem() {
        this.graph = new Graph();
        initializeGraph();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Traffic Control System");

        // Create components
        JLabel sourceLabel = new JLabel("Enter source city:");
        JTextField sourceTextField = new JTextField();
        JLabel destinationLabel = new JLabel("Enter destination city:");
        JTextField destinationTextField = new JTextField();
        JButton findPathButton = new JButton("Find Shortest Path");

        // Add action listener to the button
        findPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String source = sourceTextField.getText();
                String destination = destinationTextField.getText();

                // Finding shortest paths
                List<String> shortestPath = graph.dijkstra(source, destination);
                int shortestPathCost = graph.getPathCost(shortestPath);

                // Simulating traffic integration
                Map<String, Map<String, Integer>> trafficInfo = new HashMap<>();
                trafficInfo.put("Gachibowli", Map.of("Shamshabad", 10, "Kukatpally", 5, "Hi-Tech City", 8));
                trafficInfo.put("Shamshabad", Map.of("Kukatpally", 15, "Jubilee Hills", 7));
                trafficInfo.put("Kukatpally", Map.of("Mallapur", 12));
                trafficInfo.put("Mallapur", Map.of("Habsiguda", 8));
                trafficInfo.put("Habsiguda", Map.of("Jubilee Hills", 10, "Secunderabad", 5));
                trafficInfo.put("Jubilee Hills", Map.of("Secunderabad", 7, "Banjara Hills", 5));
                trafficInfo.put("Secunderabad", Map.of("Banjara Hills", 10));
                trafficInfo.put("Banjara Hills", Map.of("Manikonda", 12));
                trafficInfo.put("Hi-Tech City", Map.of("Manikonda", 20));

                Map<String, Integer> trafficCosts = graph.simulateTraffic(trafficInfo, shortestPath);

                // Choosing the path with less traffic
                int totalCostWithTraffic = shortestPathCost + trafficCosts.values().stream().mapToInt(Integer::intValue).sum();

                // Display the result
                JOptionPane.showMessageDialog(null,
                        "Shortest path from " + source + " to " + destination + ":\n" +
                                shortestPath +
                                "\nCost: " + shortestPathCost + " km" +
                                "\nTotal cost with traffic (avg speed of 60): " + totalCostWithTraffic + " minutes",
                        "Shortest Path Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
     // Apply styling for a more attractive GUI
        applyStyling(sourceLabel, destinationLabel, sourceTextField, destinationTextField, findPathButton);

        // Create layout
        setLayout(new GridLayout(4, 2, 10, 10));
        add(sourceLabel);
        add(sourceTextField);
        add(destinationLabel);
        add(destinationTextField);
        add(new JLabel()); // Empty label for spacing
        add(findPathButton);

        pack();
        setLocationRelativeTo(null); // Center the frame on the screen
    }
    
    private void applyStyling(Component... components) {
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        for (Component component : components) {
            if (component instanceof JLabel) {
                component.setFont(labelFont);
                ((JLabel) component).setForeground(Color.BLUE);
            } else if (component instanceof JTextField) {
                component.setFont(textFieldFont);
            } else if (component instanceof JButton) {
                component.setFont(buttonFont);
                ((JButton) component).setBackground(Color.GREEN);
                ((JButton) component).setForeground(Color.WHITE);
            }
        }
    }

    private void initializeGraph() {
        // (Same as before, add places and connections here)
    	// Adding places
        String[] places = {"Gachibowli", "Shamshabad", "Kukatpally", "Mallapur", "Hi-Tech City",
                "Habsiguda", "Jubilee Hills", "Secunderabad", "Banjara Hills", "Manikonda"};

        for (String place : places) {
            graph.addPlace(place);
        }

        // Adding connections
        graph.addConnection("Gachibowli", "Shamshabad", 30);
        graph.addConnection("Gachibowli", "Kukatpally", 15);
        graph.addConnection("Gachibowli", "Hi-Tech City", 10);
        graph.addConnection("Shamshabad", "Kukatpally", 40);
        graph.addConnection("Shamshabad", "Jubilee Hills", 25);
        graph.addConnection("Kukatpally", "Mallapur", 20);
        graph.addConnection("Mallapur", "Habsiguda", 5);
        graph.addConnection("Habsiguda", "Jubilee Hills", 12);
        graph.addConnection("Habsiguda", "Secunderabad", 8);
        graph.addConnection("Jubilee Hills", "Secunderabad", 18);
        graph.addConnection("Jubilee Hills", "Banjara Hills", 8);
        graph.addConnection("Secunderabad", "Banjara Hills", 15);
        graph.addConnection("Banjara Hills", "Manikonda", 22);
        graph.addConnection("Hi-Tech City", "Manikonda", 35);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrafficControlSystem().setVisible(true);
            }
        });
    }
}