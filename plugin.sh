#!/bin/sh

NAME=Test
JAR=$NAME.jar

ROOT="$HOME/git/EyeInside/EyeInside"
PLUGIN_DIR="$ROOT/plugins"

SDK_HOME="$HOME/Development/lib/android-sdk-macosx"
TOOLS="$PATH:$SDK_HOME/tools:$SDK_HOME/build-tools/17.0.0:$SDK_HOME/platform-tools"
export PATH=$PATH:$TOOLS


[ -d $PLUGIN_DIR ] && rm $PLUGIN_DIR/* && rm -d $PLUGIN_DIR

mkdir $PLUGIN_DIR
[ -d $PLUGIN_DIR ] && echo "Created $PLUGIN_DIR"


echo Creating $JAR $CLASSES
jar -cvf $PLUGIN_DIR/$JAR -C $ROOT/bin/classes ./com/code4bones/plugins/ 

echo Creating DEX...
dx --dex --output=$PLUGIN_DIR/classes.dex $PLUGIN_DIR/$JAR

echo Adding DEX to bundle
pushd $PLUGIN_DIR
aapt a $JAR classes.dex

echo Deploying to target

TARGET="/mnt/sdcard/data/com.code4bones/plugins/"


adb shell mkdir $TARGET 
adb push $JAR $TARGET
#call D:\devel\prj\lib\Android\android-sdk\platform-tools\adb.exe push %ROOT%/deploy/%jar% /mnt/sdcard/
echo Done
popd

#adb
# echo $PATH
# env