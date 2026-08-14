package com.example.audioplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://stackoverflow.com/questions/190629/what-is-the-easiest-way-to-parse-an-ini-file-in-java
public class IniFile {
    private final Pattern mSection = Pattern.compile("\\s*\\[([^]]*)]\\s*");
    private final Pattern mKeyValue = Pattern.compile("\\s*([^=]*)=(.*)");
    public final Map<String, Map<String, String>> mEntries = new HashMap<>();
    private BufferedWriter mBufferedWriter;

    public void load(String path) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line;
            String section = null;

            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = mSection.matcher(line);

                if (matcher.matches()) {
                    section = Objects.requireNonNull(matcher.group(1)).trim();
                } else if (section != null) {
                    matcher = mKeyValue.matcher(line);

                    if (matcher.matches()) {
                        String key = Objects.requireNonNull(matcher.group(1)).trim();
                        String value = Objects.requireNonNull(matcher.group(2)).trim();

                        Map<String, String> keyValue = mEntries.computeIfAbsent(section, k -> new HashMap<>());

                        keyValue.put(key, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public String get(String section, String key) {
        Map<String, String> keyValue = mEntries.get(section);

        if (keyValue == null) {
            return null;
        }

        return keyValue.get(key);
    }

    public void CreateWriter(String path) throws IOException {
        FileWriter writer = new FileWriter(path);

        mBufferedWriter = new BufferedWriter(writer);
    }

    public void WriteSection(String section) throws IOException {
        mBufferedWriter.write("[");
        mBufferedWriter.write(section);
        mBufferedWriter.write("]");
        mBufferedWriter.newLine();
    }

    public void CloseWriter() throws IOException {
        mBufferedWriter.close();
    }

    public void WriteKeyAndValue(String key, String value) throws IOException {
        mBufferedWriter.write(key);
        mBufferedWriter.write("=");
        mBufferedWriter.write(value);
        mBufferedWriter.newLine();
    }

    @SuppressWarnings("unused")
    public void removeKey(String section, String key) {
        Map<String, String> keyValue = mEntries.get(section);

        if (keyValue == null) {
            return;
        }

        keyValue.remove(key);
    }
}