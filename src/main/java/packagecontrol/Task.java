package packagecontrol;

public class Task implements Runnable {

    private byte taskNo;
    private byte sequenceNo;

    public Task(byte taskNo) {
        this.taskNo = taskNo;
        sequenceNo = 1;
    }

    public void run() {

    }
}
