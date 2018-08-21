package com.olah.homeautomation.helloworldlambda;

public class DeviceStatusCheckFailed extends RuntimeException {
    public DeviceStatusCheckFailed(Throwable throwable) {
        super(throwable);
    }

    public DeviceStatusCheckFailed(String errorMessage) {
        super(errorMessage);
    }
}
