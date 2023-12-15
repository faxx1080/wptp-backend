package helloworld.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import helloworld.controller.implementation.CAllQuestions;
import helloworld.controller.implementation.CQuestionOnExam;
import helloworld.util.Util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    public static Object handleRoute(APIGatewayV2HTTPEvent input) throws Exception {
        String method = input.getRequestContext().getHttp().getMethod();
        String url = input.getPathParameters().get("proxy");
        if ("GET".equals(method)) {
            if (url.equals("api/questions/all")) {
                return new CAllQuestions().handle(null);
            }

            // /api/exam/1/question/1,2,
            if (url.startsWith("api/exam/")) {
                Pattern pattern = Pattern.compile("^api/exam/(\\d+)/question/(\\d+)$");
                Matcher matcher = pattern.matcher(url);
                var found = matcher.find();
                if (!found) {
                    throw new RuntimeException("Wrong path");
                }
                var examIdS = matcher.group(1);
                int examId = Integer.parseInt(examIdS.trim());
                var questionIdS = matcher.group(2);
                int questionId = Integer.parseInt(questionIdS.trim());

                return new CQuestionOnExam().handle(Map.of(
                        "examId", examId,
                        "questionId", questionId
                ));
            }
        } else if ("POST".equals(method)) {
            // TODO: Fix
            // Retrieve data from the HTTP request
            Map<String, String> body = parseJsonBody(input.getBody());

            // Call the postQuestion method with the extracted values
            return postQuestion(body);
        }
        return Util.createErrorResponse("Path Not Found", 404);
    }

    private static Object postQuestion(Map<String, String> body) {
        // TODO: fix, refactor from GetQuestion
        return null;
    }

    private static Map<String, String> parseJsonBody(String body) {
        // TODO: fix, refactor from GetQuestion
        return null;
    }
}
