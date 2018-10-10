package com.quicktutorialz.nio.servlets;

import com.quicktutorialz.nio.services.ReactiveService;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;

/* non-blocking servlet which calls a Service and uses Flowable */

public class NioToServiceServlet extends HttpServlet {

    private String contenuto;

    private final ReactiveService service = new ReactiveService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name")!=null ? request.getParameter("name") : "USER";
        service.getFlowableData(name).subscribe(res -> setContent(res));
        ByteBuffer finalContent = ByteBuffer.wrap(contenuto.getBytes());

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

    private void setContent(String content){
        this.contenuto = content;
    }

}
