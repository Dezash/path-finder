package graphTest;

import edu.ktu.ds.benchmark.TestCost;
import org.junit.Assert;
import org.junit.Test;
import sample.Graph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


public class GraphTests
{
    // papildo bandomąjį prekių sąrašą iš failo
    private void loadFromFile(String nodeFileName, String edgeFileName, Graph<String, TestCost> graph, HashMap<Integer, String> nodes)
    {
        File nodeFile = new File(nodeFileName);
        try
        {
            Scanner scanner = new Scanner(nodeFile);
            while(scanner.hasNext())
            {
                int id = scanner.nextInt();
                String value = scanner.next();
                nodes.put(id, value);
            }
            scanner.close();
        } catch (IOException e)
        {
            System.err.println(e);
            System.err.println("!!! Klaida apdorojant failą " + nodeFileName);
        }


        File edgeFile = new File(edgeFileName);

        try
        {
            Scanner sc = new Scanner(edgeFile);
            while(sc.hasNext())
            {
                int idFrom = sc.nextInt();
                int idTo = sc.nextInt();
                int weight = sc.nextInt();

                TestCost cost = new TestCost(weight);
                graph.add(nodes.get(idFrom), nodes.get(idTo), cost);
            }
            sc.close();
        } catch (IOException e)
        {
            System.err.println("!!! Klaida apdorojant failą " + edgeFileName);
            e.printStackTrace();
        }
    }


    @Test
    public void testDijkstra()
    {
        Graph<String, TestCost> graph = new Graph<>();
        HashMap<Integer, String> nodes = new HashMap<>();
        loadFromFile("data/unit_test/nodes.txt", "data/unit_test/edges.txt", graph, nodes);
        Assert.assertEquals(6, graph.getNodeCount(), 0.0001);
        System.out.println("Nuo " + nodes.get(1) +  " iki " + nodes.get(4) + ":");
        sample.Path<String> path = graph.getPath(nodes.get(1), nodes.get(4));
        Assert.assertNotEquals(null, path);
        for(String str : path.elements)
        {
            System.out.println(str);
        }
        Assert.assertEquals(13, path.distance.price, 0.0001);
    }


    @Test
    public void testAdd()
    {
        Graph<Integer, TestCost> graph = new Graph<>();
        Assert.assertEquals(0, graph.size(), 0.0001);
        graph.add(1, 2);
        Assert.assertEquals(1, graph.size(), 0.0001);
        graph.add(2 , 3);
        Assert.assertEquals(2, graph.size(), 0.0001);
        graph.add(2 , 3);
        Assert.assertEquals(2, graph.size(), 0.0001);
    }


    @Test
    public void testRemove()
    {
        Graph<Integer, TestCost> graph = new Graph<>();
        Assert.assertEquals(0, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(0, graph.size(), 0.0001);
        graph.add(1, 2);
        Assert.assertEquals(true, graph.contains(1));
        Assert.assertEquals(2, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(1, graph.size(), 0.0001);
        graph.removeNode(1);
        Assert.assertEquals(false, graph.contains(1));
        Assert.assertEquals(1, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(0, graph.size(), 0.0001);

        graph.add(2, 3);
        Assert.assertEquals(2, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(1, graph.size(), 0.0001);
        graph.remove(2, 3);
        Assert.assertEquals(2, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(0, graph.size(), 0.0001);
    }

    @Test
    public void testClear()
    {
        Graph<Integer, TestCost> graph = new Graph<>();
        graph.add(1, 2);
        graph.add(2, 3);
        Assert.assertEquals(false, graph.isEmpty());
        Assert.assertEquals(3, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(2, graph.size(), 0.0001);
        graph.clear();
        Assert.assertEquals(true, graph.isEmpty());
        Assert.assertEquals(0, graph.getNodeCount(), 0.0001);
        Assert.assertEquals(0, graph.size(), 0.0001);
    }

    @Test
    public void testContains()
    {
        Graph<Integer, TestCost> graph = new Graph<>();
        Assert.assertEquals(false, graph.contains(1));
        Assert.assertEquals(false, graph.contains(2));
        graph.add(1, 2);
        Assert.assertEquals(true, graph.contains(1));
        Assert.assertEquals(true, graph.contains(2));
    }


}
