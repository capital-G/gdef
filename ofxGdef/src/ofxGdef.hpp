//
//  gDef.hpp
//  24h-to-take
//
//  Created by Dennis Scheiba on 02.08.22.
//

#pragma once

#include <stdio.h>
#include <ofMain.h>
#include "ofxOsc.h"

class GdefItem
{
public:
    std::string name;
    ofColor color = ofColor::black;
    bool doDraw = true;
    of3dPrimitive *item;
    float thickness = 1.0;

    virtual void draw();
    virtual void update();
    virtual void parseOsc(ofxOscMessage &message);
};

class GdefSphere : public GdefItem
{
public:
    float x;
    float y;
    float r;
    ofSpherePrimitive sphere;

    virtual void parseOsc(ofxOscMessage &message);
    virtual void draw();
};

class GdefCone : public GdefItem
{
public:
    float r;
    float h;
    int rSeg = 100;
    int hSeg = 100;
    ofConePrimitive cone;

    virtual void parseOsc(ofxOscMessage &message);
    virtual void draw();
};

class Gdef
{

public:
    Gdef();
    void setup();
    void update();
    void draw();

    GdefItem *getItem(std::string name);

    ofxOscReceiver receiver;
    int oscPort = 57190;
    ofColor backgroundColor = ofColor::black;

private:
    void updateNetwork();

    // cpp and polymorphism are ???
    std::map<std::string, GdefSphere> spheres;
    std::map<std::string, GdefCone> cones;

    vector<GdefItem *> allItems();
    std::map<std::string, GdefItem *> allItemsDict();
};
