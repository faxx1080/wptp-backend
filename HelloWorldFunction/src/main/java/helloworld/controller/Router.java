package helloworld.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import helloworld.controller.implementation.*;
import helloworld.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j

public class Router {

    public static Object handleRoute(APIGatewayV2HTTPEvent input) throws Exception {
        String method = input.getRequestContext().getHttp().getMethod();
        String url = input.getPathParameters().get("proxy");
        String requestBody = input.getBody();

        if ("GET".equals(method)) {
            //Returns name of user
            if(url.equals("api/name/(\\d+)")){
                Matcher matcher = generateMatcher(url, "^api/name/(\\d+)$");
                var userIds = matcher.group(1);
                int userId = Integer.parseInt(userIds.trim());

                return new GQuestionOnExam().handle(Map.of(
                        "examId", userId
                ));
            }
            //Returns ALL Questions
            else if (url.equals("api/questions/all")) {
                return new GAllQuestions().handle(null);
            }

            // Returns Specified Question
            // /api/exam/1/question/1,2,...
            else if (url.startsWith("api/exam/")) {
                Matcher matcher = generateMatcher(url, "^api/exam/(\\d+)/question/(\\d+)$");
                var examIdS = matcher.group(1);
                int examId = Integer.parseInt(examIdS.trim());
                var questionNumbers = matcher.group(2);
                int questionNumber = Integer.parseInt(questionNumbers.trim());

                return new GQuestionOnExam().handle(Map.of(
                        "examId", examId,
                        "questionNumber", questionNumber
                ));
            }
            //Returns some data about the examgit
            else if (url.startsWith("api/examattempts/")) {
                Matcher matcher = generateMatcher(url, "^api/examattempts/(\\d+)/(\\d+)$");
                var userIds = matcher.group(1);
                int userId = Integer.parseInt(userIds.trim());
                var examAttemptIds = matcher.group(2);
                int examAttemptId = Integer.parseInt(examAttemptIds.trim());

                return new ExamAttemptData().handle(Map.of(
                        "userId", userId,
                        "examAttemptId", examAttemptId
                ));
            }
        }
        else if ("POST".equals(method)) {
            if (url.startsWith("api/submit/")){
                //Ends Exam - sets done = true, edit timestamp of end_time
                if (url.endsWith("/end")) {
                    Matcher matcher = generateMatcher(url, "^api/submit/(\\d+)/(\\d+)/end$");
                    var userIds = matcher.group(1);
                    int userId = Integer.parseInt(userIds.trim());
                    var examAttemptIds = matcher.group(2);
                    int examAttemptId = Integer.parseInt(examAttemptIds.trim());

                    return new EndExam().handle(Map.of(
                            "userId", userId,
                            "examAttemptId", examAttemptId
                    ));
                }
                else {
                    Matcher matcher = generateMatcher(url, "^api/submit/(\\d+)/(\\d+)/question/(\\d+)$");

                    var userIds = matcher.group(1);
                    int userId = Integer.parseInt(userIds.trim());
                    var examAttempts = matcher.group(2);
                    int examAttempt = Integer.parseInt(examAttempts.trim());
                    var questionIds = matcher.group(3);
                    int questionId = Integer.parseInt(questionIds.trim());

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
            }
            //Starts Exam - sets done = false, edit timestamp of start_time
            else if (url.startsWith("api/exam/")){
                Matcher matcher = generateMatcher(url, "^api/exam/(\\d+)/(\\d+)/start$");
                var userIds = matcher.group(1);
                int userId = Integer.parseInt(userIds.trim());
                var examIds = matcher.group(2);
                int examId = Integer.parseInt(examIds.trim());

                return new StartExam().handle(Map.of(
                        "userId", userId,
                        "examId", examId
                ));
            }
        }
        return Util.createErrorResponse("Path Not Found", 404);
    }

    //Helper Functions
    private static Matcher generateMatcher(String fullURL, String endingURL){
        Pattern pattern = Pattern.compile(endingURL);
        Matcher matcher = pattern.matcher(fullURL);
        var found = matcher.find();
        if (!found) {
            throw new RuntimeException("Wrong path");
        }
        return matcher;
    }

}
