package packagecontrol;

public class Task implements Runnable {

    private int taskNo;

    public Task(int taskNo) {
        this.taskNo = taskNo;
    }

    public void run() {

    }

    public int getTaskNo() {
        return taskNo;
    }
}
