package com.example.metars;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

public class TafInfo extends StationRequester {

    public TafInfo(ActivityViewModel activityViewModel, CoordinatorLayout root, String handlerName, String url, String s) {
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

        int eventType;

        try {
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

            return;
        }

        String name = null;

        boolean isSkyCondition = true;

        mStringBuilder = new StringBuilder();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.END_TAG: {

                    break;
                }

                case XmlPullParser.START_TAG: {
                    name = xpp.getName();

                    if (mActivityViewModel.mDisplayFormatted) {
                        if (Objects.equals(name, "forecast")) {
                            isSkyCondition = true;
                        }

                        if (Objects.equals(name, "sky_condition")) {
                            if (xpp.getAttributeCount() > 0) {
                                if (isSkyCondition) {
                                    isSkyCondition = false;
                                    mStringBuilder.append("\nSky:");
                                }

                                for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                    mStringBuilder.append(xpp.getAttributeValue(i))
                                            .append(" ");
                                }
                            }
                        }
                    }

                    break;
                }

                case XmlPullParser.TEXT: {
                    try {
                        if (!xpp.isWhitespace()) {
                            if (Objects.equals(name, "raw_text")) {
                                if (mActivityViewModel.mDisplayFormatted) {
                                    mStringBuilder.append("\n\nRaw Text:")
                                            .append(xpp.getText().replace("FM", "\nFM"));
                                } else {
                                    mStringBuilder.append("\n\n")
                                            .append(xpp.getText().replace("FM", "\nFM"));
                                }

                                break;
                            }

                            if (mActivityViewModel.mDisplayFormatted) {
                                if (Objects.equals(name, "issue_time")) {
                                    mStringBuilder.append("\nIssue:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "bulletin_time")) {
                                    mStringBuilder.append("\nBulletin:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "valid_time_from")) {
                                    mStringBuilder.append("\nValid From:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "valid_time_to")) {
                                    mStringBuilder.append("\nValid To:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "fcst_time_from")) {
                                    mStringBuilder.append("\n\nForecast\nFrom:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "fcst_time_to")) {
                                    mStringBuilder.append("\nTo:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "change_indicator")) {
                                    mStringBuilder.append("\nChange:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "probability")) {
                                    mStringBuilder.append("\nProbability:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "remarks")) {
                                    mStringBuilder.append("\nRemarks:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "wind_dir_degrees")) {
                                    mStringBuilder.append("\nWinds:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "wind_speed_kt")) {
                                    mStringBuilder.append("/")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "wx_string")) {
                                    mStringBuilder.append("\nWX:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "visibility_statute_mi")) {
                                    mStringBuilder.append("\nVisibility:")
                                            .append(xpp.getText());

                                    break;
                                }
                            }
                        }
                    } catch (XmlPullParserException e) {
                        mActivityViewModel.mErrorToaster.LogError(mView, e.toString());

                        return;
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