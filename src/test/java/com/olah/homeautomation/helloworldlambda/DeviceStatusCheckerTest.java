package com.olah.homeautomation.helloworldlambda;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;


class DeviceStatusCheckerTest {

    private static String TOKEN;
    private static String DEVICE_ID;

    @BeforeAll
    private static void readTokenAndDeviceID() throws IOException {
        // create and load default properties
        Properties defaultProps = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("credentials.properties");
        defaultProps.load(in);
        in.close();

        Properties credentials = new Properties(defaultProps);
        TOKEN = credentials.getProperty("token");
        DEVICE_ID = credentials.getProperty("deviceId");
    }


    @org.junit.jupiter.api.Test
    void getDeviceStatusJson() throws IOException {
        DeviceStatusChecker deviceStatusChecker = new DeviceStatusChecker();
        String deviceStatus = deviceStatusChecker.getDeviceStatus(TOKEN,
                DEVICE_ID);
    }


    @org.junit.jupiter.api.Test
    void getRelayStatus() throws IOException {
        DeviceStatusChecker deviceStatusChecker = new DeviceStatusChecker();
        boolean status = deviceStatusChecker.getRelayStatus(TOKEN,
                DEVICE_ID);
    }



    @org.junit.jupiter.api.Test()
    void getRelayStatus_wrong_deviceID() throws IOException {
        DeviceStatusChecker deviceStatusChecker = new DeviceStatusChecker();
        assertThrows(
        DeviceStatusCheckFailed.class, () -> deviceStatusChecker.getRelayStatus(TOKEN,
                "wrong_device_id"));
    }

    @org.junit.jupiter.api.Test
    void getRelayStatus_wrong_deviceID2() throws IOException {
        DeviceStatusChecker deviceStatusChecker = new DeviceStatusChecker();
        assertThrows(
                DeviceStatusCheckFailed.class, ()->deviceStatusChecker.getRelayStatus("wrong_token",
                        DEVICE_ID));
    }
}