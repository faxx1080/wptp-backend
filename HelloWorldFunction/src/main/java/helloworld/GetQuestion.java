package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        try (Connection cxn = DriverManager.getConnection(URL, info);
             Statement st = cxn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM public.a")) {

            List<Object> values = new ArrayList<>();
            while (rs.next()) {
                context.getLogger().log("Info: " + rs.getObject(1));
                values.add(rs.getObject(1));
            }

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
        log.debug("Received Referer: {}", referer);
        log.debug("Referer ends with /api/get/question: {}", referer != null && referer.endsWith("/api/get/question"));

        // Check if the referer matches the expected value
        if (referer != null && referer.endsWith("/api/get/question")) {
            return Util.responseBuilderOK(
                    Map.of("question", "What is the meaning of life?", "answer", "42"));
        } else { // Normal functionality
            // Create a JSON object with 4 choices

            // Return the APIGatewayProxyResponseEvent with the JSON body
            return Util.responseBuilderOK(
                     Map.of(
                             "question", "This is a question",
                             "answerA", "Choice A",
                             "answerB", "Choice B",
                             "answerC", "Choice C",
                             "answerD", "Choice D"
                     ));
        }
    }
}