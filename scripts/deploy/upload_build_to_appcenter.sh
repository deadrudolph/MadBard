#!bin/bash

cmdAppCenterInstall="npm install --location=global appcenter-cli"

cmdAppCenterUpload="appcenter distribute release --token \"${token}\" --app \"${appName}\" --file \"${file}\""

eval "$cmdAppCenterInstall"

eval "$cmdAppCenterUpload"

