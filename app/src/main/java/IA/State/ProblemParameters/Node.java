package IA.State.ProblemParameters;

public abstract class Node {
    public final int x;
    public final int y;
    public final int maxConnections;
    public final int id;

    public Node(int cx, int cy, int maxConnections, int id) {
        this.x = cx;
        this.y = cy;
        this.maxConnections = maxConnections;
        this.id = id;
    }

    /**
     * @return Squared distance to the other node
     */
    public int sqDistanceTo(Node node) {
        int dx = x - node.x;
        int dy = y - node.y;
        return dx * dx + dy * dy;
    }

    /**
     * @return Squared distance to the other node
     */
    public double distanceTo(Node node) {
        return Math.sqrt(sqDistanceTo(node));
    }

    abstract public int maxReceivingVolume();
}
