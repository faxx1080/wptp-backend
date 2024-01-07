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
        JSONArray breakdownArray = new JSONArray();
        int questionCount = 0;
        int answersSubmitted = 0;
        int correctAnswers = 0;
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
                    questionCount++;

                    JSONObject breakdownObject = new JSONObject();

                    // Check for null before calling isEmpty()
                    String choice = rs.getString("choice");
                    boolean isSubmitted = choice != null && !choice.isEmpty();

                    boolean isCorrect = false;
                    if (isSubmitted) {
                        isCorrect = choice.equalsIgnoreCase(rs.getString("correctanswerchoice"));
                    }

                    if (isSubmitted) {
                        answersSubmitted++;
                    }
                    if (isCorrect) {
                        correctAnswers++;
                    }

                    breakdownObject.put("question_id", rs.getInt("question_id"));
                    breakdownObject.put("tag", rs.getString("name"));
                    breakdownObject.put("is_submitted", isSubmitted);
                    breakdownObject.put("is_correct", isCorrect);
                    breakdownObject.put("submitted_answer", rs.getString("choice"));
                    breakdownObject.put("correct_answer", rs.getString("correctanswerchoice"));
                    breakdownObject.put("explanation", rs.getString("answerexplanation"));

                    breakdownArray.put(breakdownObject);
                }

            }
        }
            JSONObject result = new JSONObject();
            result.put("question_count", questionCount);
            result.put("answers_submitted", answersSubmitted);
            result.put("correct_answers", correctAnswers);
            result.put("breakdown", breakdownArray);

            return result;
    }
}
