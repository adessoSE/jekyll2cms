[![Build Status](https://travis-ci.org/daklassen/jekyll2cms.svg?branch=master)](https://travis-ci.org/daklassen/jekyll2cms) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/687c8b3c2ddc41dbb9ea08f3d0aa829f)](https://www.codacy.com/app/shsanayei/jekyll2cms?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=shsanayei/jekyll2cms&amp;utm_campaign=Badge_Grade)

## Jekyll2cms 

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

### Access database console

For development there is a h2 database configured. The console can be reached after booting up the application at:

```
http://localhost:8080/console
```

The JDBC URL is **jdbc:h2:mem:testdb**.

### Run application

Build a jar file and execute it with help parameter:
```
gradlew build
java -jar build/libs/jekyll2cms-0.0.1.jar -h
```
