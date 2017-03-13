# Game of Codes 2017 (Casumo Submission)

In this repository, you'll find Kyle Pullicino's (@KPull) submission, representing Casumo, for the
Game of Codes 2017 programming competition held in February 2017 by the University of Malta ICTSA.

You will find the problem statement attached as a PDF file in the repository. You can also build the
solution using gradle by typing in,

```
./gradlew build
```

After building, run the solution as follows,

```
java -jar build/libs/journey-1.0-SNAPSHOT.jar input.rds output.rt
```

# Solution Overview

The program in `Solution.java` is a solution which always gives the optimal path (provided it doesn't crash due to memory constraints) as
follows:

1. Construct a graph containing every pair of town/time unit possible except for the destination (there is only one destination node).
    * Let's say there is a town called "Zebbug", and the maximum time limit is 1000, then the graph will contain nodes, `Zebbug@0`, `Zebbug@1`,
      `Zebbug@2` and so on until `Zebbug@1000`.
2. For every possible connection described in the input file, have an edge between the relevant town (at the relevant time). Assign a weight
to the edge depending on the time it takes to travel that road at that particular time.
    * An exception: don't create edges for anything that will exceed the time limit.
3. For coffee stops, create edges of weight 1 which stay at the same town.
    * For example, if a coffee stop exists at `*SanGwann`, we will have edges from `*SanGwann@0` to `*SanGwann@1` which in turn is connected
    `SanGwann@2` and so on.
    * Exception: The coffee edges at our home town will have edge weight of 0!
4. Use Dijkstra's algorithm to find the shortest path from the home town at time 0 to the destination town.
5. Choose the shortest path which wins tiebreakers.

Note: there is a better, more efficient way to use Dijkstra to also find the path which wins tiebreakers if you encode all the necessary information
into the edge weights.

# Disclaimer

Since the competitors have a time limit to make their submission, there was no attempt to make the code look good. Please do not use the code
in this repository as an example of how to build good, readable, maintainable software. Otherwise, feel free to discuss the solution and ask
questions!