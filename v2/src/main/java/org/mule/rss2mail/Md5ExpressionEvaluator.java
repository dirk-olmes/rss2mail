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

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.expression.ExpressionEvaluator;

public class Md5ExpressionEvaluator implements ExpressionEvaluator
{
    private static final String NAME = "md5";

    public Md5ExpressionEvaluator()
    {
        super();
    }

    @Override
    public Object evaluate(String expression, MuleMessage message)
    {
        MuleContext context = message.getMuleContext();
        Object value = context.getExpressionManager().evaluate(expression, message);
        if (value != null)
        {
            return MD5Sum.digest(value.toString());
        }

        return null;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public void setName(String name)
    {
        throw new UnsupportedOperationException();
    }
}
