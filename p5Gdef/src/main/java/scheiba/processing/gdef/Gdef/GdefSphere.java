package scheiba.processing.gdef.Gdef;

import oscP5.OscMessage;
import processing.core.PApplet;

public class GdefSphere extends GdefItem {
    float x;
    float y;
    float r;

    public GdefSphere(PApplet applet) {
        super(applet);
        this.applet = applet;
    }

    @Override
    void draw() {
        super.draw();
        applet.ellipse(x, y, r, r);
    }

    @Override
    void parseOsc(OscMessage oscMessage) {
        super.parseOsc(oscMessage);
        this.x = oscMessage.get(2).floatValue();
        this.y = oscMessage.get(3).floatValue();
        this.r = oscMessage.get(4).floatValue();
    }
}
