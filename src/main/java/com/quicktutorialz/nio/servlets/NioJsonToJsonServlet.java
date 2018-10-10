package com.quicktutorialz.nio.servlets;

import com.google.gson.Gson;
import com.quicktutorialz.nio.entities.UserData;
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

/* non-blocking asyncronous servlet with flowable, json in input, json in output */

public class NioJsonToJsonServlet extends HttpServlet {


    private String contenuto;

    private final ReactiveService service = new ReactiveService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        Gson gson = new Gson();

        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }
        UserData userData = (UserData) gson.fromJson(sb.toString(), UserData.class);
        service.getFlowableUser(userData).subscribe(res -> wrapResponse(gson.toJson(res)));
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

    private void wrapResponse(String jsonResponse){
        this.contenuto = jsonResponse;
    }

}

