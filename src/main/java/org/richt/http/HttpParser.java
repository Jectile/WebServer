package org.richt.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10

    public HttpRequest parseHttpRequest(InputStream input) throws RuntimeException, HttpParsingException {
        InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();
        try {
            parseRequestLine(inputReader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            parseRequestHeaders(inputReader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseBody(inputReader, request);
        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        boolean methodParsed = false;
        boolean requestTargetParsed = false;
        StringBuilder dataBuffer = new StringBuilder();
        int _byte;
        while ( (_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    LOGGER.debug("Request Line VERSION to process : {}", dataBuffer.toString());
                    if (!methodParsed || !requestTargetParsed) throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    try {
                        request.setHttpVersion(dataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    return;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            if (_byte == SP) {
                if (!methodParsed) {
                    LOGGER.debug("Request Line METHOD to process : {}", dataBuffer.toString());
                    request.setMethod(dataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request line REQ TARGET to process : {}", dataBuffer.toString());
                    request.setRequestTarget(dataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                dataBuffer.delete(0, dataBuffer.length());
            } else {
                dataBuffer.append((char) _byte);
                if (!methodParsed) {
                    if (dataBuffer.length() > HttpMethod.MAX_LENGTH) throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                }
            }
        }
    }

    private void parseRequestHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder dataBuffer = new StringBuilder();
        boolean crlfFound = false;
        int _byte;
        while ( (_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if (!crlfFound) {
                        crlfFound = true;
                        processSingleHeaderField(dataBuffer, request);
                        // clear the buffer
                        dataBuffer.delete(0, dataBuffer.length());
                    } else {
                        // Two crlf found
                        return;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else {
                crlfFound = false;
                dataBuffer.append((char)_byte);
            }
        }
    }

    private void processSingleHeaderField(StringBuilder builder, HttpRequest request) throws HttpParsingException {
        Pattern HEADER_PATTERN = Pattern.compile(
                "^(?<fieldName>[!#$%&'*+.^_`|~0-9A-Za-z-]+):[ \\t]*(?<fieldValue>[\\x21-\\x7E]+(?:[ \\t]+[\\x21-\\x7E]+)*)?[ \\t]*$"
        );
        Matcher matcher = HEADER_PATTERN.matcher(builder.toString());
        if (matcher.matches()) {
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName, fieldValue);
        } else throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }

    private void parseBody(InputStreamReader reader, HttpRequest request) {
    }




}
