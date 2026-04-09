package edgecases;

import java.io.IOException;

public class CheckedExceptionsInCallbacks {
    interface ThrowingCallback {
        void run() throws IOException;
    }

    public void execute(ThrowingCallback callback) throws IOException {
        callback.run();
    }
}
