package helloworld.controller.implementation;

import helloworld.controller.IController;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Map;

import static helloworld.util.Util.getDatabase;

@Slf4j
public class CAllQuestions implements IController {

    @Override
    public Object handle(Map<String, Object> input) throws Exception {
        var jsonArray = new JSONArray();
        try (Connection cxn = getDatabase();
             Statement st = cxn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM public.question")) {

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

            // Print or use the JSON array as needed
            log.info("{}", jsonArray);

            // Process the results, if needed
            // ...
            return jsonArray;

        }
    }
}
