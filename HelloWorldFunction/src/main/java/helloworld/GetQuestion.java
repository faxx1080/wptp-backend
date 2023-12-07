package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import helloworld.controller.Router;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static helloworld.util.Util.*;

@Slf4j
public class GetQuestion implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

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


    private APIGatewayV2HTTPResponse handleOptionsRequest() {
        Map<String, String> headers = getCorsHeaders();
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(headers)
                .build();
    }

    public APIGatewayV2HTTPResponse handleRequestInner(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        logEnv(input, context);
        log.debug(input.getRequestContext().getHttp().getMethod());


        if ("OPTIONS".equals(input.getRequestContext().getHttp().getMethod())) {
            // Handle pre-flight OPTIONS request
            return handleOptionsRequest();
        } else if ("GET".equals(input.getRequestContext().getHttp().getMethod())) {
            return getQuestion(input, context);
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

    private APIGatewayV2HTTPResponse getQuestion(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        log.info("Entering getQuestion");

        JSONArray jsonArray;

        try {
            var output = Router.handleRoute(input);
            if (output instanceof APIGatewayV2HTTPResponse casted) {
                return casted;
            } else if (output instanceof JSONArray casted) {
                jsonArray = casted;
            } else {
                jsonArray = new JSONArray();
                jsonArray.put(output);
            }
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
                .withBody(jsonArray.toString()) // TODO: Fix data types
                .build();
    }

    private APIGatewayV2HTTPResponse postQuestion(Map<String, String> body) throws SQLException {
        log.info("Entering postQuestion");

        // TODO: Refactor into controller

        try (Connection cxn = getDatabase()) {
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

    }

}