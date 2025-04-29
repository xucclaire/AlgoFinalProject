import java.util.List;

public class CampusNavigator{

    private final AdjacencyListGraph graph;
    private final double backpackMultiplier;

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

    public CArrayList<Position> computeOptimalPathSegment(
            int start,
            int end,
            boolean startCarrying,
            boolean endCarrying
    ) {
        PriorityQueue<Position> queue = new PriorityQueue<>();
        CArrayList<Position> bestPositions = new CArrayList<>();

        Position startPosition = new Position(
                start,
                startCarrying,
                start,
                0.0,
                null,
                "start"
        );
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
                double dropCost = current.cost + 0.01;
                Position dropPosition = new Position(
                        current.vertex,
                        false,
                        current.vertex,
                        dropCost,
                        current,
                        "drop backpack"
                );
                if (updateBestPosition(dropPosition, bestPositions)) {
                    queue.add(dropPosition);
                }
            }
            else {
                if (current.vertex == current.backpackLocation) {
                    double pickupCost = current.cost + 0.01;
                    Position pickupPosition = new Position(
                            current.vertex,
                            true,
                            current.backpackLocation,
                            pickupCost,
                            current,
                            "pick up backpack"
                    );
                    if (updateBestPosition(pickupPosition, bestPositions)) {
                        queue.add(pickupPosition);
                    }
                }
            }

            SinglyLinkedList<Edge> neighbors = graph.getNeighbors(current.vertex);
            for (Edge edge : neighbors) {
                int nextVertex = edge.to;
                double edgeCost;
                if (current.carrying) {
                    edgeCost = current.cost + edge.weight * backpackMultiplier;
                } else {
                    edgeCost = current.cost + edge.weight;
                }

                String moveAction;
                if (current.carrying) {
                    moveAction = "move from " + current.vertex +
                            " to " + nextVertex +
                            " with backpack";
                } else {
                    moveAction = "move from " + current.vertex +
                            " to " + nextVertex +
                            " without backpack";
                }

                Position nextPosition = new Position(
                        nextVertex,
                        current.carrying,
                        current.backpackLocation,
                        edgeCost,
                        current,
                        moveAction
                );
                if (updateBestPosition(nextPosition, bestPositions)) {
                    queue.add(nextPosition);
                }
            }
        }

        CArrayList<Position> path = new CArrayList<>();
        if (goalPosition != null) {
            Position cursor = goalPosition;
            while (cursor != null) {
                path.add(0, cursor);
                cursor = cursor.prev;
            }
        }
        return path;
    }

    public CArrayList<Position> computeFullSchedule(CArrayList<ScheduleEntry> schedule) {
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
            int segmentStart = currentPosition.vertex;
            int segmentEnd = schedule.get(i + 1).vertex;
            boolean segStartCarrying = currentPosition.carrying;
            boolean segEndCarrying = schedule.get(i + 1).requiresBackpack;

            if (!segStartCarrying && segEndCarrying && segmentStart != currentBackpackLocation) {
                CArrayList<Position> toBackpack = computeOptimalPathSegment(segmentStart, currentBackpackLocation, false, false);
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

            CArrayList<Position> segment = computeOptimalPathSegment(currentPosition.vertex, segmentEnd, currentPosition.carrying, segEndCarrying);
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