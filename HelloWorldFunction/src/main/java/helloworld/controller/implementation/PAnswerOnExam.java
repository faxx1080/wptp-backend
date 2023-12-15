package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static helloworld.util.Util.*;
@Slf4j

public class PAnswerOnExam implements IController {
    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        // Use a prepared statement to avoid SQL injection
        String postAnswerQuery = "INSERT INTO public.submission (user_id, examattempt_id, question_id, choice, timestamp) " +
                "VALUES (?, ?, ?, ?, current_timestamp) " +
                "RETURNING user_id, examattempt_id, question_id";

        try (Connection cxn = getDatabase();
             PreparedStatement preparedStatement = cxn.prepareStatement(postAnswerQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, (int) input.get("userId"));
            preparedStatement.setInt(2, (int) input.get("examAttempt"));
            preparedStatement.setInt(3, (int) input.get("questionId"));
            preparedStatement.setString(4, (String) input.get("choice"));

            // Execute the SQL query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result if needed
            Map<String, Integer> resultValues = new HashMap<>();
            if (resultSet.next()) {
                resultValues.put("user_id", resultSet.getInt("user_id"));
                resultValues.put("examattempt_id", resultSet.getInt("examattempt_id"));
                resultValues.put("question_id", resultSet.getInt("question_id"));
            }

            return resultValues;
            }
        }
}
