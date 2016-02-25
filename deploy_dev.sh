#!/usr/bin/env bash

curl \
-F "status=2" \
-F "notify=1" \
-F "ipa=@$CIRCLE_ARTIFACTS/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: e07db807b6344a6d8e65f1c5a8c0b4eb" \
https://rink.hockeyapp.net/api/2/apps/29d2a836383d4fda977697a8754fc5de/app_versions/upload