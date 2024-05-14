package com.verygoodbank.tes.web.transform;

import org.apache.catalina.connector.ClientAbortException;

import java.io.IOException;

/**
 * This exception is used for handling a situation, when the client resets the connection aborting the transform
 */
public class TransformAbortedException extends RuntimeException {
    final IOException reason;

    public TransformAbortedException(IOException reason) {
        this.reason = reason;
    }

    public IOException getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return getReason() instanceof ClientAbortException ?
                "Transform aborted by client." : getReason().toString();
    }
}
