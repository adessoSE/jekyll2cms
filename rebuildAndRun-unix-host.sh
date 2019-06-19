#!/usr/bin/env bash
call gradlew build &&
call docker build --rm -f "Dockerfile" -t jekyll2cms:1.0.6 . &&
call docker run --rm -it  -v $(pwd):/srv/jekyll -p 127.0.0.1:4000:4000 jekyll2cms:1.0.6

