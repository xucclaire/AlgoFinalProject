public class Dijkstras {
    private final AdjacencyListGraph graph;
    private final int v;
    private final double[] distTo;
    private final int[] edgeTo;
    private final boolean[] visited;
    private final PriorityQueue<Edge> pq;


    public Dijkstras(AdjacencyListGraph graph) {
        this.graph = graph;
        this.v = graph.getVertices();
        this.distTo = new double[v];
        this.edgeTo = new int[v];
        this.visited = new boolean[v];
        this.pq = new PriorityQueue<>(v);

        for (int i = 0; i < v; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
            edgeTo[i] = -1;
            visited[i] = false;
        }
    }

    public void findShortestPath(int source, int target) {
        distTo[source] = 0;
        pq.add(new Edge(source, source, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.removeMin();
            int u = current.to;

            if (visited[u]) continue;
            visited[u] = true;

            SinglyLinkedList<Edge> neighbors = graph.getNeighbors(u);
            int size = neighbors.size();
            for (int i = 0; i < size; i++) {
                Edge edge = neighbors.getNodeAt(i).getItem();
                if (!visited[edge.to] && distTo[u] != Double.POSITIVE_INFINITY && distTo[u] + edge.weight < distTo[edge.to]) {
                    distTo[edge.to] = distTo[u] + edge.weight;
                    edgeTo[edge.to] = u;
                    pq.add(new Edge(u, edge.to, distTo[edge.to]));
                }
            }
        }
        printDikPath(source, target);
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public int edgeTo(int v) {
        return edgeTo[v];
    }

    private void printDikPath(int source, int target) {
        int[] pathVertices = new int[v];
        int pathIndex = 0;
        int current = target;

        while (edgeTo[current] != -1) {
            pathVertices[pathIndex++] = current;
            current = edgeTo[current];
        }

        if (current != source) {
            throw new IllegalArgumentException("No path found.");
        }

        pathVertices[pathIndex++] = source;

        double totalWeight = 0;
        for (int i = pathIndex - 1; i > 0; i--) {
            int from = pathVertices[i];
            int to = pathVertices[i - 1];
            double weight = getEdgeWeight(from, to);
            System.out.println(from + " " + to + " " + weight);
            totalWeight += weight;
        }
        System.out.println(totalWeight);
    }

    private double getEdgeWeight(int from, int to) {
        SinglyLinkedList<Edge> neighbors = graph.getNeighbors(from);
        int size = neighbors.size();
        for (int i = 0; i < size; i++) {
            Edge edge = neighbors.getNodeAt(i).getItem();
            if (edge.to == to) {
                return edge.weight;
            }
        }
        return Double.POSITIVE_INFINITY;
    }
}