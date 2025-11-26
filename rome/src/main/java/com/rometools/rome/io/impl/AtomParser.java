package main.java.com.rometools.rome.io.impl;

import java.util.Locale;

import javax.xml.stream.events.Namespace;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.BaseWireFeedParser;

public abstract class AtomParser extends BaseWireFeedParser {
    protected AtomParser(final String type, final Namespace namespace) {
        super(type, namespace);
    }

    @Override
    public boolean isMyType(final Document document) {
        final Element rssRoot = document.getRootElement();
        final Namespace defaultNS = rssRoot.getNamespace();
        return defaultNS != null && defaultNS.equals(getAtomNamespace());
    }

    @Override
    public WireFeed parse(final Document document, final boolean validate, final Locale locale) throws IllegalArgumentException, FeedException {
        if (validate) {
            validateFeed(document);
        }
        final Element rssRoot = document.getRootElement();
        return parseFeed(rssRoot, locale);
    }

    protected void validateFeed(final Document document) throws FeedException {
        // TBD here we have to validate the Feed against a schema or whatever not sure how to do it
        // one posibility would be to produce an ouput and attempt to parse it again with validation
        // turned on. otherwise will have to check the document elements by hand.
    }
}