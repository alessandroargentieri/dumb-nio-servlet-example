package com.quicktutorialz.nio.servlets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.util.Queue;

public class WriteListenerImpl implements WriteListener{

    private ServletOutputStream output = null;
    private Queue queue = null;
    private AsyncContext context = null;

    WriteListenerImpl(ServletOutputStream sos, Queue q, AsyncContext c) {
        output  = sos;
        queue   = q;
        context = c;
    }

    @Override
    public void onWritePossible() throws IOException {
        while (queue.peek() != null && output.isReady()) {
            String data = (String) queue.poll();
            output.print(data);
        }
        if (queue.peek() == null) {
            context.complete();
        }
    }

    @Override
    public void onError(Throwable t) {
        context.complete();
        t.printStackTrace();
    }
}
