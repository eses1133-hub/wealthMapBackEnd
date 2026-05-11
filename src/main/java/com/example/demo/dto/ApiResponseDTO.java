package com.example.demo.dto;

public record ApiResponseDTO<T>(
	    int code,
	    String message,
	    T data
	) {}
