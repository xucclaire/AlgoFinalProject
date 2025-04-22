import java.util.List;

public class CampusNavigator{

    private AdjacencyListGraph graph;
    private double backpackMultiplier;  // Multiplier for cost when carrying the backpack

    public CampusNavigator(AdjacencyListGraph graph, double carryingFactor) {
        this.graph = graph;
        this.backpackMultiplier = carryingFactor;
    }

    public class Position implements Comparable<Position> {
        int vertex;
        boolean carrying;
        int backpackLocation;
        double cost;
        Position prev;
        String action;

        public Position(int vertex, boolean carrying, int backpackLocation, double cost, Position prev, String action) {
            this.vertex = vertex;
            this.carrying = carrying;
            this.backpackLocation = backpackLocation;
            this.cost = cost;
            this.prev = prev;
            this.action = action;
        }

        @Override
        public int compareTo(Position other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    private boolean updateBestPosition(Position candidate, CArrayList<Position> bestPositions) {
        for (int i = 0; i < bestPositions.size(); i++) {
            Position existing = bestPositions.get(i);
            if (existing.vertex == candidate.vertex &&
                    existing.carrying == candidate.carrying &&
                    existing.backpackLocation == candidate.backpackLocation) {
                if (candidate.cost < existing.cost) {
                    bestPositions.set(i, candidate);
                    return true;
                }
                return false;
            }
        }
        bestPositions.add(candidate);
        return true;
    }

    public CArrayList<Position> computeOptimalPathSegment(int start, int end, boolean startCarrying, boolean endCarrying) {
        PriorityQueue<Position> queue = new PriorityQueue<>();
        CArrayList<Position> bestPositions = new CArrayList<>();

        Position startPosition = new Position(start, startCarrying, start, 0.0, null, "start");
        updateBestPosition(startPosition, bestPositions);
        queue.add(startPosition);

        Position goalPosition = null;

        while (!queue.isEmpty()) {
            Position current = queue.removeMin();

            if (current.vertex == end && current.carrying == endCarrying) {
                goalPosition = current;
                break;
            }

            if (current.carrying) {
                Position dropPosition = new Position(current.vertex, false, current.vertex,
                        current.cost, current, "drop backpack");
                if (updateBestPosition(dropPosition, bestPositions)) {
                    queue.add(dropPosition);
                }
            } else {
                if (current.vertex == current.backpackLocation) {
                    Position pickupPosition = new Position(current.vertex, true, current.backpackLocation,
                            current.cost, current, "pick up backpack");
                    if (updateBestPosition(pickupPosition, bestPositions)) {
                        queue.add(pickupPosition);
                    }
                }
            }

            SinglyLinkedList<Edge> neighbors = graph.getNeighbors(current.vertex);
            for (Edge edge : neighbors) {
                int nextVertex = edge.to;
                double edgeCost = current.carrying ? edge.weight * backpackMultiplier : edge.weight;
                Position nextPosition;
                if (current.carrying) {
                    nextPosition = new Position(nextVertex, true, current.backpackLocation,
                            current.cost + edgeCost, current,
                            "move from " + current.vertex + " to " + nextVertex + " with backpack");
                } else {
                    nextPosition = new Position(nextVertex, false, current.backpackLocation,
                            current.cost + edgeCost, current,
                            "move from " + current.vertex + " to " + nextVertex + " without backpack");
                }
                if (updateBestPosition(nextPosition, bestPositions)) {
                    queue.add(nextPosition);
                }
            }
        }

        CArrayList<Position> path = new CArrayList<>();
        if (goalPosition != null) {
            Position s = goalPosition;
            while (s != null) {
                path.add(0, s);
                s = s.prev;
            }
        }
        return path;
    }

    public CArrayList<Position> computeFullSchedule(List<ScheduleEntry> schedule) {
        CArrayList<Position> fullPath = new CArrayList<>();
        if (schedule == null || schedule.isEmpty()) {
            return fullPath;
        }

        int currentVertex = schedule.get(0).vertex;
        boolean currentCarrying = true;
        int currentBackpackLocation = currentVertex;

        Position currentPosition = new Position(currentVertex, currentCarrying, currentBackpackLocation, 0.0, null, "start at " + currentVertex);
        fullPath.add(currentPosition);

        for (int i = 0; i < schedule.size() - 1; i++) {
            int segStart = currentPosition.vertex;
            int segEnd = schedule.get(i + 1).vertex;
            boolean segStartCarrying = currentPosition.carrying;
            boolean segEndCarrying = schedule.get(i + 1).requiresBackpack;

            if (!segStartCarrying && segEndCarrying && segStart != currentBackpackLocation) {
                CArrayList<Position> toBackpack = computeOptimalPathSegment(segStart, currentBackpackLocation, false, false);
                if (!toBackpack.isEmpty()) {
                    toBackpack.removeIndex(0);
                    fullPath.addAll(toBackpack);
                    currentPosition = fullPath.get(fullPath.size() - 1);
                }

                CArrayList<Position> pickUp = computeOptimalPathSegment(currentPosition.vertex, currentPosition.vertex, false, true);
                if (!pickUp.isEmpty()) {
                    pickUp.removeIndex(0);
                    fullPath.addAll(pickUp);
                    currentPosition = fullPath.get(fullPath.size() - 1);
                }
            }

            CArrayList<Position> segment = computeOptimalPathSegment(currentPosition.vertex, segEnd, currentPosition.carrying, segEndCarrying);
            if (!segment.isEmpty()) {
                segment.removeIndex(0);
                fullPath.addAll(segment);
                currentPosition = fullPath.get(fullPath.size() - 1);
            }

            if (!currentPosition.carrying) {
                currentBackpackLocation = currentPosition.backpackLocation;
            } else {
                currentBackpackLocation = currentPosition.vertex;
            }
        }

        return fullPath;
    }

    public void printPath(CArrayList<Position> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            Position position = path.get(i);
            System.out.println("Vertex: " + position.vertex +
                    ", Carrying: " + position.carrying +
                    ", Backpack at: " + position.backpackLocation +
                    ", Cost: " + position.cost +
                    ", Action: " + position.action);
        }
    }
}