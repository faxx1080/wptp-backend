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

    private Map<String, String> parseJsonBody(String jsonBody) {
        JSONObject jsonObject = new JSONObject(jsonBody);
        Map<String, String> resultMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            resultMap.put(key, jsonObject.getString(key));
        }

        return resultMap;
    }


    private APIGatewayV2HTTPResponse createSuccessResponse(String message) {
        JSONObject responseBody = new JSONObject();
        responseBody.put("message", message);

        Map<String, String> headers = getCorsHeaders();

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(headers)
                .withBody(responseBody.toString())
                .build();
    }

    private APIGatewayV2HTTPResponse createErrorResponse(String errorMessage) {
        JSONObject responseBody = new JSONObject();
        responseBody.put("error", errorMessage);

        Map<String, String> headers = getCorsHeaders();

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(500)
                .withHeaders(headers)
                .withBody(responseBody.toString())
                .build();
    }

    private Map<String, String> getCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET"); // Add other HTTP methods if needed
        return headers;
    }


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

    private APIGatewayV2HTTPResponse handleOptionsRequest() {
        Map<String, String> headers = getCorsHeaders();
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(headers)
                .build();
    }

    public APIGatewayV2HTTPResponse handleRequestInner(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        Util.logEnv(input, context);
        log.debug(input.getRequestContext().getHttp().getMethod());


        if ("OPTIONS".equals(input.getRequestContext().getHttp().getMethod())) {
            // Handle pre-flight OPTIONS request
            return handleOptionsRequest();
        } else if ("GET".equals(input.getRequestContext().getHttp().getMethod())) {
            return getQuestion();
        } else if ("POST".equals(input.getRequestContext().getHttp().getMethod())) {
            // Retrieve data from the HTTP request
            Map<String, String> body = parseJsonBody(input.getBody());

            // Call the postQuestion method with the extracted values
            return postQuestion(body);
        }

//        if (path != null && path.contains("api/get/question")) {
//            return getQuestion();
//        }

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
             ResultSet rs = st.executeQuery("SELECT * FROM public.question")) {

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

    private APIGatewayV2HTTPResponse postQuestion(Map<String, String> body) {
        log.info("Entering postQuestion");

        try {
            // Load the JDBC driver
            Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver").newInstance();

            // Retrieve the connection info from the secret
            final String URL = "dvexam-rwuser";
            Properties info = new Properties();
            info.put("user", "dvexam-rwuser");

            try (Connection cxn = DriverManager.getConnection(URL, info)) {
                // Create a PreparedStatement to insert a new question
                String insertQuery = "INSERT INTO public.question ("
                        + "id, correctanswerchoice, difficulty, questiontext, "
                        + "choiceatext, choicebtext, choicectext, choicedtext, choiceetext, "
                        + "questiontype, section, answerexplanation, "
                        + "categoriesalgebra, categoriesgeometry, imagelink, "
                        + "equations, correctanswertext, imagesolutionlink"
                        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pst = cxn.prepareStatement(insertQuery)) {
                    // Set parameters for the PreparedStatement using values from the Map
                    int i = 1;
                    for (String key : body.keySet()) {
                        pst.setString(i++, body.get(key));
                    }

                    // Execute the update
                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        log.info("Question added successfully");
                        return createSuccessResponse("Question added successfully");
                    } else {
                        log.error("Failed to add question");
                        return createErrorResponse("Failed to add question");
                    }
                }
            }

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            log.error("An unexpected error occurred: ", e);
            return createErrorResponse("Internal Server Error");
        }
    }
}