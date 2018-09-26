package com.adobe.netty;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpPOSTSender {

    public static void main(String[] args) {

        int count = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(count);


     for (int i = 1; i<=2000; i ++)
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
             conn.setRequestProperty("Authorization", "Bearer " + "eyJ4NXUi34iJpbXNfbmExLXN0ZzEta2V5LTEuY2672yIiwiYWxnIjoiUlMyNTYifQ.eyJpZCI6IjE1Mzc4MzEzMTUyMzNfM2QwODg5MWMtNDM4MS00ZmNkLWJiNjgtNjg5MGIwOTExNWYzX3VlMSIsImNsaWVudF9pZCI6ImFjcF9jb3JlX3BpcGVsaW5lIiwidXNlcl9pZCI6ImFjcF9jb3JlX3BpcGVsaW5lQEFkb2JlSUQiLCJ0eXBlIjoiYWNjZXNzX3Rva2VuIiwiYXMiOiJpbXMtbmExLXN0ZzEiLCJwYWMiOiJhY3BfY29yZV9waXBlbGluZV9zdGciLCJydGlkIjoiMTUzNzgzMTMxNTIzNV80OGI3MTkxOS05M2EyLTQyYzEtYWQ3Yi1iMTI2ZWY3MGFlOThfdWUxIiwicnRlYSI6IjE1MzkwNDA5MTUyMzUiLCJtb2kiOiI4YjM5NjNlZiIsImMiOiJ3NGczMXpsVWhoQmRjdHZYdDErbmlBPT0iLCJleHBpcmVzX2luIjoiODY0MDAwMDAiLCJzY29wZSI6InJlYWRfcGMuZG1hX3RhcnRhbixhY3AuY29yZS5waXBlbGluZSxyZWFkX3BjLmFjcCxzeXN0ZW0scmVhZF9wYy5kbWFfY3JzLG9wZW5pZCxzZXNzaW9uLEFkb2JlSUQsYWRkaXRpb25hbF9pbmZvLnJvbGVzLGFjcC5mb3VuZGF0aW9uLGFkZGl0aW9uYWxfaW5mby5wcm9qZWN0ZWRQcm9kdWN0Q29udGV4dCxyZWFkX3BjLmRtYV9hbmFseXRpY3MiLCJjcmVhdGVkX2F0IjoiMTUzNzgzMTMxNTIzMyJ9.gOhdsJp1AnAISv42yIJKyoalnhbSjWHR5l4ERImoV5NzKYGfu3nvSjl2RNTr2VSRATPMR8rsbPxM63QfZlFQBcvSvNssEtJx5d-Q6P87E-EdlM2SqMXm4WBALLlArsnGSp1Kqi71_ycSKk0lQwlqDdVuwQMJA-uuYaA-Ib9UJP-THK0SQIPTsr64D8iNP21YPSrK6wZ1xn0lkB6vyAyltSV7BFyqJIKKtoRhL086yz_GR7fWt6arsAV0CzKN9UeevbIcyEANSJeA9zYFQg8nLrnsYKyb0rR6KRQhKu-rDSdyYPcZnVS5fCG6e-RXO7SYSUdJ7dp5ig6Omo-GyQ0hQA");
             //conn.setRequestProperty("Authorization", "Bearer " + "eyJ4NXUiOiJ");
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
