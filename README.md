jekyll2cms converts markdown files into xml in order to export [Jekyll blogs](https://jekyllrb.com/) to content management systems. It is delivered as a docker image, which you can easily use in your blog.

# Usage

jekyll2cms requires a Jekyll repository on GitHub.
This repository will be used to convert its markdown files into xml files using the xml-template files in this repository.
Repositories must adhere to structural requirements.
Make sure to check out the [adesso SE devblog](https://github.com/adessoAG/devblog) as an example.
- The markdown files to be converted must be in the `_posts` folder.
- The generated xml files will be located in `assets/first-spirit-xml`.
- The format of the generated xml files will be determined by `_layouts/post-xml.xml` . 

## How to execute

Running jekyll2cms requires some environment variables that must be passed to the `docker run` command or be defined as [GitHub Secrets](https://help.github.com/en/actions/configuring-and-managing-workflows/creating-and-storing-encrypted-secrets):

| Name | Description |
| ------------- | ------------- |
| REPOSITORY_LOCAL_USER_NAME | The GitHub username of the user who will commit the generated xml files to the repository. This user needs to have at least write access to the repository. If the branches are protected, this user needs admin access. |
| REPOSITORY_LOCAL_USER_PASSWORD | The password of this user. It is needed to provide the credentials to allow the commit. Sinse version 2.2.0 this is not needed anymore |
| REPOSITORY_LOCAL_USER_TOKEN | The access token of this user. It is needed to provide the credentials to allow the commit. This is new in version 2.2.0 |
| REPOSITORY_LOCAL_USER_MAIL | The email of this user. This is needed to provide further commit information. |
| REPOSITORY_REMOTE_URL | The URL of the repository which contains the markdown files. This is also the location where the generated xml files are pushed to. |

### Executing localy
Run jekyll2cms on your machine using the following command:

```
docker run -e REPOSITORY_REMOTE_URL=<repository_url> -e REPOSITORY_LOCAL_USER_NAME=<github_username> -e REPOSITORY_LOCAL_USER_MAIL=<github_email> -e REPOSITORY_LOCAL_USER_PASSWORD=<github_password> jekyll2cms/jekyll2cms:<tag>
```

### Automatic execution on GitHub
It is also possible to use jekyll2cms as a GitHub Action - Create a [workflow ](https://help.github.com/en/actions/configuring-and-managing-workflows/configuring-and-managing-workflow-files-and-runs) and copy the following steps. We recommend using GitHub Secrets to provide the configuration.

```
 - name: Pull Docker image
   run: docker pull <your_docker_hub_user>/<your_docker_hub_image>:<tag>

 - name: Run Docker image
   run: docker run -e REPOSITORY_REMOTE_URL='${{ secrets.REPOSITORY_REMOTE_URL }}' -e REPOSITORY_LOCAL_USER_NAME='${{ secrets.REPOSITORY_LOCAL_USER_NAME }}' -e REPOSITORY_LOCAL_USER_MAIL='${{ secrets.REPOSITORY_LOCAL_USER_MAIL }}' -e REPOSITORY_LOCAL_USER_PASSWORD='${{ secrets.REPOSITORY_LOCAL_USER_PASSWORD }}' <your_docker_hub_user>/<your_docker_hub_image>:<tag>
```

# Program sequence

jekyll2cms is a Java Spring Boot application. The execution is separated into four steps.

* Check configuration
* Clone repository
* Execute `jekyll build` and copy generated xml files to specified folder 
* Commit and push generated files to remote repository

jekyll2cms will first check if the configuration and the associated environment variables are passed correctly.
The target repository will then be cloned.
`jekyll build` is executed and generates the xml files in the `_site` directory.
The generated xml files are moved from `_site` to `assets/first-spirit-xml`.
Images used in the markdown files are copied to` assets/images`.

jekyll2cms then checks if any files have changed.
This is done using `git status` in JGit.
Any changes are committed and pushed to the remote repository using a commit message that specifies the changes.
The process is finished without commits if no changes were detected.
jekyll2cms then exits.

## Program exit 

jekyll2cms will exit successfuly if 
- the generated xml files were pushed successfuly
- or if a commit doesn't containt new changes.

jekyll2cms will exit with code 0 in both cases.

### Error codes

jekyll2cms can fail during execution.
Reasons might include misconfiguration or errors in Jekyll.
We have defined error codes to make debugging easier. 

| Exit code | Description |
| ------------- | ------------- |
| 01 | An error ocurred that is not handled by jekyll2cms. |
| 10 | Environment variable REPOSITORY_REMOTE_URL not found. |
| 11 | Environment variable REPOSITORY_LOCAL_USER_NAME not found. |
| 12 | Environment variable REPOSITORY_LOCAL_USER_MAIL not found. |
| 13 | Environment variable REPOSITORY_LOCAL_USER_PASSWORD not found. |
| 14 | Environment variable REPOSITORY_LOCAL_USER_TOKEN not found. |
| 20 | Error while cloning remote repository. |
| 30 | Execution of `jekyll build` returned a non zero exit code. |
| 31 | Couldn't generate `jekyll build`. Execution directory not found. |
| 32 | Error calling generated `jekyll build` command. This is not an error with jekyll. |
| 33 | Error assembling files to copy. |
| 34 | Error copying genereated files. |
| 35 | Error copying images. |
| 40 | Error pushing files to remote repository. |

# Developing
Feel free to fork and adapt jekyll2cms to your needs. We've prepared a short guide to help you get started.

## Prerequisites

Make sure that the following components are installed correctly on your system:

* Java Development Kit (JDK) (v1.8 or higher) [Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Docker [Download](https://www.docker.com/get-started)

## Running jekyll2cms on your local machine

We highly recommend to always execute jekyll2cms using docker. This prevents errors due to missing environment variables or different path separators. However you can also use the jar to execute it. 

### Run with Docker
To run your local changes using docker follow these steps:

1. Rebuild your app with `gradlew build`
2. Run `docker build -t jekyll2cms` to build a new image
3. Use the `docker run` command as specified in [Executing localy](#executing-localy)

# Releases
On every commit on the master branch a new release for jekyll2cms is built using GitHub actions.
The version number is defined in the docker-build-and-push.yml workflow file. 
If it is not changed the current image on docker hub is overwritten.

# Questions?
We do not provide any official support for this software. You can however create an issue in this repository if you have any questions, feedback or technical difficulties. You can also reach us at devblog[replace with the at-sign]adesso.de. 
