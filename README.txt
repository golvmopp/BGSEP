v1.01 - Virtual Gamepad Final Version

No new features added, only bugfixes.

_____________________________________________________

v1.0  - Virtual Gamepad

Description
A virtual gamepad with three controller layouts simulating NES, GC and PS. Bluetooth is used to connect the Android devices to the host application.

App features

    Buttons
    Analog sticks
    Button vibrations
    Accelerometer based Gyro function

Host features

    User friendly console interface
    Configurable key codes
    Up to 10 players
    Up to 25 buttons per client
    Freeze mode

Requirements

    Android 4.0.3 or later with Bluetooth
    A Linux server with Bluetooth support using Bluez v4 running the console based host application

_____________________________________________________

v0.3 - Third release with working bluetooth communication

We are no longer using Bluetooth Low Energy due to many reasons that will be explained later. Instead, we have a server on the host computer that uses RFCOM for the communication, resulting in support for Android version 4.0.3 and later.

In this version, the working buttons are A, B, X and Y on the GC controller.

To setup the communication:

    Make sure the Android device is paired with the computer only and not with any other device.
    Start the server by executing server.java on the host computer.
    Start the Virtual Gamepad app on the Android device.

We apologize for a delayed release. We had som issues on some devices that we wanted to fix before the release.

_____________________________________________________

v0.2 - Second release with bluetooth services implementation

This version adds complete support for all screen resolutions on the NES controller.

We have also included an initial bluetooth implementation. It is not yet a fully working HID device, although a host computer with an operating system and a bluetooth 4.0 device with LE support does recognize some of our services.

_____________________________________________________

v0.1 - First initial version with GUI

First release with GUI.

    With clickable buttons and a NES-view.

Due to some problems with the bluetooth implementation in Android SDK (in Android version 4.2 they've switched bluetooth stack from 'Bluez' to 'Bluedroid'), we will not include any bluetooth features yet.

Next version will include a bluetooth implementation, probably with a server application for the PC.
