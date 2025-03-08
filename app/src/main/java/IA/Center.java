package IA;

public class Center {
    private static int maxConnections = 25;
    private static int maxMbps = 150;

    private int cx;
    private int cy;

    public Center(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
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

    public static int getMaxConnections() {
        return maxConnections;
    }

    public static int getMaxMbps() {
        return maxMbps;
    }
}
