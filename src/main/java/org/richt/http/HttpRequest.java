package org.richt.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage {
    private HttpMethod method;
    private String requestTarget;
    // Version found in request
    private String originalHttpVersion;
    // Version given
    private HttpVersion bestCompatibleVersion;
    private HashMap<String, String> headers = new HashMap<>();

    HttpRequest() {

    }

    public HttpMethod getMethod() {
        return method;
    }
    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleVersion;
    }
    public String getRequestTarget() {
        return requestTarget;
    }
    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method : HttpMethod.values()) {
            if (methodName.equals(method.name())) {
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }
    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()) throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL);
        this.requestTarget = requestTarget;
    }
    void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestCompatibleVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (bestCompatibleVersion == null) throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_VERSION_NOT_SUPPORTED);
    }
    void addHeader(String name, String field) { headers.put(name.toLowerCase(), field); }
    Set<String> getHeaderNames() { return headers.keySet(); }
    String getHeader(String name) {return headers.get(name).toLowerCase(); }
}
