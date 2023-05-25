package com.yolo.validator.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Phone {
	@NotBlank
  	private String operatorType;        
  	@NotBlank    
  	private String phoneNum;
}