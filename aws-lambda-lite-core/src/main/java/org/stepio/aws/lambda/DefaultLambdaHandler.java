package org.stepio.aws.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.stepio.aws.lambda.util.Assert.message;
import static org.stepio.aws.lambda.util.Assert.notNull;
import static org.stepio.aws.lambda.util.Responses.noContent;
import static org.stepio.aws.lambda.util.Responses.ok;
import static org.stepio.aws.lambda.util.StringUtils.isEmpty;

public class DefaultLambdaHandler<B, R> extends AbstractLambdaHandler {

    protected Class<B> bodyClass;
    protected Class<R> responseClass;

    public DefaultLambdaHandler() {
        Type[] array = genericTypes();
        if (array != null) {
            this.bodyClass = (Class<B>) array[0];
            this.responseClass = (Class<R>) array[1];;
        }
    }

    public DefaultLambdaHandler(Class<B> bodyClass, Class<R> responseClass) {
        this.bodyClass = bodyClass;
        this.responseClass = responseClass;
    }

    @Override
    protected AwsProxyResponse doGet(AwsProxyRequest request, Context context) {
        return wrap(
                doGetImpl(request, context)
        );
    }

    @Override
    protected AwsProxyResponse doPost(AwsProxyRequest request, Context context) {
        return wrap(
                doPostImpl(
                        body(request), request, context
                )
        );
    }

    @Override
    protected AwsProxyResponse doPut(AwsProxyRequest request, Context context) {
        return wrap(
                doPutImpl(
                        body(request), request, context
                )
        );
    }

    @Override
    protected AwsProxyResponse doDelete(AwsProxyRequest request, Context context) {
        return wrap(
                doDeleteImpl(
                        body(request), request, context
                )
        );
    }

    protected R doGetImpl(AwsProxyRequest request, Context context) {
        throw new UnsupportedOperationException("GET is not supported");
    }

    protected R doPostImpl(B body, AwsProxyRequest request, Context context) {
        throw new UnsupportedOperationException("POST is not supported");
    }

    protected R doPutImpl(B body, AwsProxyRequest request, Context context) {
        throw new UnsupportedOperationException("PUT is not supported");
    }

    protected R doDeleteImpl(B body, AwsProxyRequest request, Context context) {
        throw new UnsupportedOperationException("DELETE is not supported");
    }

    protected B body(AwsProxyRequest request) {
        String bodyText = request.getBody();
        if (isEmpty(bodyText)) {
            return null;
        }
        notNull(this.bodyClass, "Request class is mandatory");
        if (String.class == this.bodyClass) {
            return (B) bodyText;
        }
        ObjectReader reader = reader(this.bodyClass);
        try {
            return reader.readValue(bodyText);
        } catch (IOException ex) {
            throw new IllegalArgumentException(message("Failed to extract %s from request body %s", this.bodyClass, bodyText), ex);
        }
    }

    protected AwsProxyResponse wrap(R response) {
        if (isEmpty(response)) {
            return noContent();
        }
        notNull(this.responseClass, "Response class is mandatory");
        if (String.class == this.responseClass) {
            return ok((String) response);
        }
        ObjectWriter writer = writer(this.responseClass);
        try {
            String responseText = writer.writeValueAsString(response);
            return ok(responseText);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(message("Failed to serialize %s", response), ex);
        }
    }

    protected Type[] genericTypes() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        Type[] array = ((ParameterizedType) type).getActualTypeArguments();
        if (array == null || array.length < 2) {
            return null;
        }
        return array;
    }
}
