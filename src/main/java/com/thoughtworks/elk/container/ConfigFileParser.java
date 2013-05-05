package com.thoughtworks.elk.container;

import com.thoughtworks.elk.container.exception.ElkParseException;
import com.thoughtworks.elk.injection.ConstructorInjection;
import com.thoughtworks.elk.injection.Injection;
import com.thoughtworks.elk.injection.SetterInjection;

import java.util.HashSet;
import java.util.Scanner;

import static com.google.common.collect.Sets.newHashSet;

public class ConfigFileParser {
    private String injection;
    private final Scanner scanner;

    public ConfigFileParser(String configFilePath) {
        scanner = new Scanner(this.getClass().getClassLoader().getResourceAsStream(configFilePath));
        if (scanner.hasNext()) {
            injection = scanner.next();
        }
    }


    public Injection getInjection() {
        if (injection.equals("constructor")) {
            return new ConstructorInjection();
        }
        return new SetterInjection();
    }

    public HashSet<Class> getClassList() {
        HashSet<Class> classes = newHashSet();
        while (scanner.hasNext()) {
            try {
                classes.add(Class.forName(scanner.next()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }
}
