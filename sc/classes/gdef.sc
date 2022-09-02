/*

SuperCollider  -- OSC --> OpenFrameworks

*/

GdefItem {
	classvar type;

	*initClass {
		type = \item;
	}

	sendUpdate {}
}


GdefColor {
	*black {^[0, 0, 0]}
	*white {^[255, 255, 255]}
}

Gdef {
	// init variables
	var <>type;
	var <>name;
	var <color;
	var thickness;

	var >p;
	var <changed;
	var <colorChanged;
	var <colorMorphRoutine;
	var <rotate;
	var <rotateChanged;

	classvar <>all;
	// openFrameWorks
	classvar <>ofHost;
	classvar <>ofPort;
	classvar ofNet;
	classvar updatePath;
	classvar <>updateRoutine;
	classvar <>fps;

	*initClass {
		all = ();
		ofHost = "localhost";
		// hardcoded b/c of netaddr init
		ofPort = 57190;
		ofNet = NetAddr(ofHost, ofPort);
		fps = 25;
	}

	// todo create a function which sends updates of all items 40 times a second
	// use a has changed flag to reduce message load?

	*new { | name, type, properties, color|
		var res;
		properties = properties ? ();

		res = all.at(name.asSymbol);
		if(res.isNil) {
			"create new gdef".postln;
			res = super.newCopyArgs(
				type,
				name,
			).init;
			all[name.asSymbol] = res;
		};
		if(properties.notNil, {
			properties.pairsDo({|k,v|
				res.p[k] = v;
			});
		});
		res.color = color ? res.color ? Color.black;
		res.sendUpdate;
		^res;
	}

	*startUpdater {
		if(updateRoutine.notNil, {
			updateRoutine.stop;
		});
		updateRoutine = Routine({
			loop({
				all.do({|gdef|
					gdef.sendUpdate;
				});
				fps.reciprocal.wait;
			});
		});
		updateRoutine.play;
	}


	init {
		p = ();
	}

	thickness_ {|newThickness|
		thickness = newThickness;
		ofNet.sendMsg("/gdef/thickness", name, thickness);
	}

	p {
		changed = true;
		^p;
	}

	color_ { |newColor|
		colorChanged = true;
		color = newColor;
	}

	*sphere { |name, properties, color|
		^Gdef(name, \sphere, properties, color);
	}

	*background { |color|
		^Gdef(\background, \background, nil, color);
	}

	rotate_ {|degree|
		rotateChanged = true;
		rotate = degree;
	}

	// todo add morphParameter

	morphColor {|newColor, time=1.0|
		var oldColor = color;
		var routine;
		if(colorMorphRoutine.notNil, {
			colorMorphRoutine.stop;
		});
		colorMorphRoutine =  Routine({
			var steps = time*fps;
			(steps+1).do({|step|
				var mix = step.linlin(0, steps, 0, 1);
				color = Color(
					(oldColor.red*(1-mix)) + (newColor.red*mix),
					(oldColor.green*(1-mix)) + (newColor.green*mix),
					(oldColor.blue*(1-mix)) + (newColor.blue*mix),
					(oldColor.alpha*(1-mix)) + (newColor.alpha*mix),
				);
				colorChanged = true;
				fps.reciprocal.wait;
			});
			"color transition of \"%\" finished".format(name).postln;
			color = newColor;
		});
		colorMorphRoutine.play;
	}

	prSendColor {
		if(colorChanged==true, {
			if(colorChanged == true) {
				if(type==\background, {
					ofNet.sendMsg("/gdef/background", color.red*255.0, color.green*255.0, color.blue*255.0);
				}, {
					ofNet.sendMsg("/gdef/color", name, color.red*255.0, color.green*255.0, color.blue*255.0, color.alpha*255.0);
				});
			};
			colorChanged = false;
		});
	}

	prSendUpdate {
		if(changed!=true, {^this});

		if(type==\sphere) {
			ofNet.sendMsg("/gdef/update", name, "sphere", (p.x ? 100).asFloat, (p.y ? 100.0).asFloat, (p.r ? 0).asFloat);
		};
		changed = false;
	}

	prSendRotate {
		if(rotateChanged!=true, {^this});
		ofNet.sendMsg("/gdef/rotate", name, (rotate?0).asFloat);
		rotateChanged = false;
	}

	sendUpdate {
		this.prSendUpdate();
		this.prSendColor();
		this.prSendRotate();
	}

	*clear {
		// do it this way b/c otherwise some things wont get deleted
		// b/c we delete while we iterate over the array
		all.do({|gdef|
			gdef.clear;
		});
		all = ();
		ofNet.sendMsg("/gdef/reset");
	}

	clear {
		if(colorMorphRoutine.notNil, {
			colorMorphRoutine.stop;
		});
		ofNet.sendMsg("/gdef/clear", name);
		all[name] = nil;
		"cleared %".format(name).postln;
	}
}
