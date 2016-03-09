package com.medzone.framework.network.serializer;

public interface ISerializer<T> {

	String serialize(T t);

	T deserialize(String s);
}
