FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
CMD ["/bin/sh", "-c", "java -cp $(lein classpath) clojure.main -m cms.core"]

