#!/usr/bin/env bash

APP_ID=$0
APP_TOKEN=$1

echo "Sending binary to hockeyapp (app_id=$APP_ID , app_token=$APP_TOKEN)"

curl \
-F "status=2" \
-F "notify=1" \
-F "ipa=@$CIRCLE_ARTIFACTS/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: $APP_TOKEN" \
https://rink.hockeyapp.net/api/2/apps/$APP_ID/app_versions/upload