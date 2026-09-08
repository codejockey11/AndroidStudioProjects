package com.example.metars;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

public class StationInfo extends StationRequester {
    public String mStationId;
    public double mLatitude;
    public double mLongitude;
    public double mElevationMeters;
    public String mSite;
    public String mState;
    public String mCountry;
    public StringBuilder mType;

    public StationInfo(ActivityViewModel activityViewModel, CoordinatorLayout root, String handlerName, String url, String s) {
        super(activityViewModel, root, handlerName, url, s);
    }

    public void ParseBuffer() {
        XmlPullParserFactory factory;

        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

            return;
        }

        factory.setNamespaceAware(true);

        XmlPullParser xpp;

        try {
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

            return;
        }

        try {
            xpp.setInput(new StringReader(mHttpRequester.mBuffer.toString()));
        } catch (XmlPullParserException e) {
            mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

            return;
        }

        String name = null;

        int eventType;

        mType = new StringBuilder();

        try {
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

            return;
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT: {

                    break;
                }

                case XmlPullParser.START_TAG: {
                    name = xpp.getName();

                    break;
                }

                case XmlPullParser.TEXT: {
                    try {
                        if (!xpp.isWhitespace()) {
                            if (Objects.equals(name, "station_id")) {
                                mStationId = xpp.getText();

                                break;
                            }

                            if (Objects.equals(name, "latitude")) {
                                mLatitude = Double.parseDouble(xpp.getText());

                                break;
                            }

                            if (Objects.equals(name, "longitude")) {
                                mLongitude = Double.parseDouble(xpp.getText());

                                break;
                            }

                            if (Objects.equals(name, "elevation_m")) {
                                mElevationMeters = Double.parseDouble(xpp.getText());

                                break;
                            }

                            if (Objects.equals(name, "site")) {
                                mSite = xpp.getText();

                                break;
                            }

                            if (Objects.equals(name, "state")) {
                                mState = xpp.getText();

                                break;
                            }

                            if (Objects.equals(name, "country")) {
                                mCountry = xpp.getText();

                                break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

                        return;
                    }

                    break;
                }

                case XmlPullParser.END_TAG: {
                    name = xpp.getName();

                    if (Objects.equals(name, "METAR")) {
                        mType.append("METAR");

                        break;
                    }

                    if (Objects.equals(name, "TAF")) {
                        mType.append("/TAF");
                    }

                    break;
                }
            }

            try {
                eventType = xpp.next();
            } catch (IOException | XmlPullParserException e) {
                mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

                return;
            }
        }
    }
}