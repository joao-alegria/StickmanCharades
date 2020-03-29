# Stickman Charades - Unity Project

This directory contains the Unity project for the desktop app of "Stickman Charades".
The application is meant to serve as the main interface with our end users.
It allows them to create and manage accounts and play the online version of Charades with other users.

The app has access to users' Orbbec devices connected to their computers and, with the help of NuitrackSDK, we are able to track their movements and map them to virtual characters.

## Instructions on how to run the code

(Under construction)

## Instructions for developers

The directories 'Temp', 'Logs', 'Library' and 'Builds' are ignored by default.
'ProjectSettings' and 'Packages' should not be a concern to you.
Inside the 'Assets' directory, you'll find everything you need to work on the project.

The '_Scenes' dir contains all the playable views of the application, i.e. the menus and the game sessions.

The 'Animations' dir is where all game animations and animators should be placed (e.g. button transitions).

'Fonts' and 'Plugins' contain, as the names state, all of the available fonts and plugins to be used (Android is available, although not meant to be used).

The 'Images' dir should contain all textures, reference images, backgrounds, etc. of the application. 
Photoshop and other image editing files should also be kept here.

The 'Prefabs' dir contains all the prefab objects usable in game.
Unity's Prefab system allows you to create, configure, and store a GameObject complete with all its components, property values, and child GameObjects as a reusable Asset.
The Prefab Asset acts as a template from which you can create new Prefab instances in the Scene.

The 'Scripts' dir is where all the actual functionality of the application lies.
It includes the functions called when actions are perfomed in the app menu, as well as all communications to the server (via REST API and Kafka).

Finally, the 'NuitrackSDK' dir is an imported Unity package containing the SDK used to interact with the Orbbec device.
It supports other depth cameras like Microsoft Azure's Kinect camera, so, in theory, the app should work with these as well.
However, this has not been tested.

