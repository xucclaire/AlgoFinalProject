import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.PriorityQueue;

public class GraphVisualizer extends JPanel {

    static class Node {
        String name;
        int x, y;
        boolean visible;

        public Node(String name, int x, int y, boolean visible) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.visible = visible;
        }

        public boolean contains(int mouseX, int mouseY) {
            int radius = 10;
            return Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2) <= radius * radius;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class VisualEdge {
        int from;
        int to;
        double weight;

        public VisualEdge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private final CArrayList<Node> nodes = new CArrayList<>();
    private final CArrayList<Edge> edges = new CArrayList<>();
    private final AdjacencyListGraph graph;


    private Node startNode = null;
    private Node endNode = null;
    private CArrayList<Node> pathNodes = new CArrayList<>();

    private JPanel clickedNodesPanel;
    private JScrollPane clickedScrollPane;
    private CArrayList<JCheckBox> backpackCheckboxes = new CArrayList<>();

    public GraphVisualizer() {
        clickedNodesPanel = new JPanel();
        clickedNodesPanel.setLayout(new BoxLayout(clickedNodesPanel, BoxLayout.Y_AXIS));
        clickedScrollPane = new JScrollPane(clickedNodesPanel);
        clickedScrollPane.setPreferredSize(new Dimension(300, 770));  // width for side panel

        setupNodes();
        graph = new AdjacencyListGraph(nodes.size(), false);
        setupEdges();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    resetSelection();
                    return;
                }
                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    if (node.visible && node.contains(e.getX(), e.getY())) {
                        handleClick(node);
                        break;
                    }
                }
                repaint();
            }
        });
    }

    private final CArrayList<Node> clickedNodes = new CArrayList<>();

    private void handleClick(Node node) {
        if (clickedNodes.isEmpty()) {
            clickedNodes.add(node);
            pathNodes = new CArrayList<>();
            pathNodes.add(node);
        } else {
            Node lastNode = clickedNodes.get(clickedNodes.size() - 1);
            clickedNodes.add(node);

            int startIdx = getNodeIndex(lastNode);
            int endIdx = getNodeIndex(node);

            int[] prev = dijkstra(startIdx);
            CArrayList<Node> segment = reconstructPath(prev, startIdx, endIdx);

            if (segment.size() > 1) {
                for (int i = 1; i < segment.size(); i++) {
                    pathNodes.add(segment.get(i));
                }
            }
        }
        addCheckboxForNode(node);
        repaint();
    }
    private void addCheckboxForNode(Node node) {
        JCheckBox box = new JCheckBox(node.name);
        box.setSelected(true);
        backpackCheckboxes.add(box);
        clickedNodesPanel.add(box);
        clickedNodesPanel.revalidate();
        clickedNodesPanel.repaint();
    }
    private void resetSelection() {
        clickedNodes.clear();
        pathNodes = new CArrayList<>();
        backpackCheckboxes = new CArrayList<>();
        clickedNodesPanel.removeAll();
        clickedNodesPanel.revalidate();
        clickedNodesPanel.repaint();
    }


    private void findPath() {
        if (startNode == null || endNode == null) return;

        int startIdx = getNodeIndex(startNode);
        int endIdx = getNodeIndex(endNode);

        int[] prev = dijkstra(startIdx);
        pathNodes = reconstructPath(prev, startIdx, endIdx);

//        updateClickedText("Path from " + startNode.name + " to " + endNode.name + ":");
    }

    private int getNodeIndex(Node node) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == node) {
                return i;
            }
        }
        return -1;
    }

    private int[] dijkstra(int start) {
        int n = graph.getVertices();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Double.compare(a[1], b[1]));
        dist[start] = 0;
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.to;
                double weight = edge.weight;
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    prev[v] = u;
                    pq.offer(new int[]{v, (int) dist[v]});
                }
            }
        }
        return prev;
    }

    private CArrayList<Node> reconstructPath(int[] prev, int start, int end) {
        CArrayList<Node> path = new CArrayList<>();
        int at = end;
        while (at != -1) {
            path.add(0, nodes.get(at));
            at = prev[at];
        }
        if (!path.isEmpty() && getNodeIndex(path.get(0)) == start) {
            return path;
        }
        return new CArrayList<>();
    }

    private void setupNodes() {
        nodes.add(new Node("Portola Road Entrance", 350, 750, true));
        nodes.add(new Node("ARC", 740, 312, true));
        nodes.add(new Node("Student Center", 770, 330, true));
        nodes.add(new Node("Dining Hall", 765, 250, true));
        nodes.add(new Node("Gym", 120, 530, true));
        nodes.add(new Node("Pool", 190, 500, true));
        nodes.add(new Node("Locker Rooms", 190, 580, true));
        nodes.add(new Node("Basketball Courts", 140, 360, true));
        nodes.add(new Node("Tennis Courts", 80, 270, true));
        nodes.add(new Node("Kovacs Field", 480, 750, true));
        nodes.add(new Node("Father Christopher Field/Track", 800, 750, true));
        nodes.add(new Node("Kalman Field", 50, 62, true));
        nodes.add(new Node("Founders Hall", 505, 560, true));
        nodes.add(new Node("PA1 Theater", 390, 500, true));
        nodes.add(new Node("PA2 Black Box", 375, 580, true));
        nodes.add(new Node("PA3 Orchestra", 375, 650, true));
        nodes.add(new Node("PA4 Choir", 440, 640, true));
        nodes.add(new Node("STREAM Center", 680, 175, true));
        nodes.add(new Node("Nurse's Office/Health Center", 1070, 550, true));
        nodes.add(new Node("Boys Dorm", 1000, 505, true));
        nodes.add(new Node("Girls Dorm", 1000, 390, true));
        nodes.add(new Node("Guest House", 1000, 6000, true));
        nodes.add(new Node("Chapel", 500, 400, true));
        nodes.add(new Node("Monastery", 440, 125, true));
        nodes.add(new Node("Faculty Housing", 30, 480, true));
        nodes.add(new Node("Franklin Garden", 800, 30, true));
        nodes.add(new Node("Fitness Room", 130, 440, true));

        nodes.add(new Node("B1", 610, 595, true));
        nodes.add(new Node("B2", 631, 589, true));
        nodes.add(new Node("B3", 652, 583, true));
        nodes.add(new Node("B4", 673, 577, true));
        nodes.add(new Node("B5", 694, 571, true));
        nodes.add(new Node("B6", 715, 567, true));
        nodes.add(new Node("B7", 750, 561, true));
        nodes.add(new Node("B8/B9", 771, 555, true));

        nodes.add(new Node("B10", 673, 538, true));
        nodes.add(new Node("B11", 710, 532, true));
        nodes.add(new Node("B12", 750, 524, true));
        nodes.add(new Node("B13", 790, 518, true));

        nodes.add(new Node("B14", 750, 430, true));
        nodes.add(new Node("B15", 730, 410, true));
        nodes.add(new Node("B16", 710, 390, true));

        nodes.add(new Node("B17", 640, 375, true));
        nodes.add(new Node("B18", 620, 385, true));
        nodes.add(new Node("B19", 600, 395, true));

        nodes.add(new Node("B20", 600, 430, true));

        nodes.add(new Node("B21", 652, 320, true));
        nodes.add(new Node("B22", 631, 330, true));
        nodes.add(new Node("B23", 610, 340, true));
        nodes.add(new Node("B24", 589, 350, true));

        nodes.add(new Node("MS Admin", 900, 180, true));
        nodes.add(new Node("C1", 920, 170, true));
        nodes.add(new Node("C2", 940, 160, true));
        nodes.add(new Node("C3", 960, 150, true));

        nodes.add(new Node("C4", 925, 125, true));
        nodes.add(new Node("C5", 945, 115, true));
        nodes.add(new Node("C6", 965, 105, true));

        nodes.add(new Node("C7", 925, 250, true));
        nodes.add(new Node("C8", 945, 265, true));
        nodes.add(new Node("C9/Learning Commons", 960, 290, true));
        nodes.add(new Node("C10", 990, 290, true));
        nodes.add(new Node("C11", 1020, 287, true));
        nodes.add(new Node("C12", 1050, 285, true));

        nodes.add(new Node("S101", 750, 140, true));
        nodes.add(new Node("S102/S103", 720, 135, true));
        nodes.add(new Node("S104", 690, 130, true));

        nodes.add(new Node("S105", 631, 110, true));
        nodes.add(new Node("S106", 631, 120, true));

        nodes.add(new Node("S203", 631, 109, true));
        nodes.add(new Node("S202", 705, 105, true));
        nodes.add(new Node("S201", 755, 110, true));

        nodes.add(new Node("Junior Parking", 631, 85, true));

        nodes.add(new Node("Church Square", 992, 187, false));
        nodes.add(new Node("Schilling Square", 682, 436, false));
        nodes.add(new Node("Maker Court", 660, 187, false));
        nodes.add(new Node("ITN-B3-B4", 653, 560, false));
        nodes.add(new Node("Maker Court", 660, 187, false));
        nodes.add(new Node("Breezeway", 725, 550, false));
        nodes.add(new Node("Fr.Egon Plaza", 440, 580, false));

        nodes.add(new Node("ITN-Dine-MS", 653, 560, false));
        nodes.add(new Node("ITN-SRM-MS", 653, 560, false));



    }

    private void setupEdges() {
        addEdgeByNames("B1", "B2", 11.03);
        addEdgeByNames("B1", "B10", 19.82);
        addEdgeByNames("B2", "B3", 2.01);
        addEdgeByNames("B3", "B4", 1.9);
        addEdgeByNames("B3", "ITN-B3-B4", 1);

        addEdgeByNames("ITN-B3-B4", "B10", 11.80);
        addEdgeByNames("B4", "B5", 5.16);
        addEdgeByNames("B4", "ITN-B3-B4", 1);

        addEdgeByNames("B5", "B6", 4.86);
        addEdgeByNames("B6", "Breezeway", 3.33);
        addEdgeByNames("B7", "Breezeway", 3.88);
        addEdgeByNames("B7", "B8/B9", 5.15);

        addEdgeByNames("Breezeway", "B12", 12.38);
        addEdgeByNames("B13", "B12", 8.8);
        addEdgeByNames("B14", "B12", 10);
        addEdgeByNames("B12", "B11", 14.7);
        addEdgeByNames("B10", "B11", 3.8);
        addEdgeByNames("B13", "Student Center", 25.94);


        addEdgeByNames("B10", "Schilling Square", 7.56);
        addEdgeByNames("Schilling Square", "B12", 22.57);
        addEdgeByNames("Schilling Square", "B14", 14.03);
        addEdgeByNames("Schilling Square", "B17", 18.41);
        addEdgeByNames("Schilling Square", "B20", 13.01);
        addEdgeByNames("Schilling Square", "Chapel", 23.6);


        addEdgeByNames("B14", "B15", 4);
        addEdgeByNames("B15", "B16", 5.45);
        addEdgeByNames("B16", "B17", 8.47);
        addEdgeByNames("B17", "B18", 7.85);
        addEdgeByNames("B18", "B20", 6.78);
        addEdgeByNames("B18", "B19", 4.58);
        addEdgeByNames("B19", "Chapel", 9.7);
        addEdgeByNames("B24", "Chapel", 14.54);
        addEdgeByNames("B24", "B23", 1);
        addEdgeByNames("B22", "B23", 7.91);
        addEdgeByNames("B22", "B21", 1);
        addEdgeByNames("B17", "B21", 15.75);

        addEdgeByNames("ARC", "B21", 29.12);
        addEdgeByNames("Student Center", "B21", 32.2);
        addEdgeByNames("Student Center", "ARC", 18.7);
        addEdgeByNames("Student Center", "Dining Hall", 22.75);

        addEdgeByNames("Maker Court", "Dining Hall", 14.27);
        addEdgeByNames("Maker Court", "S104", 2);
        addEdgeByNames("Maker Court", "S106", 10);
        addEdgeByNames("Maker Court", "S203", 17.24);
        addEdgeByNames("Maker Court", "S106", 7.4);
        addEdgeByNames("Junior Parking", "S106", 17.17);
        addEdgeByNames("S202", "S203", 12.3);
        addEdgeByNames("S202", "S201", 7.75);
        addEdgeByNames("S201", "S101", 14.08);
        addEdgeByNames("S102/S103", "S101", 5);
        addEdgeByNames("S102/S103", "S104", 5);
        addEdgeByNames("S101", "Dining Hall", 22.75);

        /*
        addEdgeByNames("ITN-Dine-MS", "Dining Hall", 22.75);
        addEdgeByNames("ITN-Dine-MS", "ITN-SRM-MS", 22.75);
        addEdgeByNames("ITN-Dine-MS", "C1", 22.75);
        addEdgeByNames("ITN-Dine-MS", "C7", 22.75);
        addEdgeByNames("ITN-Dine-MS", "Church Square", 22.75);

        addEdgeByNames("S101", "ITN-SRM-MS", 22.75);
        addEdgeByNames("C4", "ITN-SRM-MS", 22.75);
        addEdgeByNames("C4", "C5", 22.75);
        addEdgeByNames("C6", "C5", 22.75);
        addEdgeByNames("C6", "C3", 22.75);
        addEdgeByNames("C2", "C3", 22.75);
        addEdgeByNames("C2", "C1", 22.75);
        addEdgeByNames("C2", "C7", 22.75);
        addEdgeByNames("C8", "C7", 22.75);
        addEdgeByNames("C8", "C9", 22.75);
        addEdgeByNames("C10", "C9", 22.75);
        addEdgeByNames("C10", "C11", 22.75);
        addEdgeByNames("C12", "C11", 22.75);

        addEdgeByNames("C10", "Church Square", 22.75);
        addEdgeByNames("C3", "Church Square", 22.75);
        addEdgeByNames("C8", "Church Square", 22.75);



*/
        addEdgeByNames("B1", "Founders Hall", 28.28);
        addEdgeByNames("Fr.Egon Plaza", "Founders Hall", 8);
        addEdgeByNames("Fr.Egon Plaza", "Schilling Square", 55.77);
        addEdgeByNames("Fr.Egon Plaza", "PA1 Theater", 4.49);
        addEdgeByNames("Fr.Egon Plaza", "PA2 Black Box", 3);
        addEdgeByNames("Fr.Egon Plaza", "PA4 Choir", 4.49);
        addEdgeByNames("PA4 Choir", "PA3 Orchestra", 6.2);
        addEdgeByNames("PA2 Black Box", "PA3 Orchestra", 7.13);
        addEdgeByNames("PA2 Black Box", "PA1 Theater", 9.67);








    }

    private void addEdgeByNames(String name1, String name2, double weight) {
        int idx1 = findNodeIndexByName(name1);
        int idx2 = findNodeIndexByName(name2);
        if (idx1 != -1 && idx2 != -1) {
            graph.addEdge(idx1, idx2, weight);
            edges.add(new Edge(idx1, idx2, weight));
            edges.add(new Edge(idx2, idx1, weight));
        }
    }

    private int findNodeIndexByName(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw all nodes
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.visible) {
                g.setColor(Color.BLUE);
                g.fillOval(node.x - 10, node.y - 10, 20, 20);
                g.setColor(Color.BLACK);
                g.drawRect(node.x - 10, node.y - 10, 20, 20);
                g.drawString(node.name, node.x + 15, node.y + 5);
            }
        }

        // Draw path lines
        if (pathNodes.size() >= 2) {
            g.setColor(Color.RED);
            for (int i = 0; i < pathNodes.size() - 1; i++) {
                Node a = pathNodes.get(i);
                Node b = pathNodes.get(i + 1);
                g.drawLine(a.x, a.y, b.x, b.y);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Campus Map - Pathfinding");

        GraphVisualizer panel = new GraphVisualizer();
        panel.setPreferredSize(new Dimension(1300, 770));
        JScrollPane graphScroll = new JScrollPane(panel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphScroll, panel.clickedScrollPane);
        splitPane.setResizeWeight(1.0);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}