package com.study.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyTomcat {
   private int port = 8088;
   private Map<String,String> urlServletMap = new HashMap<String,String>();

    public MyTomcat(int port) {
        this.port = port;
    }
    public void  start(){
        initServletMapping();
        ServerSocket serverSocket = null;
        try {
            serverSocket= new ServerSocket(port);
            System.out.println("MyTomcat is start...");
            while (true){
                Socket accept = serverSocket.accept();
                InputStream inputStream = accept.getInputStream();
                OutputStream outputStream = accept.getOutputStream();
                MyRequest myRequest = new MyRequest(inputStream);
                MyResponse myResponse = new MyResponse(outputStream);
                dispatch(myRequest,myResponse);
                accept.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initServletMapping(){
        for (ServletMapping servletMapping : ServletMappingConfig.servletMappingList) {
            urlServletMap.put(servletMapping.getUrl(),servletMapping.getClazz());
        }
    }
    public void dispatch(MyRequest request,MyResponse response){
        String clazz = urlServletMap.get(request.getUrl());
        if (clazz == null){return;}
        try {
            Class<MyServlet> myServletClass  = (Class<MyServlet>) Class.forName(clazz);
            MyServlet myServlet = null;
            try {
                myServlet = myServletClass.newInstance();
                myServlet.service(request,response);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            myServlet.service(request,response);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        new MyTomcat(8088).start();
    }
}
