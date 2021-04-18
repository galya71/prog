package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class Controller {

    private Thread thread;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button buttonPauseContinue;

    public Thread createThread(final Runnable runnable, final String name) {
        if (runnable != null) {
            Thread thread;

            if (name != null) {
                thread = new Thread(runnable, name);
            } else {
                thread = new Thread(runnable);
            }

            thread.setDaemon(true);
            return thread;
        }

        return null;
    }

    public boolean threadNotNull(final Thread thread) {
        return thread != null;
    }

    public void interruptThread(final Thread thread) {
        if (threadNotNull(thread) && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public void startThread(final Thread thread) {
        if (threadNotNull(thread)) {
            thread.start();
        }
    }

    @FXML
    public void startIterations() {
        if (threadNotNull(thread) && thread.isAlive()) {
            interruptThread(thread);
        }

        thread = createThread(new Iterations(progressBar), "Iterations");
        thread.setDaemon(true);

        if (!"PAUSE".equals(buttonPauseContinue.getText())) {
            buttonPauseContinue.setText("PAUSE");
        }

        progressBar.setProgress(0d);

        startThread(thread);
    }

    @FXML
    public void stopIterations() {
        interruptThread(thread);
        thread = null;

        if (!"PAUSE".equals(buttonPauseContinue.getText())) {
            buttonPauseContinue.setText("PAUSE");
        }

        progressBar.setProgress(0d);
    }

    @FXML
    public void pauseOrContinueIterations() {
        final double savedProgress = progressBar.getProgress();

        if (threadNotNull(thread)) {
            if (thread.isAlive()) {
                interruptThread(thread);
                buttonPauseContinue.setText("CONTINUE");
            } else {
                thread = createThread(new Iterations(progressBar), "Iterations");
                thread.setDaemon(true);
                progressBar.setProgress(savedProgress);

                buttonPauseContinue.setText("PAUSE");
                startThread(thread);
            }
        }
    }
}