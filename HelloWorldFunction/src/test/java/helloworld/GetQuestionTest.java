package helloworld;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import org.junit.jupiter.api.Disabled;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import util.TestContext;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Slf4j
class GetQuestionTest {

    @Test
    void handleRequest() {
        APIGatewayV2HTTPEvent httpEvent =
                EventLoader.loadApiGatewayHttpEvent("event-test.json");

        var getQuestion = new GetQuestion();
        var output = getQuestion.handleRequest(httpEvent, new TestContext());
        log.info("Output: {}", output);
    }
}