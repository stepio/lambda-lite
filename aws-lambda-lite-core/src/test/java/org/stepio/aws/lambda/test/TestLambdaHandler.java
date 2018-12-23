package org.stepio.aws.lambda.test;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import org.stepio.aws.lambda.DefaultLambdaHandler;

import java.util.Objects;

import static org.stepio.aws.lambda.util.Assert.state;

public class TestLambdaHandler extends DefaultLambdaHandler<ABody, AResult> {

    @Override
    protected AResult doGetImpl(AwsProxyRequest request, Context context) {
        AResult result = new AResult();
        result.setValue("DUMMY#1");
        return result;
    }

    @Override
    protected AResult doPostImpl(ABody body, AwsProxyRequest request, Context context) {
        state(Objects.equals("DUMMY#2", body.getName()), "Unexpected request %s", body.getName());
        AResult result = new AResult();
        result.setValue("DUMMY#2");
        return result;
    }

    @Override
    protected AResult doPutImpl(ABody body, AwsProxyRequest request, Context context) {
        state(Objects.equals("DUMMY#3", body.getName()), "Unexpected request %s", body.getName());
        AResult result = new AResult();
        result.setValue("DUMMY#3");
        return result;
    }

    @Override
    protected AResult doDeleteImpl(ABody body, AwsProxyRequest request, Context context) {
        state(Objects.equals("DUMMY#4", body.getName()), "Unexpected request %s", body.getName());
        AResult result = new AResult();
        result.setValue("DUMMY#4");
        return result;
    }
}
