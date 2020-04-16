package edu.ktu.ds.benchmark;

import sample.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    static Random generator = new Random();

    static ArrayList<Float> generateGraph(Graph<Float, TestCost> graph, int size) {
        ArrayList<Float> elements = new ArrayList<>();
        elements.add(generator.nextFloat());
        for (int i = 0; i < size; i++) {
            Float nextFloat = generator.nextFloat();
            graph.add(elements.get(generator.nextInt(elements.size())), nextFloat, new TestCost(generator.nextInt()));
            elements.add(nextFloat);
        }
        return elements;
    }
    
    static void generateIndexes(int[] from, int[] to, int listSize) {
        for (int i = 0; i < from.length; i++) {
            from[i] = generator.nextInt(listSize);
            to[i] = generator.nextInt(listSize);
        }
    }
}
