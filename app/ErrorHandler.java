import com.fasterxml.jackson.databind.node.ObjectNode;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.internalServerError;

@Singleton
class ErrorHandler implements HttpErrorHandler {

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        final ObjectNode json = Json.newObject();
        json.put("Error", message);
        return CompletableFuture.completedFuture(internalServerError(json));
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        final ObjectNode json = Json.newObject();
        json.put("Error", exception.getMessage());
        return CompletableFuture.completedFuture(internalServerError(json));
    }
}