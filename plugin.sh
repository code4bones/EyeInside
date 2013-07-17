#!/bin/sh

NAME=Test
JAR=$NAME.jar

ROOT="$HOME/Development/src/Android/EyeInside"
PLUGIN_DIR="$ROOT/plugins"
TARGET="/mnt/sdcard/data/com.code4bones.EyeInside/plugins/"


SDK_HOME="$HOME/Development/lib/android-sdk-macosx"
TOOLS="$PATH:$SDK_HOME/tools:$SDK_HOME/build-tools/17.0.0:$SDK_HOME/platform-tools"
export PATH=$PATH:$TOOLS


if [ -e $PLUGIN_DIR ];then 
	rm $PLUGIN_DIR/*
else
	mkdir $PLUGIN_DIR
fi


echo Creating $JAR $CLASSES
jar -cvf $PLUGIN_DIR/$JAR -C $ROOT/bin/classes ./com/code4bones/plugins/ 

echo Creating DEX...
dx --dex --output=$PLUGIN_DIR/classes.dex $PLUGIN_DIR/$JAR

echo Adding DEX to bundle
pushd $PLUGIN_DIR
aapt a $JAR classes.dex && rm classes.dex

echo Deploying to target



adb shell mkdir $TARGET 
adb push $JAR $TARGET
echo Done
popd

