/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.rss2mail;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;
import org.mule.util.IOUtils;

import java.io.InputStream;
import java.util.UUID;

public class RssToMailTestCase extends FunctionalTestCase
{
    private MuleClient client;

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        client = muleContext.getClient();
    }

    @Override
    protected String getConfigResources()
    {
        return "feed-split-test-config.xml";
    }

    public void testSendFeedWithOneEntry() throws Exception
    {
        String payload = loadFeedAndReplaceId("single-entry.rss.xml");
        MuleEvent event = getTestEvent(payload);
        sendEventToFeedSplitter(event);

        MuleMessage resultMessage = client.request("vm://sendMail", RECEIVE_TIMEOUT);
        assertMessageWasSuccessfullyProcessed(resultMessage);
    }

    public void testSameEntrySentMultipleTimesIsFiltered() throws Exception
    {
        String payload = loadFeedAndReplaceId("single-entry.rss.xml");
        MuleEvent event = getTestEvent(payload);
        sendEventToFeedSplitter(event);

        MuleMessage resultMessage = client.request("vm://sendMail", RECEIVE_TIMEOUT);
        assertMessageWasSuccessfullyProcessed(resultMessage);

        // send the same message again, this time it must be filtered
        event = getTestEvent(payload);
        sendEventToFeedSplitter(event);

        resultMessage = client.request("vm://sendMail", RECEIVE_TIMEOUT);
        assertNull(resultMessage);
    }

    private String loadFeedAndReplaceId(String filename)
    {
        String contents = stringWithContentsOfFile(filename);

        // insert a new id into the feed entry every time to avoid messages being filtered
        // by the idempotent-message-filter
        return contents.replace("@@uuid@@", UUID.randomUUID().toString());
    }

    private String stringWithContentsOfFile(String filename)
    {
        InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
        assertNotNull(input);

        try
        {
            return IOUtils.toString(input);
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }
    }

    private void sendEventToFeedSplitter(MuleEvent event) throws Exception
    {
        SimpleFlowConstruct flowConstruct = lookupFlowConstruct("feedSplitter");
        flowConstruct.process(event);
    }

    private SimpleFlowConstruct lookupFlowConstruct(String name)
    {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }

    private void assertMessageWasSuccessfullyProcessed(MuleMessage message)
    {
        assertNotNull(message);

        // check that the MD5 sum of the feed's id was calculated
        assertNotNull(message.getOutboundProperty("feed.guid"));

        // check that the email properties were properly put into the message. Check the subject
        // only as that's the only property we can safely hard-code
        assertEquals("Mashups and ESBs at Qcon", message.getOutboundProperty("subject"));
    }
}
