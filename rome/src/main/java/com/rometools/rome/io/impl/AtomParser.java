package com.rometools.rome.io.impl;

import java.util.Locale;

import com.rometools.rome.feed.atom.Link;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;

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

    protected abstract Namespace getAtomNamespace();

    protected abstract WireFeed parseFeed(Element eFeed, Locale locale) throws FeedException;

    protected final Link parseLink(final Element eLink, final String baseURI) {
        final Link link = new Link();

        setbaseAttributes(eLink, link);
        setAttributes(eLink, link, baseURI);

        return link;
    }

    // sets rel, type, and href
    protected final void setbaseAttributes(final Element eLink, final Link link) {
        final String rel = getAttributeValue(eLink, "rel");
        if (rel != null) {
            link.setRel(rel);
        }

        final String type = getAttributeValue(eLink, "type");
        if (type != null) {
            link.setType(type);
        }

        final String href = getAttributeValue(eLink, "href");
        if (href != null) {
            link.setHref(href);
        }
    }

    // the Atom03 and Atom10 parsers should override this
    protected abstract void setAttributes(final Element eLink, final Link link, final String baseURI);
}