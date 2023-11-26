package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class GetQuestion implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Logger log = LoggerFactory.getLogger(GetQuestion.class);

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        try {
            return handleRequestInner(input, context);
        } catch (Exception e) {
            log.error("An error occurred: ", e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody("{\"error\": \"Internal Server Error\"}").build();
        }
    }

    public APIGatewayV2HTTPResponse handleRequestInner(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        Util.logEnv(input, context);

        if ("POST".equals(input.getRequestContext().getHttp().getMethod())) {
            // blah
        }

        String path = input.getPathParameters().get("proxy");

        if (path != null && path.contains("api/get/question")) {
            return getQuestion();
        }

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(404)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody("{\"error\": \"Path Not Found\"}").build();


    }

    private APIGatewayV2HTTPResponse getQuestion() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        log.info("Entering getQuestion");
        // Load the JDBC driver
        Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver").newInstance();

        // Retrieve the connection info from the secret
        final String URL = "dvexam-rwuser";

        // Populate the user property with the secret ARN to retrieve user and password from the secret
        Properties info = new Properties( );
        info.put( "user", "dvexam-rwuser" );
        JSONArray jsonArray = new JSONArray();

        try (Connection cxn = DriverManager.getConnection(URL, info);
             Statement st = cxn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM public.questions")) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);

                    // Add each column to the JSON object dynamically
                    jsonObject.put(columnName, columnValue);
                }

                jsonArray.put(jsonObject);
            }

            // Print or use the JSON array as needed
            log.info("{}", jsonArray);

            // Process the results, if needed
            // ...

        } catch (SQLException e) {
            log.error("SQL error occurred: ", e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody("{\"error\": \"Internal Server Error\"}").build();
        } catch (Exception e) {
            log.error("An unexpected error occurred: ", e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody("{\"error\": \"Internal Server Error\"}").build();
        }

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody(jsonArray.toString())
                .build();
    }
}