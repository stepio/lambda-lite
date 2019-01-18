package io.github.stepio.lambda.json.test;

import io.github.stepio.lambda.json.DefaultLambdaHandler;

public class StringLambdaHandlerExplicit extends DefaultLambdaHandler<String, String> {
    public StringLambdaHandlerExplicit() {
        super(String.class, String.class);
    }
}
