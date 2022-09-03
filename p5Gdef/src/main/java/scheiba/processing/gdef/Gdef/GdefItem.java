package scheiba.processing.gdef.Gdef;

import oscP5.*;
import processing.core.PApplet;

public class GdefItem {
    String name;
    GdefColor color = new GdefColor(255.0f, 255.0f, 255.0f, 255.0f);
    boolean doDraw;
    float thickness;

    public GdefItem(PApplet applet) {
        this.applet = applet;
    }

    PApplet applet;

    void update() {}

    void draw() {
        applet.fill(color.r, color.g, color.b, color.a);
    }

    void parseOsc(OscMessage oscMessage) {}
}
