package com.rometools.rome.io.impl;

public class StreamStateSix extends StreamState{

    public StreamStateSix(boolean loop, boolean hasContent, XmlFixerReader reader) {
        super(loop, hasContent, reader);
    }

    @Override
    public StreamState handleState(int c) {
        StreamState nextState;
        if (c == -1) {
            loop = false;
            hasContent = true;
            setReaderState(3);
            nextState = this;
        } else if (c != '>') {
            bufferAppend(c);
            loop = true;
            nextState = new StreamStateFour(loop, hasContent, reader);
        } else {
            setBufferLen(0);
            loop = true;
            nextState = new StreamStateZero(loop, hasContent, reader);
        }
        return nextState;
    }
}

