package pcd.framework;

public enum SetupConstants {
    NUM_WORKERS(10);

    private final int value;

    private SetupConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
