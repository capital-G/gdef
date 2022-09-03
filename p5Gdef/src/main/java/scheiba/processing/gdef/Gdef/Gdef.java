package scheiba.processing.gdef.Gdef;

import java.util.HashMap;
import java.util.Map;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class Gdef {

    public Gdef(PApplet applet) {
        this.applet = applet;
    }

    Map<String, GdefItem> items = new HashMap<String, GdefItem>();
//    List<GdefItem> items = new ArrayList<>();
    GdefColor bgColor = new GdefColor(0.0f, 0.0f, 0.0f, 1.0f);


    PApplet applet;

    OscP5 osc;

    public void setup() {
        osc = new OscP5(this, 57190);
        System.out.println("OSC is live?");
    }

    void oscEvent() {}

    public void update() {
        for (GdefItem item:items.values()) {
            item.update();
        }
    }

    public void draw() {
        applet.background(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        for (GdefItem item:items.values()){
            item.draw();
        }
    }

    static String name(OscMessage oscMessage) {
        return oscMessage.get(0).stringValue();
    }

    public void oscEvent(OscMessage oscMessage) {
        if(oscMessage.checkAddrPattern("/gdef/background")) {
            bgColor.r = oscMessage.get(0).floatValue();
            bgColor.g = oscMessage.get(1).floatValue();
            bgColor.b = oscMessage.get(2).floatValue();
            bgColor.a = oscMessage.get(3).floatValue();
        }

        if(oscMessage.checkAddrPattern("/gdef/update")) {
            String itemName = name(oscMessage);
            String itemType = oscMessage.get(1).stringValue();

            if(itemType.equals("sphere")) {
                GdefSphere sphere;
                if(items.containsKey(itemName)) {
                    // @todo bug when /foo was a Sphere but now something different
                    sphere = (GdefSphere) items.get(itemName);
                } else {
                    sphere = new GdefSphere(this.applet);
                    items.put(itemName, sphere);
                }
                sphere.parseOsc(oscMessage);
            }
        }

        if(oscMessage.checkAddrPattern("/gdef/color")) {
            String itemName = name(oscMessage);
            float alpha = 255.0f;
            try {
                // @todo this can fail?
                alpha = oscMessage.get(4).floatValue();
            } finally {
            }
            GdefItem item = items.get(itemName);
            if(item != null) {
                item.color.r = oscMessage.get(1).floatValue();
                item.color.g = oscMessage.get(2).floatValue();
                item.color.b = oscMessage.get(3).floatValue();
                item.color.a = alpha;
                System.out.println("Changed color");
            }
        }

        // @todo makes this sense w/o wireframes?
        // @todo rotate
        // @todo thickness

        if(oscMessage.checkAddrPattern("/gdef/clear")) {
            String itemName = name(oscMessage);
            items.remove(itemName);
        }

        if(oscMessage.checkAddrPattern("/gdef/clear")) {
            items.clear();
            System.out.println("Cleared all Gdef elements");
        }
    }
}
