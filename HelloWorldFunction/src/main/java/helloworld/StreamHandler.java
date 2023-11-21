package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
public class StreamHandler implements RequestStreamHandler {
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));
        StringBuilder in = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                in.append(line);
            }
            log.info("Input: {}", in);
        } catch (IOException e) {
            log.error("Error", e);
        } finally {
            reader.close();
            writer.write(new Gson().toJson(
                    new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                            .withBody("{\"ok\": true}")));
            String finalString = writer.toString();
            log.info("Final string result: {}", finalString);
            writer.close();
        }
    }
}
