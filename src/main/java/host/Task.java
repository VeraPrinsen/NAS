package host;

import gui.InfoGUI;

/**
 * Each packet that is send belongs to a general Task.
 * A task has a taskNo and can be paused.
 * TODO: Implement taskNo here, not in the specific Tasks
 */
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
