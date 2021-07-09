package com.heliorodri.springwebfluxpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxPocApplication {

    public static void main(String[] args) {
        BlockHound.install(builder ->
                builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));

        SpringApplication.run(SpringWebfluxPocApplication.class, args);
    }

}
