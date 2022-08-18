package com.ruse.world.entity.actor.player.controller;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ControllerHandler {

    private static final Map<String, Class<? extends Controller>> controllers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void initialise() {
        try (ScanResult result = new ClassGraph().whitelistPackages(Controller.class.getPackage().getName() + ".impl").scan()) {
            ClassInfoList list = result.getSubclasses(Controller.class.getName());
            for (ClassInfo info : list) {
                try {
                    controllers.put(info.getSimpleName(), (Class<? extends Controller>) info.loadClass(true));
                } catch (Throwable e) {
                    log.error("Error while loading controller type", e);
                }
            }
        }
    }

    public static Controller getController(String name) {
        Class<? extends Controller> type = controllers.get(name);
        if (type == null) {
            return null;
        }
        try {
            return type.getConstructor().newInstance();
        } catch (Throwable e) {
            log.error("Error while creating a controller instance: {}", name, e);
            return null;
        }
    }
}
