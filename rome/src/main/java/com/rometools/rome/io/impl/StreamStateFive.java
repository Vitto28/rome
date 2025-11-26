package com.rometools.rome.io.impl;

public class StreamStateFive extends StreamState{

    public StreamStateFive(boolean loop, boolean hasContent, XmlFixerReader reader) {
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
            nextState = new StreamStateFour(loop, hasContent, reader);
        } else {
            bufferAppend(c);
            loop = true;
            nextState = new StreamStateSix(loop, hasContent, reader);
        }
        return nextState;
    }
}

