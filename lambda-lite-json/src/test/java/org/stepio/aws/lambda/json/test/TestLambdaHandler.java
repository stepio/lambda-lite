package org.stepio.aws.lambda.json.test;

import org.stepio.aws.lambda.enums.Method;
import org.stepio.aws.lambda.json.DefaultLambdaHandler;

import java.util.Objects;

import static org.stepio.aws.lambda.util.Assert.state;

public class TestLambdaHandler extends DefaultLambdaHandler<ABody, AResult> {

    public TestLambdaHandler() {
        super(ABody.class, AResult.class);

        registerCustom(Method.GET, requestContext -> {
            AResult result = new AResult();
            result.setValue("DUMMY#1");
            return result;
        });

        registerCustom(Method.POST, requestContext -> {
            state(Objects.equals("DUMMY#2", requestContext.getBody().getName()),
                    "Unexpected request %s", requestContext.getBody().getName());
            AResult result = new AResult();
            result.setValue("DUMMY#2");
            return result;
        });

        registerCustom(Method.PUT, requestContext -> {
            state(Objects.equals("DUMMY#3", requestContext.getBody().getName()),
                    "Unexpected request %s", requestContext.getBody().getName());
            AResult result = new AResult();
            result.setValue("DUMMY#3");
            return result;
        });

        registerCustom(Method.DELETE, requestContext -> {
            state(Objects.equals("DUMMY#4", requestContext.getBody().getName()),
                    "Unexpected request %s", requestContext.getBody().getName());
            AResult result = new AResult();
            result.setValue("DUMMY#4");
            return result;
        });
    }
}
