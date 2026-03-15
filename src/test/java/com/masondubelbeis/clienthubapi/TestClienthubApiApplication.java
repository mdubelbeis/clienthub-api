package com.masondubelbeis.clienthubapi;

import org.springframework.boot.SpringApplication;

public class TestClienthubApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(ClienthubApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
