package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static helloworld.util.Util.*;
@Slf4j

public class EndExam implements IController {
    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        // Use a prepared statement to avoid SQL injection
        String postEndExam = "UPDATE public.exam_attempt\n" +
                "\tSET time_end = current_timestamp, done = true\n" +
                "\tWHERE id = ?;";

        try (Connection cxn = getDatabase();
             PreparedStatement preparedStatement = cxn.prepareStatement(postEndExam)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, (int) input.get("examAttemptId"));

            // Update done variable
            preparedStatement.executeUpdate();

            return null;
        }
    }
}
