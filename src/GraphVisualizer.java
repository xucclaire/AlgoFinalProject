import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphVisualizer extends JPanel {
    static class Node {
        String name;
        int x, y;
        boolean visible;
        public Node(String name, int x, int y, boolean visible) {
            this.name = name; this.x = x; this.y = y; this.visible = visible;
        }
        public boolean contains(int mx, int my) {
            int r = 10;
            return (mx - x)*(mx - x) + (my - y)*(my - y) <= r*r;
        }
    }

    private final CArrayList<Node> nodes = new CArrayList<>();
    private final AdjacencyListGraph graph;
    private final CampusNavigator navigator;
    private CArrayList<ScheduleEntry> scheduleEntries = new CArrayList<>();

    private final CArrayList<Node> clickedNodes       = new CArrayList<>();
    private final CArrayList<JCheckBox> backpackBoxes  = new CArrayList<>();
    private CArrayList<Node> pathNodes                 = new CArrayList<>();

    private final JPanel    clickedNodesPanel;
    public  final JScrollPane clickedScrollPane;
    private final JTextArea actionsDisplay;

    public GraphVisualizer() {
        clickedNodesPanel = new JPanel();
        clickedNodesPanel.setLayout(new BoxLayout(clickedNodesPanel, BoxLayout.Y_AXIS));
        actionsDisplay = new JTextArea();
        actionsDisplay.setEditable(false);

        JPanel right = new JPanel(new BorderLayout());
        JScrollPane clicks = new JScrollPane(clickedNodesPanel);
        clicks.setPreferredSize(new Dimension(300, 400));
        JScrollPane actions = new JScrollPane(actionsDisplay);
        actions.setPreferredSize(new Dimension(300, 370));
        right.add(clicks, BorderLayout.NORTH);
        right.add(actions, BorderLayout.SOUTH);
        clickedScrollPane = new JScrollPane(right);
        clickedScrollPane.setPreferredSize(new Dimension(300, 770));

        setupNodes();
        graph     = new AdjacencyListGraph(nodes.size(), false);
        setupEdges();
        navigator = new CampusNavigator(graph, 1.5);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    resetSelection();
                    repaint();
                    return;
                }
                for (int i = 0; i < nodes.size(); i++) {
                    Node n = nodes.get(i);
                    if (n.visible && n.contains(e.getX(), e.getY())) {
                        handleClick(n);
                        repaint();
                        return;
                    }
                }
            }
        });
    }

    private void handleClick(Node node) {
        clickedNodes.add(node);
        addCheckboxFor(node);
        rerunSchedule();
    }

    private void addCheckboxFor(Node node) {
        JCheckBox cb = new JCheckBox(node.name, true);
        backpackBoxes.add(cb);
        clickedNodesPanel.add(cb);
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rerunSchedule();
                repaint();
            }
        });
        clickedNodesPanel.revalidate();
    }

    private void rerunSchedule() {
        if (navigator == null || clickedNodes.isEmpty()) {
            return;
        }

        scheduleEntries = new CArrayList<ScheduleEntry>();
        for (int i = 0; i < clickedNodes.size(); i++) {
            Node n = clickedNodes.get(i);
            boolean needBackpack;
            if (backpackBoxes.get(i).isSelected()) {
                needBackpack = true;
            } else {
                needBackpack = false;
            }
            scheduleEntries.add(new ScheduleEntry(getNodeIndex(n), needBackpack));
        }

        CArrayList<CampusNavigator.Position> fullPath =
                navigator.computeFullSchedule(scheduleEntries);

        pathNodes = new CArrayList<Node>();
        for (int i = 0; i < fullPath.size(); i++) {
            int v = fullPath.get(i).vertex;
            pathNodes.add(nodes.get(v));
        }

        StringBuilder sb = new StringBuilder();

        for (int step = 0; step < fullPath.size(); step++) {
            CampusNavigator.Position p = fullPath.get(step);

            String action;
            if (p.action == null) {
                action = "";
            } else {
                action = p.action.toLowerCase();
            }

            String nodeName = nodes.get(p.vertex).name;

            if (action.indexOf("drop") != -1) {
                sb.append("- Drop backpack at ").append(nodeName).append("\n");
            }
            if (action.indexOf("pick up") != -1 || action.indexOf("pickup") != -1) {
                sb.append("- Pick up backpack at ").append(nodeName).append("\n");
            }
            boolean isStart = action.startsWith("start");
            boolean isMove  = action.startsWith("move");
            if (isStart || isMove) {
                for (int i = 0; i < clickedNodes.size(); i++) {
                    Node clicked = clickedNodes.get(i);
                    if (getNodeIndex(clicked) == p.vertex) {
                        sb.append("- Arrived at ").append(nodeName);
                        if (p.carrying) {
                            sb.append(" (carrying backpack)");
                        } else {
                            sb.append(" (no backpack)");
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        actionsDisplay.setText(sb.toString());
        repaint();
    }

    private void resetSelection() {
        clickedNodes.clear();
        backpackBoxes.clear();
        pathNodes.clear();
        clickedNodesPanel.removeAll();
        actionsDisplay.setText("");
        clickedNodesPanel.revalidate();
        clickedNodesPanel.repaint();
    }

    private int getNodeIndex(Node node) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == node) return i;
        }
        return -1;
    }

    private void setupNodes() {
        nodes.add(new Node("Kalman Field", 50, 120, true));
        nodes.add(new Node("Tennis Courts", 150, 120, true));
        nodes.add(new Node("Basketball Courts", 140, 360, true));
        nodes.add(new Node("Portola Road Entrance", 350, 750, true));
        nodes.add(new Node("ARC", 760, 312, true));
        nodes.add(new Node("Student Center", 790, 330, true));
        nodes.add(new Node("Dining Hall", 790, 270, true));
        nodes.add(new Node("Gym", 120, 530, true));
        nodes.add(new Node("Pool", 190, 500, true));
        nodes.add(new Node("Locker Rooms", 190, 580, true));
        nodes.add(new Node("Kovacs Field", 480, 750, true));
        nodes.add(new Node("Father Christopher Field/Track", 800, 750, true));
        nodes.add(new Node("Founders Hall", 505, 560, true));
        nodes.add(new Node("PA1 Theater", 390, 500, true));
        nodes.add(new Node("PA2 Black Box", 375, 580, true));
        nodes.add(new Node("PA3 Orchestra", 375, 650, true));
        nodes.add(new Node("PA4 Choir", 440, 640, true));
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
        nodes.add(new Node("B2", 636, 589, true));
        nodes.add(new Node("B3", 662, 583, true));
        nodes.add(new Node("B4", 688, 577, true));
        nodes.add(new Node("B5", 714, 571, true));
        nodes.add(new Node("B6", 740, 567, true));
        nodes.add(new Node("B7", 795, 561, true));
        nodes.add(new Node("B8/B9", 821, 555, true));

        nodes.add(new Node("B10", 688, 538, true));
        nodes.add(new Node("B11", 714, 532, true));
        nodes.add(new Node("B12", 795, 524, true));
        nodes.add(new Node("B13", 821, 518, true));

        nodes.add(new Node("B14", 790, 430, true));
        nodes.add(new Node("B15", 760, 410, true));
        nodes.add(new Node("B16", 730, 390, true));

        nodes.add(new Node("B17", 688, 375, true));
        nodes.add(new Node("B18", 662, 385, true));
        nodes.add(new Node("B19", 636, 395, true));

        nodes.add(new Node("B20", 636, 440, true));

        nodes.add(new Node("B21", 714, 320, true));
        nodes.add(new Node("B22", 688, 330, true));
        nodes.add(new Node("B23", 662, 340, true));
        nodes.add(new Node("B24", 636, 350, true));

        nodes.add(new Node("MS Admin", 900, 180, true));
        nodes.add(new Node("C1", 925, 170, true));
        nodes.add(new Node("C2", 950, 160, true));
        nodes.add(new Node("C3", 975, 150, true));

        nodes.add(new Node("C4", 925, 125, true));
        nodes.add(new Node("C5", 950, 115, true));
        nodes.add(new Node("C6", 975, 105, true));

        nodes.add(new Node("C7", 925, 250, true));
        nodes.add(new Node("C8", 950, 270, true));
        nodes.add(new Node("C9/ Learning Commons", 975, 290, true));
        nodes.add(new Node("C10", 1005, 290, true));
        nodes.add(new Node("C11", 1035, 287, true));
        nodes.add(new Node("C12", 1065, 285, true));

        nodes.add(new Node("S101", 790, 180, true));
        nodes.add(new Node("S102/S103", 755, 165, true));
        nodes.add(new Node("S104", 720, 150, true));
        nodes.add(new Node("Maker Court", 680, 135, true));

        nodes.add(new Node("S105", 640, 120, true));
        nodes.add(new Node("S106", 640, 160, true));

        nodes.add(new Node("S203", 640, 65, true));
        nodes.add(new Node("S202", 720, 95, true));
        nodes.add(new Node("S201", 790, 125, true));

        nodes.add(new Node("Junior Parking", 580, 65, true));

        nodes.add(new Node("Church Square", 992, 187, false));
        nodes.add(new Node("Schilling Square", 682, 436, false));
        nodes.add(new Node("ITN-B3-B4", 653, 560, false));
        nodes.add(new Node("Maker Court", 650, 135, false));
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
        addEdgeByNames("Dining Hall", "ARC", 20.1);
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

    /**
     * Find the index of a node given its name.
     * @return index in nodes[], or –1 if not found.
     */
    private int findNodeIndexByName(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Conveniently add an undirected edge by referring to node names.
     */
    private void addEdgeByNames(String name1, String name2, double weight) {
        int index1 = findNodeIndexByName(name1);
        int index2 = findNodeIndexByName(name2);
        if (index1 == -1 || index2 == -1) {
            throw new IllegalArgumentException("Unknown node(s): " + name1 + ", " + name2);
        }
        graph.addEdge(index1, index2, weight);
    }

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

            String[] words = node.name.split(" ");
            int lineHeight = 15;
            for (int j = 0; j < words.length; j++) {
                g.drawString(words[j], node.x - 10, node.y + 25 + (j * lineHeight));
            }
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
        JFrame f = new JFrame("Campus Navigator");
        GraphVisualizer panel = new GraphVisualizer();
        panel.setPreferredSize(new Dimension(1300,770));
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(panel),
                panel.clickedScrollPane
        );
        split.setResizeWeight(1.0);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(split);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}