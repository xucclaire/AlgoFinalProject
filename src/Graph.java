interface Graph {
    void addEdge(int from, int to);

    boolean hasEdge(int from, int to);

    void printNeighbors(int vertex);

    void printMaxDegree();
}