jekyll2cms is a tool to convert markdown files to xml files using [jekyll site generator](https://jekyllrb.com/). jekyll2cms comes as a docker image which you can easily deploy to your blog.

# usage

## how to run

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
   run: docker pull jekyll2cms/jekyll2cms:<tag>

 - name: Run Docker image
   run: docker run -e REPOSITORY_REMOTE_URL='${{ secrets.REPOSITORY_REMOTE_URL }}' -e REPOSITORY_LOCAL_USER_NAME='${{ secrets.REPOSITORY_LOCAL_USER_NAME }}' -e REPOSITORY_LOCAL_USER_MAIL='${{ secrets.REPOSITORY_LOCAL_USER_MAIL }}' -e REPOSITORY_LOCAL_USER_PASSWORD='${{ secrets.REPOSITORY_LOCAL_USER_PASSWORD }}' jekyll2cms/jekyll2cms:<tag>
```

## program sequence

jekyll2cms has four steps:
* Check configuration
* Clone repository
* execute `jekyll build` and copy generated xml files to a specific folder 
* commit and push changed files to remote repository

### exit program

jekyll2cms will stop after its work is done. This can either be after sucessfully pushing the generated xml files or if there are no changes to commit. In both cases jekyll2cms will exit with exit code 0.

### error codes

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


# developing and contributing

## Prerequisites

Before you can start the application, make sure that the following components are installed correctly on your local system.

* Git (v2.4 or higher) [Download](https://git-scm.com/downloads)
* Java Development Kit (JDK) (v1.8 or higher) [Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Docker [Dwnload](https://www.docker.com/get-started)

## run jekyll2cms on your local machine

We highly recommend to allways execute jekyll2cms using docker. This prevents errors due to missing environment variables or different path separators. However you can also use the jar to execute it. 

### Run with Docker
To run your local changes using docker follow these steps:

1. Rebuild your app with `gradlew build`
2. Run `docker build -t jekyll2cms` to build an actual image
3. Run the image locally using the docker command mentioned in [how to run](#how-to-run)

# Questions?
In general, we do not provide any official support for this software. If you have any questions, feedback or issues, create an issue on GitHub or write a mail to devblog[replace with the at-sign]adesso.de. 
