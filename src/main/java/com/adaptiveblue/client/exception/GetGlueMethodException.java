package com.adaptiveblue.client.exception;

import com.adaptiveblue.client.Error;

public class GetGlueMethodException extends GetGlueException {
	private static final long serialVersionUID = 4052665393007453028L;
	
	private final Error _error;

	public GetGlueMethodException(Error error) {
		_error = error;
	}
	
	public Error getError() {
		return _error;
	}

	@Override
	public String getMessage() {
		return _error.message;
	}
}
