package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map, Map> {

    Logger log = LoggerFactory.getLogger(App.class);

    @SuppressWarnings("unchecked")
    public Map handleRequest(final Map input, final Context context) {
        try {
            return handleRequestInner(input, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map handleRequestInner(final Map input, final Context context) throws Exception {
        // Load the JDBC driver
        Class.forName( "com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver" ).newInstance();

        // Retrieve the connection info from the secret
        String URL = "dvexam-rwuser";

        // Populate the user property with the secret ARN to retrieve user and password from the secret
        Properties info = new Properties( );
        info.put( "user", "dvexam-rwuser" );

        log.debug("AAAA");

        // Establish the connection
        Connection cxn = DriverManager.getConnection(URL, info);

        var st = cxn.createStatement();
        var rs = st.executeQuery("SELECT * from public.a");
        List<Object> values = new ArrayList<>();
        while (rs.next()) {
            context.getLogger().log("Info: " + rs.getObject(1));
            values.add(rs.getObject(1));
        }

        return Map.of(
                "values", values
        );
    }
}
