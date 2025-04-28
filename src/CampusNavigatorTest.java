import java.util.ArrayList;
import java.util.List;

public class CampusNavigatorTest {
    public static void main(String[] args) {
        // Create a graph with 6 vertices (0 to 5)
        AdjacencyListGraph schoolMap = new AdjacencyListGraph(6, false);

        // Add edges between rooms (undirected for simplicity)
        schoolMap.addEdge(0, 1, 5);  // Entrance to Econ
        schoolMap.addEdge(1, 2, 7);  // Econ to Lunch area
        schoolMap.addEdge(2, 3, 6);  // Lunch area to Chem
        schoolMap.addEdge(3, 4, 4);  // Chem to Gym
        schoolMap.addEdge(4, 5, 6);  // Gym to Library
        schoolMap.addEdge(1, 3, 2);  // Shortcut Econ to Chem (longer)

        // Create a schedule
        // Suppose:
        // 0 - Entrance
        // 1 - Econ (requires backpack)
        // 2 - Lunch (does NOT require backpack)
        // 3 - Chem (requires backpack)
        // 5 - Library (does NOT require backpack)
        CArrayList<ScheduleEntry> schedule = new CArrayList<>();
        schedule.add(new ScheduleEntry(0, true));   // Start with backpack
        schedule.add(new ScheduleEntry(1, true));   // Econ
        schedule.add(new ScheduleEntry(2, false));  // Lunch
        schedule.add(new ScheduleEntry(3, true));   // Chem
        schedule.add(new ScheduleEntry(5, false));  // Library

        // Set up the navigator
        double carryingFactor = 1.5; // walking is 50% harder with backpack
        CampusNavigator navigator = new CampusNavigator(
                schoolMap, carryingFactor
        );

        // Compute and print the route
        CArrayList<CampusNavigator.Position> path = navigator.computeFullSchedule(schedule);
        System.out.println("Optimal Path with Backpack Logic:");
        navigator.printPath(path);
    }
}