package com.example.metars;

public class Temperature {
    public double fValue;
    public double cValue;
    public double kValue;
    public double rValue;

    public Temperature(String type, double temperature) {
        switch (type) {
            case "C": {
                cValue = temperature;
                fValue = ConvertCtoF(cValue);
                kValue = ConvertCtoK(cValue);
                rValue = ConvertKtoR(kValue);

                break;
            }

            case "F": {
                fValue = temperature;
                cValue = ConvertFtoC(fValue);
                kValue = ConvertCtoK(cValue);
                rValue = ConvertKtoR(kValue);

                break;
            }

            case "K": {
                kValue = temperature;
                cValue = ConvertKtoC(kValue);
                fValue = ConvertCtoF(cValue);
                rValue = ConvertKtoR(kValue);

                break;
            }
        }
    }

    public double ConvertCtoF(double temperature) {
        return (temperature * (9.0 / 5.0)) + 32.0;
    }

    public double ConvertCtoK(double temperature) {
        return temperature + 273.15;
    }

    public double ConvertFtoC(double temperature) {
        return (temperature - 32.0) * (5.0 / 9.0);
    }

    public double ConvertKtoR(double temperature) {
        double temperatureCelcius = ConvertKtoC(temperature);

        double temperatureFahrenheit = ConvertCtoF(temperatureCelcius);

        return temperatureFahrenheit + 459.69;
    }

    public double ConvertKtoC(double temperature) {
        return temperature - 273.15;
    }
}