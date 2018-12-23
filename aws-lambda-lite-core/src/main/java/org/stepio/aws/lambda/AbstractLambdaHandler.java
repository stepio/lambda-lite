package org.stepio.aws.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static org.stepio.aws.lambda.util.Responses.badRequest;
import static org.stepio.aws.lambda.util.Responses.internalServerError;
import static org.stepio.aws.lambda.util.Responses.methodNotAllowed;
import static org.stepio.aws.lambda.util.StringUtils.isEmpty;

public abstract class AbstractLambdaHandler implements RequestStreamHandler {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected ObjectMapper objectMapper;
    protected ConcurrentMap<Class<?>, ObjectReader> readers;
    protected ConcurrentMap<Class<?>, ObjectWriter> writers;

    protected AbstractLambdaHandler() {
        this.objectMapper = new ObjectMapper();
        this.readers = new ConcurrentHashMap<>();
        this.writers = new ConcurrentHashMap<>();
    }

    protected ObjectReader reader(Class<?> type) {
        return this.readers.computeIfAbsent(type, this.objectMapper::readerFor);
    }

    protected ObjectWriter writer(Class<?> type) {
        return this.writers.computeIfAbsent(type, this.objectMapper::writerFor);
    }

    protected abstract AwsProxyResponse doDelete(AwsProxyRequest request, Context context);

    protected abstract AwsProxyResponse doGet(AwsProxyRequest request, Context context);

    protected abstract AwsProxyResponse doPost(AwsProxyRequest request, Context context);

    protected abstract AwsProxyResponse doPut(AwsProxyRequest request, Context context);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        AwsProxyRequest request = reader(AwsProxyRequest.class).readValue(inputStream);
        AwsProxyResponse response = handleRequest(request, context);
        writer(AwsProxyResponse.class).writeValue(outputStream, response);
    }

    protected AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        try {
            if (isEmpty(request.getHttpMethod())) {
                LOG.warn("HTTP method is not recognized");
                return methodNotAllowed();
            }
            switch (request.getHttpMethod().toUpperCase()) {
                case DELETE:
                    return doDelete(request, context);
                case GET:
                    return doGet(request, context);
                case POST:
                    return doPost(request, context);
                case PUT:
                    return doPut(request, context);
                default:
                    LOG.warn("No specific handler for {} request", request.getHttpMethod());
                    return methodNotAllowed();
            }
        } catch (IllegalArgumentException ex) {
            LOG.error("Failed to process an AWS request as it's incorrect", ex);
            return badRequest();
        } catch (UnsupportedOperationException ex) {
            LOG.error("Failed to process an AWS request as it's handler is not implemented", ex);
            return methodNotAllowed();
        } catch (RuntimeException ex) {
            LOG.error("Failed to process an AWS request due to unexpected error", ex);
            return internalServerError();
        }
    }
}
