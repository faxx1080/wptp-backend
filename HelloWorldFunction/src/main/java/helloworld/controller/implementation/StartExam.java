package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static helloworld.util.Util.*;
@Slf4j

public class StartExam implements IController {
    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        // Use a prepared statement to avoid SQL injection
        String postStartExam = "INSERT INTO public.exam_attempt(\n" +
                "\texam_id, user_id, time_start, done)\n" +
                "\tVALUES (?, ?, current_timestamp, false)\n" +
                "\tRETURNING id;";

        try (Connection cxn = getDatabase();
             PreparedStatement preparedStatement = cxn.prepareStatement(postStartExam)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, (int) input.get("userId"));
            preparedStatement.setInt(2, (int) input.get("examId"));

            // Execute the SQL query
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
