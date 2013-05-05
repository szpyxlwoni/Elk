package com.thoughtworks.elk.container.test;

import com.thoughtworks.elk.container.ConfigFileParser;
import com.thoughtworks.elk.injection.ConstructorInjection;
import com.thoughtworks.elk.injection.Injection;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class ConfigFileParserTest {
    @Test
    public void should_get_injection() {
        ConfigFileParser configFileParser = new ConfigFileParser("testConstructorInjection.property");

        Injection injection = configFileParser.getInjection();

        assertThat(injection, is(instanceOf(ConstructorInjection.class)));
    }
}
