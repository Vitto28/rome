package com.rometools.rome.io.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;

public abstract class AtomGenerator extends BaseWireFeedGenerator {

    private final String version;

    protected AtomGenerator(String type, final String version) {
        super(type);
        this.version = version;
    }

    protected String version() {
        return version;
    }

    protected abstract Namespace feedNamespace();

    @Override
    public Document generate(final WireFeed wFeed) throws FeedException {
        final Feed feed = (Feed) wFeed;
        final Element root = createRootElement(feed);
        populateFeed(feed, root);
        purgeUnusedNamespaceDeclarations(root);
        return createDocument(root);
    }

    protected Document createDocument(final Element root) {
        return new Document(root);
    }

    protected final Element createRootElement(final Feed feed) {
        final Element root = new Element("feed", feedNamespace());
        root.addNamespaceDeclaration(feedNamespace());
        createRootElementHelper(feed, root);
        generateModuleNamespaceDefs(root);
        return root;
    }

    // Atom03 and Atom10 generators implement this
    protected abstract void createRootElementHelper(final Feed feed, final Element root);

    protected void populateFeed(final Feed feed, final Element parent) throws FeedException {
        addFeed(feed, parent);
        addEntries(feed, parent);
    }

    // template
    protected void addFeed(final Feed feed, final Element eFeed) throws FeedException {
        populateFeedHeader(feed, eFeed);
        addFeedHelper(feed, eFeed);
    }

    // Atom03 and Atom10 generators implement this
    protected abstract void addFeedHelper(final Feed feed, final Element eFeed) throws FeedException;

    protected void addEntries(final Feed feed, final Element parent) throws FeedException {
        final List<Entry> items = feed.getEntries();
        for (final Entry entry : items) {
            addEntry(entry, parent);
        }
        checkEntriesConstraints(parent);
    }

    // template
    protected final void addEntry(final Entry entry, final Element parent) throws FeedException {
        final Element eEntry = new Element("entry", feedNamespace());
        addEntryHelper(entry, eEntry); // concrete specific behavior
        checkEntryConstraints(eEntry);
        generateItemModules(entry.getModules(), eEntry);
        parent.addContent(eEntry);
    }

    // Atom03 and Atom10 generators implement this
    protected abstract void addEntryHelper(final Entry entry, final Element eEntry) throws FeedException;

    protected abstract void fillContentElement(final Element contentElement, final Content content) throws FeedException;

    // template
    protected final void populateFeedHeader(final Feed feed, final Element eFeed) throws FeedException {
        final Content titleEx = feed.getTitleEx();
        if (titleEx != null) {
            final Element titleElement = new Element("title", feedNamespace());
            fillContentElement(titleElement, titleEx);
            eFeed.addContent(titleElement);
        }

        final List<Link> alternateLinks = feed.getAlternateLinks();
        if (alternateLinks != null) {
            for (final Link link : alternateLinks) {
                eFeed.addContent(generateLinkElement(link));
            }
        }

        final List<Link> otherLinks = feed.getOtherLinks();
        if (otherLinks != null) {
            for (final Link link : otherLinks) {
                eFeed.addContent(generateLinkElement(link));
            }
        }

        populateFeedHeaderHelper(feed, eFeed);
    }

    // helper
    protected abstract void populateFeedHeaderHelper(final Feed feed, final Element eFeed) throws FeedException;

    // template
    protected final Element generateLinkElement(final Link link) {
        final Element linkElement = new Element("link", feedNamespace());

        final String rel = link.getRel();
        if (rel != null) {
            final Attribute relAttribute = new Attribute("rel", rel);
            linkElement.setAttribute(relAttribute);
        }

        final String type = link.getType();
        if (type != null) {
            final Attribute typeAttribute = new Attribute("type", type);
            linkElement.setAttribute(typeAttribute);
        }

        final String href = link.getHref();
        if (href != null) {
            final Attribute hrefAttribute = new Attribute("href", href);
            linkElement.setAttribute(hrefAttribute);
        }

        // extra work (implementation-specific)
        generateLinkElementHelper(link, linkElement);

        return linkElement;
    }

