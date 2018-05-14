# Jekyll2cms - Getting started

The main purpose of this web application is to automatically extract blog posts, defined in markdown-format,  from a **[Github Pages](https://pages.github.com/)** repository and to convert it into an XML-format with the help of **[jekyll](https://jekyllrb.com/)**. This XML format is compatible with the CMS "First-Spirit" from **[E-Spirit](http://www.e-spirit.com/de/)**, a member of the **[adesso Group](https://www.adesso.de)** . The main goal of this project is to provide a developer friendly way (using git and markdown) for submitting blog posts to a CMS based web site.

## Prerequisites

Before you can start the application, make sure that the following components are installed correctly on your local system.

* Git (v2.4 or higher) [Download](https://git-scm.com/downloads)
* Java Development Kit (JDK) (v1.8 or higher)  [Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* jekyll (v3.5.0 or higher)  [Download and getting started](https://jekyllrb.com/)

You can check if the tools are installed correctly by trying to execute them with the version command (e.g. `git --version`)

## How to run jekyll2cms

### Before you start
If all necessary tools are installed, you can checkout *jekyll2cms*, using [this link](https://github.com/adessoAG/jekyll2cms.git) as clone URI. Before starting the application, you first need to configure and build it. You will find a file named `application-sample.properties` in the directory `src/main/resources/`. Rename the file to `application.properties` and open it to insert your local configuration. It is necessary, for example, to define to which local destination the remote repository is cloned to (`repository.local.path`) and, of course, the URL of the remote repository (`repository.remote.url`) which contains the markdown files. The generated XML files will be pushed to the same repository. You also have to specify the destination path for the generated XML files and the login data of a user who is allowed to push on the remote repository. The comments in the sample file should help you doing the configuration right. Note that the remote repository must have the same structure as you require in the `application.properties` (e.g. if you expect the markdown files in a folder `/_posts`, make sure that this folder exists on the remote repository). 

After you have done the configuration, you can build the application. Open a new terminal window in the root directory of the project and run `gradlew build`. The output should be BUILD SUCESSFUL. 

### Run application
Open a new terminal window in the root folder of the project an run 
`java -jar build/libs/jekyll2cms-0.0.1.jar`

### Run with Docker
If you want to run the application with Docker you need to build an Image with the Dockerfile.

To do so: (In the project-directory)

1. Rebuild your app with `gradlew build`
2. Run `docker build -t jekyll2cms:latest` to build an actual image
3. Run the image locally with `docker run -d --name jekyll2cms jekyll2cms:latest`
4. To see the logs use `docker logs jekyll2cms`
5. If you want to deploy it, push your image to a registry ([see the documentation](https://docs.docker.com/engine/reference/commandline/push/))

## How to work with jekyll2cms
After you successfully started the application, it will clone the remote repository. If there is already a local clone, updates will be pulled automatically. The received blog-content in markdown will be transformed into FirstSpirit compatible XML, which is stored in the directory you defined in the `application.properties` file and pushed back to the remote repository. 

Jekyll2cms updates the repository every 10 seconds to check for new blog posts. If the application detects changes in a markdown-file, the XML files will be updated and pushed to the remote repository.  

### Add Blogpost
To add new posts, simply add a file in the directory you configured in the `jekyll.path.posts` property that follows the convention `YYYY-MM-DD-name-of-post.markdown` and includes the necessary [front matter](https://jekyllrb.com/docs/frontmatter/), which should look something like this:

    ---
    # layout is required. DO NOT CHANGE.
    layout: [post, post-xml]
    # title is required. Add the title of your post.
    title:  "adesso AG Blog Post Example"
    # date is required. If possible, also provide a time. e.g. 2017-08-10 10:25:00.
    date:   2017-08-10 10:25:00 
    # If you are modifying an existing post, provide a date for it.
    modified_date:
    # author must be your name used in the _data/authors.yml file.
    author: jondoe
    # Categories are written inside square brackets '[cat1, cat2]' and are separated by commas.
    # add at least one category name.
    categories: [Technologie]
    # Tags are written inside square brackets '[tag1, tag2]' and are separated by commas.
    # tags are optional, but help to narrow down the subject of the blog post
    tags: [Digitalisierung, Banken]
    ---

You can find more examples for blog posts in the [adesso devblog repository](https://github.com/adessoAG/devblog/tree/master/_posts).

For creating or modifying posts, it is recommended not to work on the same local clone as jekyll2cms does to prevent unpredictable errors. If you like,  you can also edit the markdown files directly with the help of the web interface of your desired git service, for example GitHub.

### See XML result
After adding or updating a markdown file and waiting for a couple of seconds, jekyll2cms will detect the changes. The XML file will be created and pushed immediately to the remote repository so that it can be included into the CMS by the end user with the next manual pull.  

## Questions?
In general, we do not provide any official support for this software. If you have any questions, feedback or issues, create an issue on GitHub or write a mail to info[replace with the at-sign]adesso.de and ask for the Open Source Team. 

## License
This software is released under MIT-License. Copyright (c) 2017 adesso AG, 44269 Dortmund


Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


