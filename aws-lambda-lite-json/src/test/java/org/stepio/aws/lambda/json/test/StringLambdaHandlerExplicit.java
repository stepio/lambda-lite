package org.stepio.aws.lambda.json.test;

import org.stepio.aws.lambda.json.DefaultLambdaHandler;

public class StringLambdaHandlerExplicit extends DefaultLambdaHandler<String, String> {
    public StringLambdaHandlerExplicit() {
        super(String.class, String.class);
    }
}
