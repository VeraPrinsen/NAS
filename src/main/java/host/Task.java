package host;

import gui.InfoGUI;

public class Task implements Runnable {

    private Host host;
    private boolean isPaused;

    public Task(Host host) {
        this.host = host;
        isPaused = false;
    }

    public void run() {

    }

    public Host getHost() {
        return host;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
    }

}
