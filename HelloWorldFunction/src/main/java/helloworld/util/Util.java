package helloworld.util;

import com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class Util {

    private static final String URL = "dvexam-rwuser";
    private static final String DB_USER = "dvexam-rwuser";
    private static final Properties dbInfo = new Properties();
    @Getter
    private static final Map<String, String> corsHeaders = new HashMap<>();
    private static Connection cxn;

    static {
        log.info("Load DB class");
        try {
            Class.forName( "com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver")
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        dbInfo.put( "user", DB_USER );

        corsHeaders.put("Access-Control-Allow-Origin", "https://main.d3t0eddq8sk7xv.amplifyapp.com"); // Allow requests from site
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type");
        corsHeaders.put("Access-Control-Expose-Headers", "*");
        corsHeaders.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET"); // Add other HTTP methods if needed
    }
    public static Connection getDatabase() throws SQLException {
        if (cxn == null || !cxn.isValid(1))
            cxn = DriverManager.getConnection(URL, dbInfo);
        return cxn;
    }

    public static APIGatewayV2HTTPResponse createErrorResponse(String errorMessage) {
        return createErrorResponse(errorMessage, 500);
    }

    public static APIGatewayV2HTTPResponse createErrorResponse(String errorMessage, int errorCode) {
        JSONObject responseBody = new JSONObject();
        responseBody.put("error", errorMessage);

        Map<String, String> headers = new HashMap<>(getCorsHeaders());

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(errorCode)
                .withHeaders(headers)
                .withBody(responseBody.toString())
                .build();
    }

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
