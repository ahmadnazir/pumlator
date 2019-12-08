#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $DIR

lein run /dev/stdin | /usr/bin/java -Djava.awt.headless=true -jar ./bin/plantuml.jar -tpng -charset UTF-8 -p > /tmp/output.png
