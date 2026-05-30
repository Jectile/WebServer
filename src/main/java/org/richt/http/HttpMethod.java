package org.richt.http;

public enum HttpMethod {
    GET, HEAD;
    public static final int MAX_LENGTH;

    // Get longest method size
    static {
        int tempMax = -1;
        for (HttpMethod method : values()) if (method.name().length() > tempMax) tempMax = method.name().length();
        MAX_LENGTH = tempMax;
    }
}
