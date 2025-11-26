/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.rometools.rome.io.impl;

import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.utils.Lists;

/**
 * Feed Generator for Atom
 * <p/>
 */

public class Atom03Generator extends AtomGenerator {

    private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
    private static final Namespace ATOM_NS = Namespace.getNamespace(ATOM_03_URI);


    public Atom03Generator() {
        this("atom_0.3", "0.3");
    }

    protected Atom03Generator(final String type, final String version) {
        super(type, version);
    }

    @Override
    protected Namespace feedNamespace() {
        return ATOM_NS;
    }

    protected void createRootElementHelper(final Feed feed, final Element root) {
        final Attribute version = new Attribute("version", version());
        root.setAttribute(version);
    }

    @Override
    protected void addFeedHelper(final Feed feed, final Element eFeed) throws FeedException {
        checkFeedHeaderConstraints(eFeed);
        generateFeedModules(feed.getModules(), eFeed);
        generateForeignMarkup(eFeed, feed.getForeignMarkup());
    }

    @Override
    protected void addEntryHelper(final Entry entry, final Element eEntry) throws FeedException {
        populateEntry(entry, eEntry);
    }

    @Override
    protected void populateFeedHeaderHelper(final Feed feed, final Element eFeed) throws FeedException {

        final List<SyndPerson> authors = feed.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            final Element authorElement = new Element("author", feedNamespace());
            fillPersonElement(authorElement, authors.get(0));
            eFeed.addContent(authorElement);
        }

        final List<SyndPerson> contributors = feed.getContributors();
        for (final SyndPerson contributor : contributors) {
            addContributorToFeed(eFeed, contributor);
        }

        addElementToFeed(feed, eFeed, "tagline");

        setFeedId(feed, eFeed);

        setFeedGenerator(feed, eFeed);

        final String copyright = feed.getCopyright();
        if (copyright != null) {
            eFeed.addContent(generateSimpleElement("copyright", copyright));
        }

        final Content info = feed.getInfo();
        if (info != null) {
            final Element infoElement = new Element("info", feedNamespace());
            fillContentElement(infoElement, info);
            eFeed.addContent(infoElement);
        }

        setFeedDate(feed, eFeed, "modified");

    }

    protected void populateEntry(final Entry entry, final Element eEntry) throws FeedException {

        final Content titleEx = entry.getTitleEx();
        if (titleEx != null) {
            final Element titleElement = new Element("title", feedNamespace());
            fillContentElement(titleElement, titleEx);
            eEntry.addContent(titleElement);
        }

        final List<Link> alternateLinks = entry.getAlternateLinks();
        for (final Link link : alternateLinks) {
            eEntry.addContent(generateLinkElement(link));
        }

        final List<Link> otherLinks = entry.getOtherLinks();
        for (final Link link : otherLinks) {
            eEntry.addContent(generateLinkElement(link));
        }

        final List<SyndPerson> authors = entry.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            final Element authorElement = new Element("author", feedNamespace());
            fillPersonElement(authorElement, authors.get(0));
            eEntry.addContent(authorElement);
        }

        final List<SyndPerson> contributors = entry.getContributors();
        for (final SyndPerson contributor : contributors) {
            final Element contributorElement = new Element("contributor", feedNamespace());
            fillPersonElement(contributorElement, contributor);
            eEntry.addContent(contributorElement);
        }

        final String id = entry.getId();
        if (id != null) {
            eEntry.addContent(generateSimpleElement("id", id));
        }

        final Date modified = entry.getModified();
        if (modified != null) {
            final Element modifiedElement = new Element("modified", feedNamespace());
            modifiedElement.addContent(DateParser.formatW3CDateTime(modified, Locale.US));
            eEntry.addContent(modifiedElement);
        }

        final Date issued = entry.getIssued();
        if (issued != null) {
            final Element issuedElement = new Element("issued", feedNamespace());
            issuedElement.addContent(DateParser.formatW3CDateTime(issued, Locale.US));
            eEntry.addContent(issuedElement);
        }

        final Date created = entry.getCreated();
        if (created != null) {
            final Element createdElement = new Element("created", feedNamespace());
            createdElement.addContent(DateParser.formatW3CDateTime(created, Locale.US));
            eEntry.addContent(createdElement);
        }

        final Content summary = entry.getSummary();
        if (summary != null) {
            final Element summaryElement = new Element("summary", feedNamespace());
            fillContentElement(summaryElement, summary);
            eEntry.addContent(summaryElement);
        }

        final List<Content> contents = entry.getContents();
        for (final Content content : contents) {
            final Element contentElement = new Element("content", feedNamespace());
            fillContentElement(contentElement, content);
            eEntry.addContent(contentElement);
        }

        generateForeignMarkup(eEntry, entry.getForeignMarkup());

    }

    @Override
    protected void generateLinkElementHelper(final Link link, final Element linkElement) {
        // no extra functionality for Atom 0.3
    }

    @Override
    protected void fillPersonElementHelper(final Element element, final SyndPerson person) {
        // Atom03 generator adds no additional functionality
    }

    protected Element generateTagLineElement(final Content tagline) {

        final Element taglineElement = new Element("tagline", feedNamespace());

        final String type = tagline.getType();
        if (type != null) {
            final Attribute typeAttribute = new Attribute("type", type);
            taglineElement.setAttribute(typeAttribute);
        }

        final String value = tagline.getValue();
        if (value != null) {
            taglineElement.addContent(value);
        }

        return taglineElement;

    }

    @Override
    protected void fillContentElement(final Element contentElement, final Content content) throws FeedException {

        final String type = content.getType();
        if (type != null) {
            final Attribute typeAttribute = new Attribute("type", type);
            contentElement.setAttribute(typeAttribute);
        }

        final String mode = content.getMode();
        if (mode != null) {
            final Attribute modeAttribute = new Attribute("mode", mode);
            contentElement.setAttribute(modeAttribute);
        }

        final String value = content.getValue();
        if (value != null) {

            if (mode == null || mode.equals(Content.ESCAPED)) {

                contentElement.addContent(value);

            } else if (mode.equals(Content.BASE64)) {

                contentElement.addContent(Base64.encode(value));

            } else if (mode.equals(Content.XML)) {

                final StringBuffer tmpDocString = new StringBuffer("<tmpdoc>");
                tmpDocString.append(value);
                tmpDocString.append("</tmpdoc>");
                final StringReader tmpDocReader = new StringReader(tmpDocString.toString());
                Document tmpDoc;

                try {
                    final SAXBuilder saxBuilder = new SAXBuilder();
                    tmpDoc = saxBuilder.build(tmpDocReader);
                } catch (final Exception ex) {
                    throw new FeedException("Invalid XML", ex);
                }

                final List<org.jdom2.Content> children = tmpDoc.getRootElement().removeContent();
                contentElement.addContent(children);
            }

        }
    }

}
