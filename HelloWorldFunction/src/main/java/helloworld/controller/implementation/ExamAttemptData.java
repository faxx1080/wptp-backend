package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Map;

import static helloworld.util.Util.*;
@Slf4j

public class ExamAttemptData implements IController {
    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        var jsonArray = new JSONArray();
        // Use a prepared statement to avoid SQL injection
        String sql = "SELECT\n" +
                "    q.question_id,\n" +
                "    t.name,\n" +
                "    s.choice,\n" +
                "    qu.correctanswerchoice,\n" +
                "    qu.answerexplanation\n" +
                "FROM\n" +
                "    exam_question q\n" +
                "JOIN\n" +
                "    question_tag qt ON q.question_id = qt.question_id\n" +
                "JOIN\n" +
                "    tag t ON qt.tag_id = t.id\n" +
                "LEFT JOIN\n" +
                "    submission s ON q.question_id = s.question_id\n" +
                "        AND q.exam_id = s.examattempt_id\n" +
                "LEFT JOIN\n" +
                "    question qu ON q.question_id = qu.id\n" +
                "WHERE\n" +
                "    q.exam_id = (SELECT exam_id FROM exam_attempt WHERE id = ?);\n";

        try (Connection cxn = getDatabase();
             PreparedStatement preparedStatement = cxn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, (int) input.get("examAttemptId"));

            try (ResultSet rs = preparedStatement.executeQuery()) {
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
                // Process the results, if needed
                // ...

                return jsonArray;
            }

        }
    }
}
