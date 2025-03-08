package IA;

/*
 * unidireccional edge from nodeA -> nodeB
 */
public class Edge {
    private int nodeA;
    private int nodeB;

    public Edge(int nodeA, int nodeB)
    {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public int getNodeA() {
        return nodeA;
    }

    public int getNodeB() {
        return nodeB;
    }

    public void setNodeA(int nodeA) {
        this.nodeA = nodeA;
    }

    public void setNodeB(int nodeB) {
        this.nodeB = nodeB;
    }
}


