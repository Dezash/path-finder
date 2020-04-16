package sample;

import java.util.*;

public class Graph<E extends Comparable<E>, U extends ICost> implements IGraph<E, U>, Cloneable
{
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    private Node rootNode = null;

    // Number of edges
    private int size;

    public Graph()
    {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        size = 0;
    }

    public void setRootElement(E element)
    {
        Node node = findNode(element);
        if(node != null)
            rootNode = node;
    }



    private Edge findEdge(E from, E to)
    {
        for(Edge edge : edges)
        {
            if(edge.from.element.equals(from) && edge.to.element.equals(to))
            {
                return edge;
            }
        }
        return null;
    }

    private Edge findEdge(Node from, Node to)
    {
        for(Edge edge : edges)
        {
            if(edge.from.equals(from) && edge.to.equals(to))
            {
                return  edge;
            }
        }
        return  null;
    }

    private Node findNode(E elem)
    {
        for(Node node : nodes)
        {
            if(node.element.equals(elem))
                return node;
        }
        return null;
    }

    @Override
    public void add(E from, E to, U weight)
    {
        Edge currentEdge = findEdge(from, to);
        if(currentEdge == null)
        {
            Edge newEdge = new Edge(from, to, weight);
            edges.add(newEdge);
            size++;
        }
        else
        {
            currentEdge.weight = weight;
        }
    }

    public void add(E from, E to)
    {
        add(from, to, null);
    }

    public boolean removeNode(E element)
    {
        Node node = findNode(element);
        if(node != null)
        {
            for(Node theNode : nodes)
            {
                if(theNode != node)
                {
                    theNode.incoming.remove(node);
                    theNode.outgoing.remove(node);
                }
            }
            edges.removeIf((edge -> edge.from == node || edge.to == node));
            size = edges.size();
            return nodes.remove(node);
        }
        return false;
    }

    @Override
    public boolean remove(E from, E to)
    {
        Edge edge = findEdge(from, to);
        if(edge != null)
        {
            Node fromNode = findNode(from);
            Node toNode = findNode(to);
            if(fromNode != null && toNode != null)
            {
                fromNode.outgoing.remove(toNode);
                toNode.incoming.remove(fromNode);
            }

            edges.remove(edge);
            size--;
            return true;
        }
        return false;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(Node node : nodes)
        {
            sb.append(node.toString()).append('\n');
        }
        return sb.toString();
    }


    protected class Node implements Comparable<Node> {
        protected E element;

        Node previous = null;
        CostVector minDistance = new CostVector(Integer.MAX_VALUE, Integer.MAX_VALUE);

        ArrayList<Node> incoming;
        ArrayList<Node> outgoing;
        boolean visited;

        Node(E element)
        {
            this.element = element;
            visited = false;
            incoming = new ArrayList<>();
            outgoing = new ArrayList<>();
        }

        public void addIncomingVertex(Node node)
        {
            incoming.add(node);
        }

        public void addOutgoingVertex(Node node)
        {
            outgoing.add(node);
        }

        @Override
        public String toString()
        {
            return element.toString();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(element, node.element);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(element);
        }

        @Override
        public int compareTo(Node o)
        {
            return minDistance.compareTo(o.minDistance);
        }
    }

    protected class Edge
    {
        public Node from;
        public Node to;
        public U weight;

        public Edge(E from, E to)
        {
            this.from = findNode(from);
            if(this.from == null)
            {
                this.from = new Node(from);
                nodes.add(this.from);
            }

            this.to = findNode(to);
            if(this.to == null)
            {
                this.to = new Node(to);
                nodes.add(this.to);
            }

            this.from.addOutgoingVertex(this.to);
            this.to.addIncomingVertex(this.from);

            this.weight = null;
        }

        public Edge(E from, E to, U weight)
        {
            this(from, to);
            this.weight = weight;
        }
    }

