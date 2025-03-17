package IA.State.ProblemParameters;

public abstract class Node {
    private final int cx;
    private final int cy;
    private final int maxConnections;

    public Node(int cx, int cy, int maxConnections) {
        this.cx = cx;
        this.cy = cy;
        this.maxConnections = maxConnections;
    }

    public int getCx() {
        return cx;
    }

    public int getCy() {
        return cy;
    }

    /**
     * @return Squared distance to the other node
     */
    public int sqDistanceTo(Node node) {
        int dx = cx - node.cx;
        int dy = cy - node.cy;
        return dx * dx + dy * dy;
    }

    /**
     * @return Squared distance to the other node
     */
    public double distanceTo(Node node) {
        return Math.sqrt(sqDistanceTo(node));
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
