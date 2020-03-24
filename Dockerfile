FROM jekyll/builder:3.8.5

RUN echo "Ruby version:" && ruby -v
RUN echo "Gem version:" && gem -v && gem env
RUN echo "Jekyll version:" && jekyll -v
RUN echo "Bundler version:" && bundle -v

RUN apk --update add openjdk8
CMD ["/usr/bin/java", "-version"]
RUN echo "Java Version" java -version

RUN apk add imagemagick

ADD https://github.com/s-gbz/jekyll2cms/releases/download/latest/jekyll2cms-0.0.2.jar /jekyll2cms/jekyll2cms-0.0.1.jar

ENTRYPOINT ["bash","-c","rm -f repo && java -jar /jekyll2cms/jekyll2cms-0.0.1.jar"]
EXPOSE 8080