    @Override
    public boolean isEmpty()
    {
        return nodes.isEmpty();
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public int getNodeCount()
    {
        return nodes.size();
    }

    @Override
    public void clear()
    {
        nodes.clear();
        size = 0;
    }

    @Override
    public boolean contains(E element)
    {
        for(Node node : nodes)
        {
            if(node.element.equals(element))
                return true;
        }
        return false;
    }

    private void resetNodeStates()
    {
        for(Node node : nodes)
        {
            node.visited = false;
        }
    }


    public ArrayList<Path<E>> getAllPaths(E from, E to)
    {
        ArrayList<Path<E>> pathList = new ArrayList<>();

        Node source = findNode(from);
        Node target = findNode(to);
        if(source == null || target == null)
            return pathList;

        HashMap<Node, Boolean> bVisited = new HashMap<>();
        Path<E> currentPath = new Path<>();
        currentPath.distance = new CostVector(0, 0);
        currentPath.elements.add(from);
        calculateAllPaths(source, target, bVisited, pathList, currentPath);

        return pathList;
    }

    private void calculateAllPaths(Node source, Node target, HashMap<Node, Boolean> bVisited, List<Path<E>> pathList, Path<E> currentPath)
    {
        bVisited.put(source, true);

        if (source.equals(target))
        {
            pathList.add(new Path<E> (new ArrayList<>(currentPath.elements), (CostVector) currentPath.distance.clone()));
            bVisited.put(source, false);
            currentPath.distance = new CostVector(0, 0);
            return;
        }

        boolean isStart = currentPath.elements.get(0) == source.element;
        for(Node v : source.outgoing)
        {
            if(!bVisited.containsKey(v) || !bVisited.get(v))
            {
                Edge edge = findEdge(source, v);
                if(edge == null)
                    return;

                CostVector prevDis = currentPath.distance;
                currentPath.distance = currentPath.distance.add(edge.weight.getCost(currentPath.distance, isStart));
                currentPath.elements.add(v.element);
                calculateAllPaths(v, target, bVisited, pathList, currentPath);
                currentPath.elements.remove(currentPath.elements.size() - 1);
                currentPath.distance = prevDis;
            }
        }

        bVisited.put(source, false);
    }

    private Path<E> getShortestPath(Node target)
    {
        Path<E> path = new Path<>();

        if(target.minDistance.isMax())
        {
            return null;
        }

        for (Node v = target; v !=null; v = v.previous)
        {
            path.elements.add(v.element);
        }

        Collections.reverse(path.elements);
        path.distance = target.minDistance;
        return path;
    }


    public Path<E> getPath(E from, E to)
    {
        boolean calculated = calculatePaths(from);
        if(calculated)
        {
            Node toNode = findNode(to);
            if(toNode == null)
                return null;

            return getShortestPath(toNode);
        }
        return null;
    }

    private void resetPathParameters()
    {
        for(Node node : nodes)
        {
            node.minDistance = new CostVector(Integer.MAX_VALUE, Integer.MAX_VALUE);
            node.previous = null;
        }
    }

    // Dijkstra's algorithm
    private boolean calculatePaths(E from)
    {
        if(nodes.isEmpty())
            return false;

        resetPathParameters();

        Node source = findNode(from);
        if(source == null)
            return false;

        source.minDistance = new CostVector(0, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(source);

        while(!queue.isEmpty())
        {
            Node u = queue.poll();

            boolean isSource = u == source;
            for(Node v : u.outgoing)
            {
                Edge edge = findEdge(u, v);

                if(edge == null)
                    return false;

                CostVector distance = u.minDistance.add(edge.weight.getCost(u.minDistance, isSource));

                if(!edge.weight.canVisit(distance))
                    continue;

                if(distance.compareTo(v.minDistance) < 0)
                {
                    queue.remove(v);
                    v.minDistance = distance;
                    v.previous = u;
                    queue.add(v);
                }
            }

        }
        return true;
    }

    @Override
    public Iterator<E> iterator()
    {
        return new IteratorBreadthFirst();
    }

    private class IteratorBreadthFirst implements Iterator<E> {

        private Queue<Node> queue = new LinkedList<>();

        IteratorBreadthFirst()
        {
            if(rootNode == null)
            {
                rootNode = nodes.get(0);
            }

            if(rootNode != null)
                queue.add(rootNode);

            resetNodeStates();
        }

        IteratorBreadthFirst(Node startingNode) {
            resetNodeStates();
            queue.add(startingNode);
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public E next() {
            if (!queue.isEmpty()) {
                Node node = queue.poll();
                node.visited = true;

                for(Node outgoingNode : node.outgoing)
                {
                    if(!outgoingNode.visited)
                    {
                        queue.add(outgoingNode);
                        node.visited = true;
                    }
                }

                return node.element;
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            Node node = queue.poll();
            if(node != null)
                node.visited = true;
        }
    }
}

