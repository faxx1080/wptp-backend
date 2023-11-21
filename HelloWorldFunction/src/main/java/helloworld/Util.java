package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class Util {
    public static void logEnv(Object input, Context context) {
        try {
            log.debug("Input: {}", new Gson().toJson(input));
        } catch (RuntimeException ignored) {
            log.debug("Input: {}", input);
        }
    }

    public static APIGatewayV2HTTPResponse responseBuilderOK(Map input) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody(new Gson().toJson(input)).build();
    }
}
