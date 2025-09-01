package me.maxhub.logger.encoder;

public interface MessageEncoder<T> {

    T encode(Object message);

    String toString(Object message);
}
