package util;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.extern.slf4j.Slf4j;

public class TestContext implements Context {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestContext.class);
    @Override
    public String getAwsRequestId() {
        return "";
    }

    @Override
    public String getLogGroupName() {
        return "";
    }

    @Override
    public String getLogStreamName() {
        return "";
    }

    @Override
    public String getFunctionName() {
        return "";
    }

    @Override
    public String getFunctionVersion() {
        return "";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 0;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 0;
    }

    @Override
    public LambdaLogger getLogger() {
        return new LambdaLogger() {
            @Override
            public void log(String message) {
                logger.debug("{}", message);
            }

            @Override
            public void log(byte[] message) {
                // TODO: fix this to something sensible
                logger.debug("{}", message);
            }
        };
    }
}
