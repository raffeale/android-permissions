package com.stericson.permissions.donate.interfaces;

import org.xmlpull.v1.XmlPullParser;

public interface PermissionsParserDelegate {

    //handles the start tag and the return indicates to the parser whether or not we want to continue parsing the document
    public boolean handleStartTag(XmlPullParser xpp);

    //handles the end tag and the return indicates to the parser whether or not we want to continue parsing the document
    public boolean handleEndTag(XmlPullParser xpp);

    //handles a permissions that we want to work with
    public boolean handlePermission(XmlPullParser xpp);

}
