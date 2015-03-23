# JMocap: Getting Started #

JMocap has been developed with NetBeans (version 7.0). The easiest way to download and build it is to get NetBeans and Mercurial on your machine, checkout the sources and open it in NetBeans.

## Mercurial ##

Here are two excellent pages for learning Mecurial (also good for reference):

http://hginit.com

http://hgbook.red-bean.com

Here is a tip: In order to ignore the build and dist directories, simply create a new file called ".hgignore" in the root directory of your jmocap distribution and in it put:

```
syntax: glob
dist/*
build/*
```