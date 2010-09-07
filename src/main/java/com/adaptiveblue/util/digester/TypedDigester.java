package com.adaptiveblue.util.digester;

import java.io.InputStream;

public interface TypedDigester<T> {
	T digest(InputStream stream) throws Exception;
}
