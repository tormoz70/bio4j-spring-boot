package ru.bio4j.spring.commons.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class SerializersTest {
    @Test
    public void testBigDecimalSerializer() {
        BigDecimalDataObject dataObj = new BigDecimalDataObject();
        dataObj.setBdProp3(BigDecimal.valueOf(10.0 / 3));
        dataObj.setBdProp0(BigDecimal.valueOf(10.0 / 3));
        dataObj.setBdProp_2(BigDecimal.valueOf(10.0 / 3));
        dataObj.setBdProp3Exact(BigDecimal.valueOf(10.0 / 4));
        dataObj.setBdProp3Min1(BigDecimal.valueOf(10.0 / 2));
        try {
            String json = new ObjectMapper().writeValueAsString(dataObj);
            String jsonRef = loadJSON();
            Assert.assertEquals(jsonRef, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDoubleSerializer() {
        DoubleDataObject dataObj = new DoubleDataObject();
        dataObj.setBdProp3(10.0 / 3);
        dataObj.setBdProp0(10.0 / 3);
        dataObj.setBdProp_2(10.0 / 3);
        dataObj.setBdProp3Exact(10.0 / 4);
        dataObj.setBdProp3Min1(10.0 / 2);
        try {
            String json = new ObjectMapper().writeValueAsString(dataObj);
            String jsonRef = loadJSON();
            Assert.assertEquals(jsonRef, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private String loadJSON() {
        String fileName = "bdser.json";
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            StringBuilder contents = new StringBuilder();
            while ((line = reader.readLine()) != null)
                contents.append(line);
            return contents.toString();
        } catch (NullPointerException e) {
            Assert.fail(String.format("Файл %s не найден!", fileName));
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
            return null;
        }
    }
}
