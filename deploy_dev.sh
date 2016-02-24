#!/usr/bin/env bash

curl \
-F "status=2" \
-F "notify=1" \
-F "ipa=@app/build/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: 916232226137de25c68eae94ea3a926f" \
https://rink.hockeyapp.net/api/2/apps/29d2a836383d4fda977697a8754fc5de/app_versions/upload