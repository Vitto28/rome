package com.rometools.rome.io.impl;

public class StreamStateFour extends StreamState{

    public StreamStateFour(boolean loop, boolean hasContent, XmlFixerReader reader) {
        super(loop, hasContent, reader);
    }

    @Override
    public StreamState handleState(int c) {
        StreamState nextState;
        if (c == -1) {
            loop = false;
//            hasContent = true;
            setReaderState(3);
            nextState = this;
        } else if (c != '-') {
            bufferAppend(c);
            loop = true;
            nextState = this;
        } else {
            bufferAppend(c);
            loop = true;
            nextState = new StreamStateFive(loop, hasContent, reader);
        }
        return nextState;
    }
}

