
#Please execute 'gradlew build' before running this image, cause it takes the build jar and runs it
FROM wrlennon/alpine-java8-jdk

RUN apk upgrade --update \
 && apk add git imagemagick \
        libatomic readline readline-dev libxml2 libxml2-dev \
        ncurses-terminfo-base ncurses-terminfo \
        libxslt libxslt-dev zlib-dev zlib \
        ruby ruby-dev yaml yaml-dev \
        libffi-dev build-base git nodejs \
        ruby-io-console ruby-irb ruby-json ruby-rake \
 && gem install --no-document redcarpet kramdown maruku rdiscount RedCloth liquid pygments.rb \
 && gem install --no-document sass safe_yaml \    
 && gem install --no-document bundler \    
 && gem install --no-document jekyll -v 3.5.2 \
 && gem install --no-document jekyll-gist \
 && gem install --no-document jekyll-minimagick \
 && gem install --no-document liquid-md5 \
 && gem install --no-document rouge \
 && gem install --no-document jekyll-paginate jekyll-sass-converter \
 && gem install --no-document jekyll-sitemap jekyll-feed jekyll-redirect-from \
 && rm -rf /root/src /tmp/* /usr/share/man /var/cache/apk/* \
 && apk del build-base zlib-dev ruby-dev readline-dev \
            yaml-dev libffi-dev libxml2-dev \
 && apk search --update
 
#RUN git clone -b master https://70aae7a6c44003f1f9acd1f84bc978d57708b000:x-oauth-basic@github.com/silasmahler/devblog.git ./src/jekyll
#RUN cd ./src/jekyll
#RUN bundle install

RUN echo 'Hi' && ruby -v && java -version && gem -v && jekyll -v && git --version
RUN jekyll -help
ADD build/libs/jekyll2cms-0.0.1.jar jekyll2cms/jekyll2cms-0.0.1.jar

# Run Image while building -- for testing
RUN java -jar jekyll2cms/jekyll2cms-0.0.1.jar

#Start Jekyll2cms when container starts
#ENTRYPOINT ["java" "-jar", "jekyll2cms/jekyll2cms-0.0.1.jar"]

EXPOSE 8080


#RUN git clone -b master https://70aae7a6c44003f1f9acd1f84bc978d57708b000:x-oauth-basic@github.com/silasmahler/devblog.git ./src/jekyll
#RUN cd /src/jekyll && ls -a
#RUN bundle exec jekyll build
#RUN bash -c "bundle install"

#RUN bash -c "git clone -b master https://70aae7a6c44003f1f9acd1f84bc978d57708b000:x-oauth-basic@github.com/silasmahler/devblog.git ./src/jekyll " 
#RUN bash -c "cd /src/jekyll && ls -a " 
#RUN bash -c "bundle exec jekyll build" 
#RUN bash -c "bundle install"