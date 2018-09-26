package com.adobe.netty;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpDMErrorClient {

    public static void main(String[] args) {

        int count = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(count);


        for (int i = 1; i<=200; i ++)
            executorService.execute(()->{
                try {
                    URL request = new URL(new URL("http://localhost:8082"), "http://localhost:8082/v1/topics/direct_memory_test/messages");
                    HttpURLConnection conn = (HttpURLConnection) request.openConnection();
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(32000);
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-type","x-protobuf");
                    conn.setRequestProperty("Authorization", "Bearer " + new String(new char[1200]).replace("\0", "x"));
                    try (OutputStream postStream = conn.getOutputStream()) {
                        postStream.write(new byte[1000100]);
                    }
                    System.out.println("Result: " + conn.getResponseCode());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

    }
}
