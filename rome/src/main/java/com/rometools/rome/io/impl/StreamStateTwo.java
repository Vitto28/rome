package com.rometools.rome.io.impl;

public class StreamStateTwo extends StreamState{

    public StreamStateTwo(boolean loop, boolean hasContent, XmlFixerReader reader) {
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
        } else if (c == '-') {
            bufferAppend(c);
            loop = true;
            nextState = new StreamStateThree(loop, hasContent, reader);
        } else {
            bufferAppend(c);
            loop = false;
//            hasContent = true;
            setReaderState(3);
            nextState = this;
        }
        return nextState;
    }
}

