jekyll2cms is a tool to convert markdown files to xml files using [jekyll site generator](https://jekyllrb.com/). jekyll2cms comes as a docker image which you can easily deploy to your blog.

# Usage

To use jekyll2cms you need a jekyll repository on GitHub.
This repository contains the markdown files which are the base for generating the xml files and the xml template files.
This jekyll repository needs a specific structure though.
An example for this structure is the [adesso SE devblog](https://github.com/adessoAG/devblog).
The _post folder contains all markdown files which are the base of the generation.
The assets/first-spirit-xml folder contains the generated xml files.
The _layouts/post-xml.xml contains the format of the generated xml files. 

## How to run

To run jekyll2cms, you need to configure some environment variables:

| Name | Description |
| ------------- | ------------- |
| REPOSITORY_LOCAL_USER_NAME | The GitHub username of the user who will commit the generated xml files to the repository. This user needs to have at least write access to the repository. If the branches are protected, this user needs admin access. |
| REPOSITORY_LOCAL_USER_PASSWORD | The password of this user. It is needed to provide the credentials to allow the commit. |
| REPOSITORY_LOCAL_USER_MAIL | The email of this user. This is needed to provide further commit information. |
| REPOSITORY_REMOTE_URL | The URL of the repository which contains the markdown files. This is also the location where the generated xml files are pushed to. |

jekyll2cms can be run using the following command:

```
docker run -e REPOSITORY_REMOTE_URL=<repository_url> -e REPOSITORY_LOCAL_USER_NAME=<github_username> -e REPOSITORY_LOCAL_USER_MAIL=<github_email> -e REPOSITORY_LOCAL_USER_PASSWORD=<github_password> jekyll2cms/jekyll2cms:<tag>
```

It is also possible to exeute jekyll2cms in a GitHub action. The follwing steps are needed for that. We recommend using GitHub secrets to provide the needed configuration.

```
 - name: Pull Docker image
   run: docker pull <your_docker_hub_user>/<your_docker_hub_image>:<tag>

 - name: Run Docker image
   run: docker run -e REPOSITORY_REMOTE_URL='${{ secrets.REPOSITORY_REMOTE_URL }}' -e REPOSITORY_LOCAL_USER_NAME='${{ secrets.REPOSITORY_LOCAL_USER_NAME }}' -e REPOSITORY_LOCAL_USER_MAIL='${{ secrets.REPOSITORY_LOCAL_USER_MAIL }}' -e REPOSITORY_LOCAL_USER_PASSWORD='${{ secrets.REPOSITORY_LOCAL_USER_PASSWORD }}' <your_docker_hub_user>/<your_docker_hub_image>:<tag>
```

## Program sequence

jekyll2cms is a Java Spring Boot program. The execution is separated into for steps.

* Check configuration
* Clone repository
* execute `jekyll build` and copy generated xml files to a specific folder 
* commit and push changed files to remote repository

At first jekyll2cms checks if the configurations using environment variables are done correctly.
Then the configured repository is cloned.
The next step is the execution of the `jekyll build` command. 
This step generates the xml files to a new folder named _site.
jekyll2cms copies every generated xml file from this _site folder to assets/first-spirit-xml.
Also all images which are used in the xml files are copied from the _site folder to assets/images.
As the last step jekyll2cms checks if any files changed.
This is done using git status in JGit.
If there are any changes, these changes are committed and pushed to the repository using a commit message that contains all changes.
If there were no changes, the process is finished and no commits are made.
After this jekyll2cms stops itself.

### Exit program

jekyll2cms will stop after its work is done. This can either be after sucessfully pushing the generated xml files or if there are no changes to commit. In both cases jekyll2cms will exit with exit code 0.

### Error codes

During the execution of jekyll2cms, there can also appear some errors, for example due to misconfiguration or errors in jekyll. We have defined some error codes to make debugging easier. 

| Exit code | Description |
| ------------- | ------------- |
| 01 | An error ocurred that is not handled by jekyll2cms. |
| 10 | Environment variable REPOSITORY_REMOTE_URL not found. |
| 11 | Environment variable REPOSITORY_LOCAL_USER_NAME not found. |
| 12 | Environment variable REPOSITORY_LOCAL_USER_MAIL not found. |
| 13 | Environment variable REPOSITORY_LOCAL_USER_PASSWORD not found. |
| 20 | Error while cloning remote repository. |
| 30 | Execution of `jekyll build` returned a non zero exit code. |
| 31 | Couldn't generate `jekyll build`. Execution directory not found. |
| 32 | Error calling generated `jekyll build` command. This is not an error with jekyll. |
| 33 | Error assembling files to copy. |
| 34 | Error copying genereated files. |
| 35 | Error copying images. |
| 40 | Error pushing files to remote repository. |


# Developing

## Prerequisites

Before you can start the application, make sure that the following components are installed correctly on your local system.

* Java Development Kit (JDK) (v1.8 or higher) [Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Docker [Download](https://www.docker.com/get-started)

## run jekyll2cms on your local machine

We highly recommend to always execute jekyll2cms using docker. This prevents errors due to missing environment variables or different path separators. However you can also use the jar to execute it. 

### Run with Docker
To run your local changes using docker follow these steps:

1. Rebuild your app with `gradlew build`
2. Run `docker build -t jekyll2cms` to build an actual image
3. Run the image locally using the docker command mentioned in [how to run](#how-to-run)

# Releases
On every commit on the master branch a new release for jekyll2cms is built using GitHub actions.
The version number is defined in the docker-build-and-push.yml. 

# Questions?
In general, we do not provide any official support for this software. If you have any questions, feedback or issues, create an issue on GitHub or write a mail to devblog[replace with the at-sign]adesso.de. 
