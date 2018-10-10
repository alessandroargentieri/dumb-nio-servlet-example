package com.quicktutorialz.nio;

import com.quicktutorialz.nio.servlets.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class MainApplication {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8383);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BlockingServlet.class, "/status");
        servletHandler.addServletWithMapping(NioLambdaServlet.class, "/nio1");
        servletHandler.addServletWithMapping(NioToServiceServlet.class, "/nio2");
        servletHandler.addServletWithMapping(NioJsonToJsonServlet.class, "/nio3");
        server.setHandler(servletHandler);
        server.start();
        server.join();
    }

}
