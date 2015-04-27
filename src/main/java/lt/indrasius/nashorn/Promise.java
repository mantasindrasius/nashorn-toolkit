package lt.indrasius.nashorn;

import jdk.nashorn.api.scripting.JSObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by mantas on 15.4.26.
 */
public class Promise {
    public static <R> Future<R> toFuture(Object promiseObject) {
        JSObject promiseObj = (JSObject) promiseObject;

        CompletableFuture<R> f = new CompletableFuture<>();
        JSObject cb = (JSObject) promiseObj.eval(
                "function(promise, future) {" +
                        "promise.then(function(result) {" +
                        "   future.complete(result);" +
                        "});" +
                        "}");

        cb.call(promiseObj, promiseObj, f);

        return f;
    }
}
