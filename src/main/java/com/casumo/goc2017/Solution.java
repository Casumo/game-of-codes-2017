package com.casumo.goc2017;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.*;
import java.util.Iterator;

public class Solution {

    public static final String COFFEE_ATTRIBUTE = "c";

    public static void main(String args[]) throws Exception {
        Solution solution = new Solution();
        try (FileReader input = new FileReader(args[0])) {
            solution.compute(input, args[1]);
        }
    }

    public String startTownId;
    public String endTownId;
    public int timeLimit;
    public SingleGraph graph;

    public void compute(Reader input, String output) throws Exception {
        BufferedReader reader = new BufferedReader(input);
        String commaSeparatedTowns = reader.readLine();
        String[] townIds = commaSeparatedTowns.split(",");
        startTownId = reader.readLine();
        endTownId = reader.readLine();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            writer.write(endTownId);
            writer.flush();
        }
        timeLimit = Integer.parseInt(reader.readLine());

        graph = new SingleGraph("journey", false, true, townIds.length * timeLimit, (townIds.length * timeLimit) * 3);
        int uniqueId = 0;
        for (String townId : townIds) {
            if (townId.equals(endTownId)) {
                graph.addNode(townId + "@0");
            } else {
                if (townId.startsWith("*") || townId.equals(startTownId)) {
                    for (int i = 0; i < timeLimit - 1; i++) {
                        Edge edge = graph.addEdge("" + uniqueId++, townId + "@" + i, townId + "@" + (i + 1), true);
                        edge.addAttribute("length", townId.equals(startTownId) ? 0 : 1);
                        edge.addAttribute(COFFEE_ATTRIBUTE, true);
                    }
                }
            }
        }

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineComponents = line.split(",");
            String sourceTownId = lineComponents[0];
            String destinationTownId = lineComponents[1];
            if (sourceTownId.equals(endTownId)) {
                continue;
            }
            if (destinationTownId.equals(startTownId)) {
                continue;
            }
            for (int sourceTime = 0; sourceTime < timeLimit; sourceTime++) {
                int journeyDuration = Integer.parseInt(lineComponents[sourceTime + 2]);
                int destinationTime = sourceTime + journeyDuration;
                if (destinationTime >= timeLimit) {
                    continue;
                }
                String sourceVertexId = sourceTownId + "@" + sourceTime;
                String destinationVertexId;
                if (destinationTownId.equals(endTownId)) {
                    destinationVertexId = destinationTownId + "@0";
                } else {
                    destinationVertexId = destinationTownId + "@" + destinationTime;
                }
                Edge edge = graph.addEdge("" + uniqueId++, sourceVertexId, destinationVertexId, true);
                edge.addAttribute("length", journeyDuration);
            }
        }

        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(startTownId + "@0"));
        dijkstra.compute();
        Iterator<Path> iterator = dijkstra.getAllPathsIterator(graph.getNode(endTownId + "@0"));
        Path bestPath = null;
        long bestNumberOfTowns = 0;
        long bestCoffeeTime = 0;
        do {
            boolean write = false;
            Path candidatePath = iterator.next();
            long candidateNumberOfTowns = candidatePath.getEdgeSet().stream()
                                                       .filter(edge -> !edge.hasAttribute(COFFEE_ATTRIBUTE))
                                                       .count();
            long candidateTimeSpentDrinkingCoffee = candidatePath.getEdgePath().stream()
                                                                 .filter(edge -> edge.hasAttribute(COFFEE_ATTRIBUTE))
                                                                 .count();
            if (bestPath == null) {
                bestPath = candidatePath;
                bestNumberOfTowns = candidateNumberOfTowns;
                bestCoffeeTime = candidateTimeSpentDrinkingCoffee;
                write = true;
            } else if (candidateNumberOfTowns < bestNumberOfTowns) {
                bestPath = candidatePath;
                bestNumberOfTowns = candidateNumberOfTowns;
                bestCoffeeTime = candidateTimeSpentDrinkingCoffee;
                write = true;
            } else if (candidateNumberOfTowns == bestNumberOfTowns && candidateTimeSpentDrinkingCoffee > bestCoffeeTime) {
                bestPath = candidatePath;
                bestNumberOfTowns = candidateNumberOfTowns;
                bestCoffeeTime = candidateTimeSpentDrinkingCoffee;
                write = true;
            }

            if (write) {
                int coffeeCount = 0;
                BufferedWriter writer = new BufferedWriter(new FileWriter(output));
                for (Edge edge : bestPath.getEachEdge()) {
                    if (edge.hasAttribute(COFFEE_ATTRIBUTE)) {
                        coffeeCount++;
                    } else {
                        if (coffeeCount > 0) {
                            writer.write("COFFEE " + coffeeCount);
                            writer.newLine();
                            coffeeCount = 0;
                        }
                        writer.write(edge.getTargetNode().getId().substring(0, edge.getTargetNode().getId().indexOf("@")));
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
            }
        } while (iterator.hasNext());
    }
}
