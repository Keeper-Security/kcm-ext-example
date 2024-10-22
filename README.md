kcm-ext-example
===============

**kcm-ext-example** is an example extension for KCM/Guacamole that demonstrates
how arbitrary parameter tokens can be generated and injected at connection
time.

As written, the example injects a token called `EXAMPLE_TOKEN` that contains
the user's username. This can be replaced by essentially any Java code that
ultimately produces a string, and that code can take into account the current
user's identity, the details of their successful authentication attempt that
resulted in their KCM session, etc.

Building the extension
----------------------

You will need Maven and a copy of the JDK to build extensions for
KCM/Guacamole. To build this extension, run:

```console
$ mvn package
```

The result of the build will be a .jar file within the `target/` directory:

```console
$ ls target/
classes            generated-test-sources      maven-archiver  test-classes
generated-sources  kcm-ext-example-2.19.0.jar  maven-status
$
```

This extension can then be used with KCM by copying the .jar file into place:

* If KCM was installed natively (using RPMs), copy the .jar file to
  `/etc/guacamole/extensions/` of your KCM server and restart the web
  application.

* If KCM was installed using `kcm-setup.run` or the Docker images, the .jar
  file should be copied somewhere reasonable on the server and then mounted
  into place using a volume mount. If using `kcm-setup.run` or Docker Compose,
  this will mean modifying your `docker-compose.yml` such that the `guacamole`
  container points at the extension:

  ```yaml
  services:

      guacamole:
          image: keeper/guacamole:2
          restart: unless-stopped
          ...
          volumes:
              ...
              - "/path/to/kcm-ext-example-2.19.0.jar:/etc/guacamole/extensions/kcm-ext-example.jar:ro"

  ```

  To add a new volume mount like this, you will need to recreate the Docker
  container. `kcm-setup.run` will do this for you automatically when you use
  `kcm-setup.run apply`. Docker Compose will do this for you automatically when
  you use `docker-compose up`.
