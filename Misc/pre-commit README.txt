To install the pre-commit git hook to make sure the projects compile and that regression tests pass, perform the following instructions:

1. Make sure $JAVA_HOME points to your JDK directory (You might need to add this environment variable in Windows)

2. Add the path of the Android_SDK/tools and Android_SDK/platform-tools to your PATH variable (you might need to use the Android SDK Manager to download these tools if you haven't already did)

3. On Windows, install ant from http://ant.apache.org/bindownload.cgi and add the PATH of the bin directory to the PATH variable

4. You need to create local.properties file which contains the path of your Android SDK directory

Change to the MessageSearch/MessageSearch directory then type in
% android update project -p .

Change to the MessageSearch/MessageSearchTest directory then type in
% android update test-project -m ../MessageSearch -p .

On Windows, these command didn't work in git-bash, but worked in cygwin. You can bypass the above step by create the local.properties file inside both the MessageSearch and MessageSearchTest directory and add the following line inside:

sdk.dir=C:\\Program Files\\Android\\android-sdk

where sdk.dir is the path of your Android SDK directory

5. You can now test the following command in either the MessageSearch and MessageSearchTest directory
% ant clean
% ant debug
% ant test

6. If this all works, you can copy the pre-commit file inside your .git/hooks directory. On Mac/Linux, I believe you have to chmod the file to make it runnable

Optional: I commented some code in the pre-commit script that auto starts the emulator if you want to use it. It should work fine with command line git commits. If you use like Git-GUI on Windows, it doesn't display any messages until you close the emulator.
