#!/usr/bin/env bash

java -server -Xms512m -Xmx512m -XX:+UseG1GC -Dclojure.server.repl="{:port 8310 :accept clojure.core.server/repl}" -jar supervision-1.0.0-standalone.jar 2>&1 >&- &
