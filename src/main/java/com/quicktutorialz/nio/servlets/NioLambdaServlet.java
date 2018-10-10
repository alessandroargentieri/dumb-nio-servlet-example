package com.quicktutorialz.nio.servlets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/* non-blocking asynchronous servlet with Callable (asynchronous) and Future */

public class NioLambdaServlet extends HttpServlet {

    private static String HEAVY_RESOURCE = "This is some heavy resource that will be served in an async and nio way to you ";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Callable<String> myFutureResp = () -> heavyTask(request);

        ByteBuffer content = null;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(myFutureResp);
            content = ByteBuffer.wrap(future.get().getBytes());
            executorService.shutdown();
        } catch (Exception e) {  e.printStackTrace();   }
        ByteBuffer finalContent = content;

        AsyncContext async = request.startAsync();

        ServletOutputStream out = response.getOutputStream();

        out.setWriteListener(new WriteListener() {

            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!finalContent.hasRemaining()) {
                        response.setStatus(200);
                        async.complete();
                        return;
                    }
                    out.write(finalContent.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }


    //task
    private String heavyTask(HttpServletRequest request) throws InterruptedException {
        Thread.sleep(5000);
        String name = request.getParameter("name")!=null ? request.getParameter("name") : "USER";
        return HEAVY_RESOURCE + name;
    }

}
