FROM jekyll/builder

RUN apk --update add openjdk8
CMD ["/usr/bin/java", "-version"]

RUN echo 'Hi' && ruby -v && java -version && gem -v && jekyll -v && git --version && bundle -v

ADD build/libs/jekyll2cms-0.0.1.jar jekyll2cms/jekyll2cms-0.0.1.jar
RUN java -jar jekyll2cms/jekyll2cms-0.0.1.jar
#Start Jekyll2cms when container starts
#ENTRYPOINT ["java" "-jar", "jekyll2cms/jekyll2cms-0.0.1.jar"]
EXPOSE 8080