    // helper (to be implemented by concrete classes)
    protected abstract void generateLinkElementHelper(final Link link, final Element linkElement);

    // method was the same on both classes, just copied straight over
    protected Element generateSimpleElement(final String name, final String value) {
        final Element element = new Element(name, feedNamespace());
        element.addContent(value);
        return element;
    }

    // helper
    protected void addContributorToFeed(final Element eFeed, final SyndPerson contributor) throws FeedException {
        final Element contributorElement = new Element("contributor", feedNamespace());
        fillPersonElement(contributorElement, contributor);
        eFeed.addContent(contributorElement);
    }

    // template
    protected final void fillPersonElement(final Element element, final SyndPerson person) throws FeedException {
        final String name = person.getName();
        if (name != null) {
            element.addContent(generateSimpleElement("name", name));
        }

        final String uri = person.getUri();
        if (uri != null) {
            element.addContent(generateSimpleElement("uri", uri));
        }

        final String email = person.getEmail();
        if (email != null) {
            element.addContent(generateSimpleElement("email", email));
        }

        // implementation-dependent
        fillPersonElementHelper(element, person);
    }

    // helper, implemented by atom10 and atom3 generators
    protected abstract void fillPersonElementHelper(final Element element, final SyndPerson person) throws FeedException;

    protected void addElementToFeed(final Feed feed, final Element eFeed, final String elementName) throws FeedException {
        final Content content;
        switch (elementName) {
            case "tagline":
                content = feed.getTagline();
                break;
            case "subtitle":
                content = feed.getSubtitle();
                break;
            default:
                content = null;
        }
        if (content != null) {
            final Element element = new Element(elementName, feedNamespace());
            fillContentElement(element, content);
            eFeed.addContent(element);
        }

    }

    protected void setFeedId(final Feed feed, final Element eFeed) {
        final String id = feed.getId();
        if (id != null) {
            eFeed.addContent(generateSimpleElement("id", id));
        }
    }

    // implementation was the same on both classes, copied straight over
    protected Element generateGeneratorElement(final Generator generator) {

        final Element generatorElement = new Element("generator", feedNamespace());

        final String url = generator.getUrl();
        if (url != null) {
            final Attribute urlAttribute = new Attribute("url", url);
            generatorElement.setAttribute(urlAttribute);
        }

        final String version = generator.getVersion();
        if (version != null) {
            final Attribute versionAttribute = new Attribute("version", version);
            generatorElement.setAttribute(versionAttribute);
        }

        final String value = generator.getValue();
        if (value != null) {
            generatorElement.addContent(value);
        }

        return generatorElement;

    }

    protected void setFeedGenerator(final Feed feed, final Element eFeed) {
        final Generator generator = feed.getGenerator();
        if (generator != null) {
            eFeed.addContent(generateGeneratorElement(generator));
        }
    }

    protected void setFeedDate(final Feed feed, final Element eFeed, final String dateType) {
        final Date date;
        if ("modified".equals(dateType)) {
            date = feed.getModified();
        } else if ("updated".equals(dateType)) {
            date = feed.getUpdated();
        } else {
            date = null;
        }
        if (date == null) return;

        final Element element = new Element(dateType, feedNamespace());
        element.addContent(DateParser.formatW3CDateTime(date, Locale.US));
        eFeed.addContent(element);
    }

    // these methods had no implementation in the concrete classes
    // im assuming their implementation would've been the same
    protected void checkFeedHeaderConstraints(final Element eFeed) throws FeedException {
    }

    protected void checkEntriesConstraints(final Element parent) throws FeedException {
    }

    protected void checkEntryConstraints(final Element eEntry) throws FeedException {
    }
}