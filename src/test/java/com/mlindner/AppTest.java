package com.mlindner;

import org.junit.Assert;
import org.junit.Test;

public class AppTest {

    private static final String ROOT_PATH = "test";

    @Test
    public void iterateOverFiles() {
    }

    @Test
    public void processFile() {
    }

    @Test
    public void getRootPath() {
        App app = new App();
        app.setRootPath(ROOT_PATH);
        Assert.assertEquals(ROOT_PATH, app.getRootPath());
    }

    @Test
    public void setRootPath() {
    }
}