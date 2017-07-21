## Jekyll2cms 

The main purpose of this project is to create a simple application which is intended to run on a web server.  It allows to automatically extract posts from a **[Github Pages](https://pages.github.com/)** repository into an XML-format, that can be read by the CMS "First-Spirit". Within the application HTML-files are generated from markdown files the help of [**jekyll**](https://jekyllrb.com/). The main purpose of this project is to provide a developer friendly way (using pure git) for submitting blog posts to a CMS based web site.




### Run application

Build a jar file and execute it:
```
gradlew build
java -jar build/libs/jekyll2cms-0.0.1.jar
```