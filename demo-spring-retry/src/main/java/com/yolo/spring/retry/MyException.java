package com.yolo.spring.retry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyException extends Exception {
    private String myMessage;

    public MyException(String myMessage) {
        this.myMessage = myMessage;
    }
}

