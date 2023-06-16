package com.yolo.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Getter
@Setter
public class Demo10 {
    @Delegate
    private Person person;
}
