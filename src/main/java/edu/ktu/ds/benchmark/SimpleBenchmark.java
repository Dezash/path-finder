package edu.ktu.ds.benchmark;

import sample.Graph;
import sample.Path;

import java.util.ArrayList;



public class SimpleBenchmark {

    static final int OPERATION_COUNT = 10;
    static final int[] LIST_SIZES = {64_00, 400, 800, 1600, 3200};

    public static void main(String[] args) {
        runBenchmark();
    }

    static void runBenchmark() {
        int[] from = new int[OPERATION_COUNT];
        int[] to = new int[OPERATION_COUNT];
        System.out.format("%1$8s%2$16s%n", "", "dijkstra");
        for (int listSize : LIST_SIZES) {
            Util.generateIndexes(from, to, listSize);
            long graphTime = generateAndRun(new sample.Graph<Float, TestCost>(), listSize, from, to);
            System.out.format("%1$8s%2$16s%n", listSize, graphTime);
        }
    }

    static long generateAndRun(Graph<Float, TestCost> graph, int listSize, int[] from, int[] to) {
        ArrayList<Float> elements = Util.generateGraph(graph, listSize);
        return measureTime(() -> {
            for (int i = 0; i < OPERATION_COUNT; i++) {
                Path<Float> path = graph.getPath(elements.get(from[i]), elements.get(to[i]));

            }
        }
        );
    }

    static long measureTime(Runnable code) {
        long start = System.nanoTime();
        code.run();
        return System.nanoTime() - start;
    }
}
