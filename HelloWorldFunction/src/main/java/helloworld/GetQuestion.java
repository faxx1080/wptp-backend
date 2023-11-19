package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class GetQuestion implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger log = LoggerFactory.getLogger(GetQuestion.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            return handleRequestInner(input, context);
        } catch (Exception e) {
            log.error("An error occurred: ", e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody("{\"error\": \"Internal Server Error\"}");
        }
    }

    public APIGatewayProxyResponseEvent handleRequestInner(APIGatewayProxyRequestEvent input, Context context) throws Exception {
        // Load the JDBC driver
        Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver").newInstance();

        // Retrieve the connection info from the secret
        String URL = "jdbc:postgresql://your-database-endpoint:5432/your-database-name";
        String username = "your-username";
        String password = "your-password";

        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);

        log.debug("AAAA");

        // Establish the connection
        try (Connection cxn = DriverManager.getConnection(URL, info);
             Statement st = cxn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM public.a")) {

            List<Object> values = new ArrayList<>();
            while (rs.next()) {
                context.getLogger().log("Info: " + rs.getObject(1));
                values.add(rs.getObject(1));
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .withBody("{\"test\": 2}");
        }
    }
}
