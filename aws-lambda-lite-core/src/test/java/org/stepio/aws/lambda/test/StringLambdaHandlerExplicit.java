package org.stepio.aws.lambda.test;

import org.stepio.aws.lambda.DefaultLambdaHandler;

public class StringLambdaHandlerExplicit extends DefaultLambdaHandler<String, String> {
    public StringLambdaHandlerExplicit() {
        super(String.class, String.class);
    }
}
