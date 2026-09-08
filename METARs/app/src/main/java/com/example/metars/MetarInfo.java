package com.example.metars;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Objects;

public class MetarInfo extends StationRequester {
    public double mTemp;
    public double mDewpoint;
    public double mAltimeter;

    public MetarInfo(ActivityViewModel activityViewModel, CoordinatorLayout root, String handlerName, String url, String s) {
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
                        if (Objects.equals(name, "METAR")) {
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
                                            .append(xpp.getText());
                                } else {
                                    mStringBuilder.append("\n\n")
                                            .append(xpp.getText());
                                }

                                break;
                            }

                            if (mActivityViewModel.mDisplayFormatted) {
                                if (Objects.equals(name, "observation_time")) {
                                    mStringBuilder.append("\nTime:")
                                            .append(mActivityViewModel.FlipTimeDate(xpp.getText().replace("T", " ")));

                                    break;
                                }

                                if (Objects.equals(name, "latitude")) {
                                    mStringBuilder.append("\nGPS:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "longitude")) {
                                    mStringBuilder.append(", ")
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

                                if (Objects.equals(name, "wind_gust_kt")) {
                                    mStringBuilder.append("\nWind Gust:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "visibility_statute_mi")) {
                                    mStringBuilder.append("\nVisibility:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "sea_level_pressure_mb")) {
                                    mStringBuilder.append("\nSea level Pressure:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "wx_string")) {
                                    mStringBuilder.append("\nWX:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "corrected")) {
                                    mStringBuilder.append("\nCorrected:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "auto")) {
                                    mStringBuilder.append("\nAuto:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "auto_station")) {
                                    mStringBuilder.append("\nAuto Station:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "maintenance_indicator_on")) {
                                    mStringBuilder.append("\nMaintenance:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "no_signal")) {
                                    mStringBuilder.append("\nNo Signal:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "lightning_sensor_off")) {
                                    mStringBuilder.append("\nLightning Sensor Off:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "freezing_rain_sensor_off")) {
                                    mStringBuilder.append("\nFreezing Rain Sensor Off:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "present_weather_sensor_off")) {
                                    mStringBuilder.append("\nPresent Weather Sensor Off:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "flight_category")) {
                                    mStringBuilder.append("\nFlight Category:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "maxT_c")) {
                                    mStringBuilder.append("\nMax Temp:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "minT_c")) {
                                    mStringBuilder.append("\nMin Temp:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "maxT24hr_c")) {
                                    mStringBuilder.append("\nMax Temp 24hr:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "minT24hr_c")) {
                                    mStringBuilder.append("\nMin Temp 24hr:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "precip_in")) {
                                    mStringBuilder.append("\nPrecipitation:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "pcp3hr_in")) {
                                    mStringBuilder.append("\nPrecipitation 3hr:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "pcp6hr_in")) {
                                    mStringBuilder.append("\nPrecipitation 6hr:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "pcp24hr_in")) {
                                    mStringBuilder.append("\nPrecipitation 24hr:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "snow_in")) {
                                    mStringBuilder.append("\nSnow:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "vert_vis_ft")) {
                                    mStringBuilder.append("\nVertical Visibility:")
                                            .append(xpp.getText());

                                    break;
                                }

                                if (Objects.equals(name, "metar_type")) {
                                    mStringBuilder.append("\nMetar Type:")
                                            .append(xpp.getText());

                                    break;
                                }
                            }

                            if (Objects.equals(name, "temp_c")) {
                                mTemp = Double.parseDouble(xpp.getText());

                                Temperature temperature = new Temperature("C", mTemp);

                                if (mActivityViewModel.mDisplayFormatted) {
                                    mStringBuilder.append("\nTemperature:")
                                            .append(xpp.getText())
                                            .append(" (")
                                            .append(String.format(Locale.ENGLISH, "%.2f", temperature.fValue))
                                            .append(")");
                                }

                                break;
                            }

                            if (Objects.equals(name, "dewpoint_c")) {
                                mDewpoint = Double.parseDouble(xpp.getText());

                                Temperature dewpoint = new Temperature("C", mDewpoint);

                                if (mActivityViewModel.mDisplayFormatted) {
                                    mStringBuilder.append("\nDewpoint:")
                                            .append(xpp.getText())
                                            .append(" (")
                                            .append(String.format(Locale.ENGLISH, "%.2f", dewpoint.fValue))
                                            .append(")");
                                }

                                break;
                            }

                            if (Objects.equals(name, "altim_in_hg")) {
                                mAltimeter = Double.parseDouble(xpp.getText());

                                if (mActivityViewModel.mDisplayFormatted) {
                                    mStringBuilder.append("\nAltimeter:")
                                            .append(String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(xpp.getText())));
                                }

                                break;
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

    @NonNull
    public String FormatAtmosphereData(double elevationMeters) {
        StringBuilder str = new StringBuilder();

        double elevation = elevationMeters * mActivityViewModel.mFeetInMeters;

        Temperature temperatureCelcius = new Temperature("C", mTemp);

        Temperature dewpointCelcius = new Temperature("C", mDewpoint);

        double pressureAltitude = PressureAltitude(mAltimeter);

        double densityAltitude = DensityAltitude(temperatureCelcius, mAltimeter, dewpointCelcius);

        double relativeHumidity = RelativeHumidity(mDewpoint, mTemp);

        double cloudBaseAGL = CloudBaseAGL(mTemp, mDewpoint);

        str.append("\nPressure Altitude:");
        str.append(String.format(Locale.ENGLISH, "%.2f", pressureAltitude + elevation));

        str.append("\nDensity Altitude:");
        str.append(String.format(Locale.ENGLISH, "%.2f", densityAltitude));
        str.append(" (");
        str.append(String.format(Locale.ENGLISH, "%.2f", (elevationMeters * mActivityViewModel.mFeetInMeters) + densityAltitude));
        str.append(")");

        str.append("\nRelative Humidity:");
        str.append(String.format(Locale.ENGLISH, "%.2f", relativeHumidity));

        str.append("\nCloud Base AGL:");
        str.append(String.format(Locale.ENGLISH, "%.0f", cloudBaseAGL));

        return str.toString();
    }

    private double PressureAltitude(double altimeter) {
        return 145366.45 * (1.0 - Math.pow(((33.8639 * altimeter) / 1013.25), 0.190284));
    }

    // temperature in celcius
    private double CloudBaseAGL(double temperature, double dewpoint) {
        return ((temperature - dewpoint) / 2.5) * 1000.00;
    }

    // temperature in celcius
    private double DensityAltitude(Temperature temperature, double pressureHg, Temperature dewpoint) {
        double virtualTemperature = VirtualTemperature(temperature, pressureHg, dewpoint);

        // Virtual temperature in Kelvin
        return CalcDensityAltitude(pressureHg, virtualTemperature);
    }

    // Find virtual temperature using temperature and dew point in celcius
    private double VirtualTemperature(Temperature temperatureCelcius, double pressureHg, @NonNull Temperature dewpointCelcius) {
        // vapor pressure uses celcius
        double vaporPressure = 6.11 *
                Math.pow(10.0, ((7.5 * dewpointCelcius.cValue) / (237.7 + dewpointCelcius.cValue)));

        double mbPressure = 33.8639 * pressureHg;

        // Returning temperature in Kelvin
        if (mbPressure != 0) {
            return temperatureCelcius.kValue / (1.0 - (vaporPressure / mbPressure) * (1.0 - 0.622));
        }

        return 0.0;
    }

    private double CalcDensityAltitude(double pressureHg, double virtualTemperature) {
        // virtual temperature in Kelvin
        Temperature temperatureKelvin = new Temperature("K", virtualTemperature);

        // Virtual temperature as Rankine
        double pressure = (17.326 * pressureHg) / temperatureKelvin.rValue;

        // weather.gov and seems to be the most used
        return 145366.0 * (1.0 - (Math.pow(pressure, 0.235)));

        // NWS
        //return 145442.16 * (1.0 - (Math.Pow(p, 0.235)));
    }

    private double RelativeHumidity(double dewpoint, double temperature) {
        // Temperatures are celcius
        // =100*(EXP((17.625*TD)/(243.04+TD))/EXP((17.625*T)/(243.04+T)))
        return 100.0 *
                (Math.exp((17.625 * dewpoint) / (243.04 + dewpoint)) /
                        Math.exp((17.625 * temperature) / (243.04 + temperature)));
    }
}