#!/usr/bin/env bash
local APP_ID=$0
local APP_TOKEN=$1

curl \
-F "status=2" \
-F "notify=1" \
-F "ipa=@$CIRCLE_ARTIFACTS/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: $APP_TOKEN" \
https://rink.hockeyapp.net/api/2/apps/$APP_ID/app_versions/upload