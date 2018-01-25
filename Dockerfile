FROM jekyll/builder:3.5.2

RUN echo "Ruby version:" && ruby -v
RUN echo "Gem version:" && gem -v && gem env
RUN echo "Jekyll version:" && jekyll -v
RUN echo "Bundler version:" && bundle -v

# Install OpenJdk 8
RUN apk --update add openjdk8-jre
CMD ["/usr/bin/java", "-version"]
RUN echo "Java Version" java -version

# Install ImageMagick
RUN apk add imagemagick

#RUN gem install bundler multipart-post faraday sass pathutil sawyer octokit jekyll-gist mini_magick jekyll-minimagick liquid-md5

ADD build/libs/jekyll2cms-0.0.1.jar jekyll2cms/jekyll2cms-0.0.1.jar
#RUN java -jar jekyll2cms/jekyll2cms-0.0.1.jar
#Start Jekyll2cms when container starts
ENTRYPOINT ["bash","-c","rm -f repo && java -jar jekyll2cms/jekyll2cms-0.0.1.jar"]
EXPOSE 8080