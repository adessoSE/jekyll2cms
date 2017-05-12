## Jekyll2cms [![Build Status](https://travis-ci.org/daklassen/jekyll2cms.svg?branch=master)](https://travis-ci.org/daklassen/jekyll2cms)

The main purpose of this project is to create a **CLI application** that allows to automatically extract posts from a **[Github Pages](https://pages.github.com/)** repository into a CMS. Within the application we want to generate HTML from markdown files the help of [**jekyll**](https://jekyllrb.com/). The main reason for doing this, is to provide a developer friendly way (using pure git) for submitting blog posts to a CMS based web site.

### Quickstart Intellij

Clone this repo:
```
git clone https://github.com/daklassen/jekyll2cms
```
Change the directory:
```
cd jekyll2cms
```
Build the Intellij project with gradle:
```
gradlew cleanidea idea
```
Now you can open the generated Intellij project and start working on the projekt.

### Run application

Build a jar file and execute it with help parameter:
```
gradlew build
java -jar build/libs/jekyll2cms-0.0.1.jar -h
```