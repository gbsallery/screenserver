Building
--------

This app requires sbt 0.13. To build for the first time, run:

    bash$ sbt
    > screenServer/assembly
    
...which will produce you an executable Jar file. Run this jar file to support LCD screen rendering.

Running
-------

Log in to AWS, then run the screenServer.sh script. This will start Xvfb (for headless operation) and the ScreenServer.


Installation on AWS (Ubuntu)
============================

Some mojo required. Specifically:

1. Install [Java 8](http://tecadmin.net/install-oracle-java-8-jdk-8-ubuntu-via-ppa/).
2. Install [Jetty (8.1.8.v20121106)](http://pietervogelaar.nl/ubuntu-12-04-install-jetty-9).
3. Install Xvfb (via apt).
4. `apt-get install libxrender1`
5. `apt-get install libxtst6`
6. `apt-get install libxi6`
7. `apt-get install libgtk-3-dev`
8. `apt-get install libswt-gtk-3-java`
9. `apt-get install libxslt1.1`
10. Copy a TrueType font into /usr/share/fonts/ (Palatino would be a good one to choose)
11. `apt-get install libasound-dev` (yes, seriously)
12. Copy `xvfb` script to  /etc/init.d/xvfb
13. `chmod a+x /etc/init.d/xvfb`
14. `ln -s /etc/init.d/xvfb /etc/rc2.d/S99xvfb`
15. `apt-get install service-wrapper`
16. Set up wrapper init script (copy output of `/usr/share/wrapper/make-wrapper-init.sh` to `/usr/local/bin/screenserver.sh`, edit config file)
17. `mkdir /usr/local/conf`
18. `cp /usr/share/wrapper/wrapper.conf /usr/local/conf/screenserver.conf`
19. Copy `screenserver.conf` to `/usr/local/conf/screenserver.conf`. Note that setting this up on Ubuntu was a bitch; use Ubuntu documentation to work out where all the various dependencies have been scattered, then manually update the conf file to point to them. Don't forget to set DISPLAY=:1.

Yes, Docker would probably be a sensible way of handling this in future. If it worked on my machine, that is.