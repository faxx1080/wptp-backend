package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
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

        // Load the JDBC driver
        Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver").newInstance();

        // Retrieve the connection info from the secret
        String URL = "dvexam-rwuser";

        if ("POST".equals(input.getRequestContext().getHttp().getMethod())) {
            // blah
        }

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
            System.out.println(jsonArray.toString());


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
        // Additional logic after processing ResultSet
        log.debug("Received Request Event: {}", input);

        // Extract referer information
        String referer = input.getHeaders().get("referer");
//        log.debug("Received Referer: {}", referer);
//        log.debug("Referer ends with /api/get/question: {}", referer != null && referer.endsWith("/api/get/question"));

        // Check if the referer matches the expected value
        //public.questions
        if (referer != null && referer.endsWith("/api/get/question")) {
            return Util.responseBuilderOK(
                    Map.of("question", "What is the meaning of life?", "answer", "42"));
        } else { // Normal functionality
            // Create a JSON object with 4 choices
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody(jsonArray.toString())
                    .build();
            // Return the APIGatewayProxyResponseEvent with the JSON body
//            return Util.responseBuilderOK(
//                     Map.of(
//                             "question", "This is a question",
//                             "answerA", "Choice A",
//                             "answerB", "Choice B",
//                             "answerC", "Choice C",
//                             "answerD", "Choice D"
//                     ));
        }
    }
}