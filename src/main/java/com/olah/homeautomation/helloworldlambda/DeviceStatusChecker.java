package com.olah.homeautomation.helloworldlambda;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceStatusChecker {

    static {
        final InputStream inputStream = DeviceStatusChecker.class.getResourceAsStream("/logging.properties");
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            java.util.logging.Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            java.util.logging.Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }

    static final Logger LOGGER = LogManager.getLogger(DeviceStatusChecker.class);

    private static final String ENV_VARIABLE_NAME_TOKEN = "token";
    private static final String ENV_VARIABLE_NAME_DEVICEID = "device_id";
    private static final String DEVICE_ID_KEY = "DEVICE_ID";
    private static final String CONTENT_STRING = "{\"method\":\"passthrough\", \"params\": {\"deviceId\": \"" + DEVICE_ID_KEY + "\", \"requestData\": \"{\\\"system\\\":{\\\"get_sysinfo\\\":null},\\\"emeter\\\":{\\\"get_realtime\\\":null}}\" }}";

    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();


    public String getDeviceStatus(String token, String deviceId) throws IOException {
        HttpRequest request = createRequest(token, deviceId);
        LOGGER.info("Sending request ");
        HttpResponse httpResponse = request.execute();
        LOGGER.info("Request sent");
        String response = parseResonse(httpResponse);
        LOGGER.info("Received response:" + response);

        return response;
    }

    private String parseResonse(HttpResponse httpResponse) throws IOException {
        try {
            return httpResponse.parseAsString();
        } catch (IOException io) {
            throw new DeviceStatusCheckFailed(io);
        }
    }

    private HttpRequest createRequest(String token, String deviceId) {
        HttpRequestFactory requestFactory
                = HTTP_TRANSPORT.createRequestFactory(
                (HttpRequest request) -> {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                });


        DeviceStatusUrl url = new DeviceStatusUrl("https://wap.tplinkcloud.com");
        url.token = token;
        HttpContent content = ByteArrayContent.fromString("application/json", CONTENT_STRING.replace(DEVICE_ID_KEY, deviceId));
        try {
            return requestFactory.buildPostRequest(url, content);
        } catch (IOException io) {
            throw new DeviceStatusCheckFailed(io);
        }
    }


    public boolean getRelayStatus(String token, String deviceId) throws IOException {
        String deviceStatus = getDeviceStatus(token, deviceId);

        if(!deviceStatus.contains("\"error_code\":0")){
            throw new DeviceStatusCheckFailed(deviceStatus);
        }

        Pattern pattern = Pattern.compile("\\\\\"relay_state\\\\\":(\\d)");
        Matcher matcher = pattern.matcher(deviceStatus);

        matcher.find();

        return matcher.group(1) == "0" ? false : true;
    }


    public boolean getRelayStatusUsingEnvVariables() throws IOException {
        String token = System.getenv(ENV_VARIABLE_NAME_TOKEN);
        String deviceId = System.getenv(ENV_VARIABLE_NAME_DEVICEID);

        if(token == null ||deviceId == null) {
            LOGGER.error("either token or deviceid env variable is null");
        }

        LOGGER.info("Sending a request using environment variables token:{}, deviceId:{}", token, deviceId);

        return getRelayStatus(token, deviceId);
    }
}
