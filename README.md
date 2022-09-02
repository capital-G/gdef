# Gdef: Graphical node proxies for SuperCollider

*Gdef* adapts the functionality of an [Ndef](https://doc.sccode.org/Classes/Ndef.html) into the domain of graphics.
It allows you to create graphical objects from within *sclang* and also tie their properties to Ugens that are running on *scsynth*.
*Gdef* handles the communication and abstraction.

Currently it is tied to OpenFrameworks as a graphic engine, but it is also possible
to adapt the API to other graphic frameworks such as Processing, OpenRNDR or Unity.

## Installation

### SuperCollider

Include the `sc` subfolder to the sclang interpreter startup.
Go to *Settings*, *Interpreter*, add the folder under *Include* and restart the interpreter.

### openFrameworks

`ofxGdef` relies on `ofxOsc` for communication with SuperCollider, so make sure you have this dependency added
via e.g. the openFrameworks project generator.
Then simply copy the files into the your project and add them to the compile paths.

Add the `setup`, `update` and `draw` method to your app.

## License

GPL-2.0
