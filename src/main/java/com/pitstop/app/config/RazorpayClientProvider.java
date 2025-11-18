package com.pitstop.app.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RazorpayClientProvider {
    private final RazorpayClient client;
    public RazorpayClientProvider(
            @Value("${razorpay.key-id}") String key,
            @Value("${razorpay.key-secret}") String secret
    ) throws RazorpayException {

        this.client = new RazorpayClient(key, secret);
    }

}
