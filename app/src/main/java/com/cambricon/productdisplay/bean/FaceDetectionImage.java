package com.cambricon.productdisplay.bean;

/**
 * Created by cambricon on 18-3-13.
 */

public class FaceDetectionImage {

    private String name;
    private String fps;
    private String time;

    public FaceDetectionImage() {
    }

    public FaceDetectionImage(String name, String fps, String time) {
        this.name = name;
        this.fps = fps;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return name + ";" + fps + ";" + time;
    }
}
