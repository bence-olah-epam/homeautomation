package com.olah.homeautomation.helloworldlambda;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

public class DeviceStatusUrl extends GenericUrl {

    public DeviceStatusUrl(String encodedUrl) {
        super(encodedUrl);
    }

    @Key
    public String token;

}
