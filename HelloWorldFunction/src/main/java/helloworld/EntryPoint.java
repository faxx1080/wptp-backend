package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import helloworld.controller.Router;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

import static helloworld.util.Util.*;

@Slf4j
public class EntryPoint implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        try {
            return handleRequestInner(input, context);
        } catch (Exception e) {
            return createErrorResponse("An error occurred: ", e, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }

    public APIGatewayV2HTTPResponse handleRequestInner(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        logEnv(input, context);
        log.debug(input.getRequestContext().getHttp().getMethod());


        if ("OPTIONS".equals(input.getRequestContext().getHttp().getMethod())) {
            // Handle pre-flight OPTIONS request
            return handleOptionsRequest();
        } else if ("GET".equals(input.getRequestContext().getHttp().getMethod())) {
            return getRequest(input, context);
        } else if ("POST".equals(input.getRequestContext().getHttp().getMethod())) {
            // Retrieve data from the HTTP request
            if(input.getBody() == null){
                return (postRequest(input, null, context));
            }
            Map<String, String> body = parseJsonBody(input.getBody());

            // Call the postQuestion method with the extracted values
            return postRequest(input, body, context);
        }
        return createErrorResponse("An error occurred: ", null, 404, "{\"error\": \"Internal Server Error\"}");
    }

    private APIGatewayV2HTTPResponse getRequest(APIGatewayV2HTTPEvent input, Context context) throws Exception {
        log.info("Entering getRequest");

        return Request(input, context, null);
    }

    private APIGatewayV2HTTPResponse postRequest(APIGatewayV2HTTPEvent input, Map<String, String> body, Context context) throws Exception {
        log.info("Entering postRequest");

        return Request(input,context,body);
    }

    private APIGatewayV2HTTPResponse Request(APIGatewayV2HTTPEvent input, Context context, Map<String, String> body) throws Exception {
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
            return createErrorResponse("SQL error occurred: ", e, 500, "{\"error\": \"Internal Server Error\"}");
        } catch (Exception e) {
            return createErrorResponse("An error occurred: ", e, 500, "{\"error\": \"Internal Server Error\"}");
        }

        return createSuccessResponse(200, jsonArray.toString());
    }

    //Helper functions
    private Map<String, String> parseJsonBody(String jsonBody) {
        JSONObject jsonObject = new JSONObject(jsonBody);
        Map<String, String> resultMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            resultMap.put(key, jsonObject.getString(key));
        }

        return resultMap;
    }

    private APIGatewayV2HTTPResponse createSuccessResponse(int status_code, String successMessage) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(status_code)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody(successMessage) // TODO: Fix data types
                .build();
    }

    private APIGatewayV2HTTPResponse createErrorResponse(String exceptionMessage, Exception e, int status_code, String errorMessage) {
        if (e != null)
            log.error(exceptionMessage, e);
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(status_code)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody(errorMessage).build();
    }

    private APIGatewayV2HTTPResponse handleOptionsRequest() {
        Map<String, String> headers = getCorsHeaders();
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(headers)
                .build();
    }
}