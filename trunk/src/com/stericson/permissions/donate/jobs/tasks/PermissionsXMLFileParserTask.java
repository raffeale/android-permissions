package com.stericson.permissions.donate.jobs.tasks;

import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.interfaces.PermissionsParserDelegate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;

public class PermissionsXMLFileParserTask {

    public static boolean parse(PermissionsParserDelegate ppd) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new FileReader(Constants.path()));
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && ppd.handleStartTag(xpp)) {

                    while (eventType != XmlPullParser.END_DOCUMENT ) {
                        if (eventType == XmlPullParser.END_TAG && ppd.handleEndTag(xpp)) {
                            break;
                        }
                        if (xpp.getEventType() == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("item")) {
                                if (ppd.handlePermission(xpp)) {
                                    break;
                                }
                            }
                        }
                        eventType = xpp.next();
                    }
                }
                eventType = xpp.next();
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
