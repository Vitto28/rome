package com.rometools.rome.io.impl;

public class StreamStateZero extends StreamState{

    public StreamStateZero(boolean loop, boolean hasContent, XmlFixerReader reader) {
        super(loop, hasContent, reader);
    }

    @Override
    public StreamState handleState(int c) {
        StreamState nextState;
        if (c == -1) {
            loop = false;
            hasContent = false;
            nextState = this;
        } else if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
            loop = true;
            nextState = this;
        } else if (c == '<') {
            setBufferLen(0);
            setBufferPos(0);
            bufferAppend(c);
            loop = true;
            nextState = new StreamStateOne(loop, hasContent, reader);
        } else {
            setBufferLen(0);
            setBufferPos(0);
            bufferAppend(c);
            loop = false;
//            hasContent = true;
            setReaderState(3);
            nextState = this;
        }
        return nextState;
    }
}

