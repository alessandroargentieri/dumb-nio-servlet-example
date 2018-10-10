package com.quicktutorialz.nio.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* non-blocking servlet which uses Read and Write listener, so it's reactive only in reading input from the request */

@WebServlet(name = "nioServlet", urlPatterns = {"/hello"}, asyncSupported=true)
public class NioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        AsyncContext context = request.startAsync();

        context.addListener( new AsyncListener() {

            @Override
            public void onComplete(AsyncEvent event) throws IOException {

                event.getSuppliedResponse().getOutputStream().print("Complete");

            }

            @Override
            public void onError(AsyncEvent event) {
                System.out.println(event.getThrowable());
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
            }

            @Override
            public void onTimeout(AsyncEvent event) {
                System.out.println("my asyncListener.onTimeout");
            }
        });

        ServletInputStream input = request.getInputStream();

        ReadListener readListener = new ReadListenerImpl(input, response, context);

        input.setReadListener(readListener);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
