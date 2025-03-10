package IA.State;

public abstract class Node {
    private final int maxConnections;

    private int cx;
    private int cy;

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

    public void setCx(int cx) {
        this.cx = cx;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    abstract public boolean isCenter();

    /**
     * @return Squared distance to the other node
     */
    public int sqDistance(Node node) {
        int dx = cx - node.cx;
        int dy = cy - node.cy;
        return dx * dx + dy * dy;
    }

    /**
     * @return Squared distance to the other node
     */
    public double distance(Node node) {
        return Math.sqrt(sqDistance(node));
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
