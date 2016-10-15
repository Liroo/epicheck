#!/bin/bash
if [ -z "$NODE_ENV" ]; then
    export NODE_ENV=docker
fi
cd /src
npm install
if [ "$NODE_ENV" == "dev" ]; then
  echo "Launch in dev mode"
  export NODE_ENV=docker
  npm install -g gulp
  gulp develop
else
  echo "Launch in production mode"
  export NODE_ENV=docker
  npm install pm2 -g
  pm2 start app.js -i 0 --no-daemon
fi
