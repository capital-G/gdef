//
//  gDef.cpp
//  24h-to-take
//
//  Created by Dennis Scheiba on 02.08.22.
//

#include "gDef.hpp"
#include <stdio.h>
#include <ofMain.h>

Gdef::Gdef(){};

void Gdef::setup()
{
    receiver.setup(oscPort);
}

void Gdef::update()
{
    updateNetwork();
}

vector<GdefItem *> Gdef::allItems()
{
    vector<GdefItem *> itemPtrs;

    for (auto &x : spheres)
    {
        itemPtrs.push_back(&x.second);
    }
    for (auto &x : cones)
    {
        itemPtrs.push_back(&x.second);
    }
    return itemPtrs;
}

std::map<std::string, GdefItem *> Gdef::allItemsDict()
{
    map<std::string, GdefItem *> ptrDict;
    for (auto &x : spheres)
    {
        ptrDict.insert({x.first, &x.second});
    };
    for (auto &x : cones)
    {
        ptrDict.insert({x.first, &x.second});
    }
    return ptrDict;
}

void Gdef::draw()
{
    ofBackground(backgroundColor);

    for (auto &x : spheres)
    {
        x.second.draw();
    }

    for (auto &x : cones)
    {
        x.second.draw();
    }
}

GdefItem *Gdef::getItem(std::string name)
{
    // what happens if we do not find a string?
    //    if(allItemsDict().find(name) != allItemsDict().end()) {
    //        return allItemsDict().at(name);
    //    }
    if (spheres.find(name) != spheres.end())
    {
        return &spheres.at(name);
    }
    if (cones.find(name) != cones.end())
    {
        return &cones.at(name);
    }
    throw "could not find item ";
}

void Gdef::updateNetwork()
{
    while (receiver.hasWaitingMessages())
    {
        ofxOscMessage m;
        receiver.getNextMessage(m);
        string oscAddress = m.getAddress();
        if (oscAddress == "/gdef/background")
        {
            float alpha = 255.0;
            try
            {
                alpha = m.getArgAsFloat(3);
            }
            catch (...)
            {
            };
            backgroundColor.set(
                m.getArgAsFloat(0),
                m.getArgAsFloat(1),
                m.getArgAsFloat(2),
                alpha);
        }
        if (oscAddress == "/gdef/update")
        {
            string itemName = m.getArgAsString(0);
            string itemType = m.getArgAsString(1);
            if (itemType == "sphere")
            {
                if (spheres.find(itemName) == spheres.end())
                {
                    GdefSphere sphere = GdefSphere();
                    of3dPrimitive *foo = &(sphere.sphere);
                    sphere.item = foo;
                    spheres.insert({itemName, sphere});
                }
                spheres.at(itemName).parseOsc(m);
            }
            if (itemType == "cone")
            {
                if (cones.find(itemName) == cones.end())
                {
                    GdefCone cone = GdefCone();
                    cone.item = &cone.cone;
                    cones.insert({itemName, cone});
                }
                cones.at(itemName).parseOsc(m);
            }
        }

        if (oscAddress == "/gdef/color")
        {
            string itemName = m.getArgAsString(0);
            float alpha = 255.0;
            try
            {
                alpha = m.getArgAsFloat(4);
            }
            catch (...)
            {
            };
            try
            {
                auto item = getItem(itemName);
                item->color.set(
                    m.getArgAsFloat(1), m.getArgAsFloat(2), m.getArgAsFloat(3), alpha);
            }
            catch (const char *msg)
            {
                ofLog(OF_LOG_ERROR, msg);
            }
        }

        if (oscAddress == "/gdef/thickness")
        {
            string itemName = m.getArgAsString(0);
            if (spheres.find(itemName) != spheres.end())
            {
                spheres.find(itemName)->second.thickness = m.getArgAsFloat(1);
            };
        }

        if (oscAddress == "/gdef/rotate")
        {
            string itemName = m.getArgAsString(0);
            if (spheres.find(itemName) != spheres.end())
            {
                spheres.find(itemName)->second.sphere.rollDeg(m.getArgAsFloat(1));
            };
            if (cones.find(itemName) != cones.end())
            {
                cones.find(itemName)->second.cone.rollDeg(m.getArgAsFloat(1));
            }

            //            try {
            //                auto item = getItem(itemName);
            //                ofLog(OF_LOG_ERROR, "rotate is " + ofToString(m.getArgAsFloat(1)));
            //                item->item->rollDeg(m.getArgAsFloat(1));
            //            } catch (const char* msg) {
            //                ofLog(OF_LOG_ERROR, msg);
            //            }
        }

        if (oscAddress == "/gdef/clear")
        {
            string itemName = m.getArgAsString(0);

            if (spheres.find(itemName) != spheres.end())
            {
                spheres.erase(itemName);
            }

            // todo erase cones
        }

        if (oscAddress == "/gdef/reset")
        {
            spheres.clear();
            cones.clear();
        }
    }
}

void GdefItem::draw()
{
    ofSetColor(color);
    item->drawWireframe();
}

void GdefItem::update() {}

void GdefItem::parseOsc(ofxOscMessage &message) {}

void GdefSphere::parseOsc(ofxOscMessage &message)
{
    x = message.getArgAsFloat(2);
    y = message.getArgAsFloat(3);
    r = message.getArgAsFloat(4);
    sphere.setRadius(r);
    sphere.setPosition(x, y, 100.0);
};

void GdefSphere::draw()
{
    ofSetColor(color);
    ofSetLineWidth(thickness);
    sphere.drawWireframe();
}

void GdefCone::parseOsc(ofxOscMessage &message)
{
    cone.setPosition(message.getArgAsFloat(2), message.getArgAsFloat(3), message.getArgAsFloat(4));
    cone.set(message.getArgAsFloat(5), message.getArgAsFloat(6));
}

void GdefCone::draw()
{
    ofSetColor(color);
    cone.drawWireframe();
}
