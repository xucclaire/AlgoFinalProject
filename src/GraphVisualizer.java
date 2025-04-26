import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
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
    }

    static class Edge {
        Node from;
        Node to;

        public Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
        }
    }

    private final List<Edge> edges = new ArrayList<>();

    private final List<Node> nodes = new ArrayList<>();
    private final List<Node> clickedNodes = new ArrayList<>();
    private final JTextArea clickedNodeDisplay;

    public GraphVisualizer(JTextArea clickedNodeDisplay) {
        this.clickedNodeDisplay = clickedNodeDisplay;
        nodes.add(new Node("Portola Road Entrance", 350, 750, true));
        nodes.add(new Node("ARC/Student Center", 750, 312, true));
        nodes.add(new Node("Dining Hall", 750, 250, true));
        nodes.add(new Node("Learning Commons", 935, 295, true));
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
        nodes.add(new Node("Church Square", 992, 187, false));
        nodes.add(new Node("Schilling Square", 682, 436, false));
        nodes.add(new Node("Maker Court", 660, 187, false));
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
        nodes.add(new Node("B8", 771, 555, true));
        nodes.add(new Node("B9", 790, 550, true));

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

        nodes.add(new Node("S203", 631, 9, true));
        nodes.add(new Node("S202", 705, 105, true));
        nodes.add(new Node("S201", 755, 110, true));



        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Node node : nodes) {
                    if (node.visible && node.contains(e.getX(), e.getY())) {
                        clickedNodes.add(node);
                        updateClickedText();
                        repaint();
                        break;
                    }
                }
            }
        });
    }

    private void updateClickedText() {
        StringBuilder sb = new StringBuilder("Clicked Nodes:\n");
        for (Node n : clickedNodes) {
            sb.append("- ").append(n.name).append("\n");
        }
        clickedNodeDisplay.setText(sb.toString());
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Node node : nodes) {
            g.setColor(Color.BLUE);
            g.fillOval(node.x - 10, node.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(node.x - 10, node.y - 10, 20, 20);
            g.drawString(node.name, node.x + 15, node.y + 5);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Campus Map - Click to Track Nodes");

        JTextArea clickedNodeText = new JTextArea(20, 25);
        clickedNodeText.setEditable(false);
        JScrollPane textScroll = new JScrollPane(clickedNodeText);

        GraphVisualizer panel = new GraphVisualizer(clickedNodeText);
        panel.setPreferredSize(new Dimension(1300, 770));
        JScrollPane graphScroll = new JScrollPane(panel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphScroll, textScroll);
        splitPane.setResizeWeight(1.0);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}