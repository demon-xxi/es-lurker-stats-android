# Lurker Insights for Android [![Build Status](https://img.shields.io/travis/demon-xxi/es-lurker-stats-android.svg?style=flat-square)](https://travis-ci.org/demon-xxi/es-lurker-stats-android)

Android client for [Lurker Insights](http://lurker.esporter.tv) website.

Work in progress. See issues list for the roadmap.

## About 
Lurker Insights API keeps track of all the users watching Twitch channels. 
It records time spent watching channels and games and exposes that statistics.
>Note: As of now streams with less than 1000 active viewers are not recorded.

## Building and Running
Built using graddle with Android Studio 2.1. See [official documentation](http://developer.android.com/sdk/installing/studio-build.html) for details.

## Supported Devices
This code is optimized for v19 (KitKat) and up. Mainly tested on Marshmellow. 

Tablets and Phones or any screen sizes are supported. Optimized for better experience on tablets.

## Dev Details
- Storage: [SnappyDB](https://github.com/nhachicha/SnappyDB) via [RxSnappy](https://github.com/team-supercharge/rxsnappy)
- REST Api Calls: [Retrofit](http://square.github.io/retrofit) via RxJava adapter with [Moshi](https://github.com/square/moshi) serialization
- Components: Official Material Design, no 3rd party
- Image Loader: [Picasso](http://square.github.io/picasso/) for caching and work with RecyclerView
- Oauth: Simple implementation via WebView and Retrofit
- Deep Linking into Twitch app/site as per [instructions](https://github.com/justintv/Twitch-API/blob/master/mobile_deeplinks.md)
- IntentService with RxJava and LocalBroadcastManager for handling async operations and api access  

## Demo
Click image below to play video.

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/ZNFXApm_w0g/0.jpg)](http://www.youtube.com/watch?v=ZNFXApm_w0g)
