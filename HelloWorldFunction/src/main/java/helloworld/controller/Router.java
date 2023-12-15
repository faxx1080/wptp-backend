package helloworld.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import helloworld.controller.implementation.CAllQuestions;
import helloworld.controller.implementation.CQuestionOnExam;
import helloworld.controller.implementation.PAnswerOnExam;
import helloworld.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static helloworld.util.Util.getDatabase;
@Slf4j

public class Router {

    public static Object handleRoute(APIGatewayV2HTTPEvent input) throws Exception {
        String method = input.getRequestContext().getHttp().getMethod();
        String url = input.getPathParameters().get("proxy");
        String requestBody = input.getBody();

        if ("GET".equals(method)) {
            if (url.equals("api/questions/all")) {
                return new CAllQuestions().handle(null);
            }

            // /api/exam/1/question/1,2,...
            if (url.startsWith("api/exam/")) {
                Pattern pattern = Pattern.compile("^api/exam/(\\d+)/question/(\\d+)$");
                Matcher matcher = pattern.matcher(url);
                var found = matcher.find();
                if (!found) {
                    throw new RuntimeException("Wrong path");
                }
                var examIdS = matcher.group(1);
                int examId = Integer.parseInt(examIdS.trim());
                var questionNumbers = matcher.group(2);
                int questionNumber = Integer.parseInt(questionNumbers.trim());

                return new CQuestionOnExam().handle(Map.of(
                        "examId", examId,
                        "questionNumber", questionNumber
                ));
            }
        } else if ("POST".equals(method)) {
            // TODO: Fix
            // Retrieve data from the HTTP request
            Pattern pattern = Pattern.compile("^api/submit/(\\d+)/(\\d+)/question/(\\d+)$");
            Matcher matcher = pattern.matcher(url);
            var found = matcher.find();
            if (!found) {
                throw new RuntimeException("Wrong path");
            }
            var userIds = matcher.group(1);
            int userId = Integer.parseInt(userIds.trim());
            var examAttempts = matcher.group(2);
            int examAttempt = Integer.parseInt(examAttempts.trim());
            var questionIds = matcher.group(3);
            int questionId = Integer.parseInt(questionIds.trim());


            //Map<String, String> body = parseJsonBody(requestBody);
            JSONObject body = new JSONObject(requestBody);
            log.info("body{}", body);
            String choice = body.getString("choice");
            log.info("body.get(choice){}", choice);

            return new PAnswerOnExam().handle(Map.of(
                    "userId", userId,
                    "examAttempt", examAttempt,
                    "questionId",questionId,
                    "choice", choice
            ));
        }
        return Util.createErrorResponse("Path Not Found", 404);
    }

    private static Map<String, String> parseJsonBody(String body) {
        // Remove curly braces and split the string by ","
        String[] keyValuePairs = body.replaceAll("[{}]", "").split(",");

        Map<String, String> resultMap = new HashMap<>();

        for (String pair : keyValuePairs) {
            // Split each key-value pair by "="
            String[] entry = pair.split("=");
            if (entry.length == 2) {
                // If the entry has a valid key-value pair, add it to the map
                resultMap.put(entry[0].trim(), entry[1].trim());
            } else {
                // Handle invalid entries or log a warning
                System.err.println("Invalid key-value pair: " + pair);
            }
        }

        return resultMap;
    }

}
