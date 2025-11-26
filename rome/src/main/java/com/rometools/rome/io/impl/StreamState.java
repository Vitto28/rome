package com.rometools.rome.io.impl;

public abstract class StreamState {
    boolean hasContent;
    boolean loop;
    XmlFixerReader reader;

    public StreamState(final boolean loop, final boolean hasContent, final XmlFixerReader reader) {
        this.loop = loop;
        this.hasContent = hasContent;
        this.reader = reader;
    }

    public void bufferAppend(int c) {
        reader.bufferAppend((char) c);
    }

    public void setBufferLen(int len) {
        reader.setBufferLen(len);
    }

    public void setBufferPos(int pos) {
        reader.setBufferPos(pos);
    }

    public void setReaderState(int state) {
        reader.setState(state);
    }

    public abstract StreamState handleState(int c);
}
