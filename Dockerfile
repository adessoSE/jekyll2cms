FROM jekyll/builder:3.8.5 as builder

RUN echo "Ruby version:" && ruby -v
RUN echo "Gem version:" && gem -v && gem env
RUN echo "Jekyll version:" && jekyll -v
RUN echo "Bundler version:" && bundle -v
RUN apk --update add openjdk8
CMD ["/usr/bin/java", "-version"]
RUN echo "Java Version" java -version
RUN apk add imagemagick

FROM builder as jar

ADD build/libs/jekyll2cms-2.0.0.jar /jekyll2cms/jekyll2cms-2.0.0.jar
ENTRYPOINT ["java","-jar","/jekyll2cms/jekyll2cms-2.0.0.jar"]