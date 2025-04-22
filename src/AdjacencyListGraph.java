public class AdjacencyListGraph implements Graph {
    private SinglyLinkedList<Edge>[] adjacencyList;
    private int vertices;
    private boolean isDirected;


    public AdjacencyListGraph(int vertices, boolean isDirected) {
        this.vertices = vertices;
        this.isDirected = isDirected;
        this.adjacencyList = new SinglyLinkedList[vertices];

        for (int i = 0; i < vertices; i++) {
            adjacencyList[i] = new SinglyLinkedList<>();
        }
    }

    //O(1)
    @Override
    public void addEdge(int from, int to) {
        addEdge(from, to, 1);
    }

    public void addEdge(int from, int to, double weight) {
        if (from >= vertices || to >= vertices) {
            throw new IndexOutOfBoundsException("Vertex index out of bounds.");
        }
        Edge edge = new Edge(from, to, weight);
        adjacencyList[from].append(edge);
        if (!isDirected) {
            adjacencyList[to].append(new Edge(to, from, weight));
        }
    }

    //O(E)
    @Override
    public boolean hasEdge(int from, int to) {
        for (Edge edge : adjacencyList[from]) {
            if (edge.to == to) {
                return true;
            }
        }
        return false;
    }

    public CArrayList<Edge> getEdges() {
        CArrayList<Edge> edges = new CArrayList<>();

        for (int i = 0; i < vertices; i++) {
            for (Edge edge : adjacencyList[i]) {
                if (isDirected || i < edge.to) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    //O(ElogE)
    @Override
    public void printNeighbors(int vertex) {
        int[] neighbors = adjacencyList[vertex].toArray();
        for (int i = 0; i < neighbors.length; i++) {
            System.out.print(neighbors[i] + " ");
        }
        System.out.println();
    }

    //O(V)
    @Override
    public void printMaxDegree() {
        int maxDegree = -1;
        int maxVertex = -1;
        for (int i = 0; i < vertices; i++) {
            int degree = adjacencyList[i].size();
            if (degree > maxDegree || (degree == maxDegree && i < maxVertex)) {
                maxDegree = degree;
                maxVertex = i;
            }
        }
        System.out.println(maxVertex + " " + maxDegree);
    }

    //O(1)
    public SinglyLinkedList<Edge> getNeighbors(int vertex) {
        if (vertex >= vertices) {
            throw new IndexOutOfBoundsException("Vertex index out of bounds.");
        }
        return adjacencyList[vertex];
    }

    //O(V+E)
    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            if (adjacencyList[i].size() == 0) {
                continue;
            }
            System.out.print(i + ": ");
            for (Edge edge : adjacencyList[i]) {
                System.out.print("(" + edge.to + ", " + edge.weight + ") ");
            }
            System.out.println();
        }
    }

    public int getVertices() {
        return vertices;
    }

    public int getEdgeCount() {
        int count = 0;
        for (int i = 0; i < vertices; i++) {
            count += adjacencyList[i].size();
        }
        if (!isDirected) {
            count /= 2;
        }
        return count;
    }

}
