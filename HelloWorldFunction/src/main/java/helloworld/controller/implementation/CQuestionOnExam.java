package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.*;
import java.util.Map;

import static helloworld.util.Util.*;
@Slf4j

public class CQuestionOnExam implements IController {
    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        // Use a prepared statement to avoid SQL injection
        String sql = "SELECT * FROM question q " +
                "JOIN exam_question exq ON question_id = q.id " +
                "WHERE order_number = ? AND exq.exam_id = ?";

        try (Connection cxn = getDatabase();
             PreparedStatement preparedStatement = cxn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, (int) input.get("questionNumber"));
            preparedStatement.setInt(2, (int) input.get("examId"));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Create a single JSONObject to store the row
                JSONObject jsonObject = new JSONObject();

                // Use only the first row, assuming there is at most one result
                if (rs.next()) {
                    // Iterate through columns and add them to the JSON object
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = rs.getObject(i);

                        // Add each column to the JSON object dynamically
                        jsonObject.put(columnName, columnValue);
                    }
                }

                // Log or use the JSON object as needed
                log.info("{}", jsonObject);

                // Process the results, if needed
                // ...

                return jsonObject;
            }

        }
    }
}
