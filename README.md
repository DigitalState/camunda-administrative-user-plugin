# Camunda Administrative User Plugin

This plugin generates a single user, a single group with administrative permissions for all resources and adds the user to the group.

This plugin is designed for use cases were you want to load the Camunda Docker image without the Camunda-invoice app and still have a default administrative user in the system.

# Configuration

In the `bpm-platform.xml` file add the following to the `<plugins>` section:

```xml
<plugin>
    <class>io.digitalstate.camunda.plugins.AdministrativeUserPlugin</class>
    <properties>
        <property name="administratorUserName">admin</property>
        <property name="administratorPassword">admin</property>
        <property name="administratorFirstName">Steve</property>
        <property name="administratorLastName">TheAdmin</property>
        <property name="administratorEmail">myEmail@email.com</property>
    </properties>
</plugin>
```
On Camunda engine startup, the plugin will search for the `administratorUserName` value in the list of Camunda usernames.  If the provided username is **not** found, the plugin will generate a administrative user and relevant group/authorizations.  If the username **is** found, the plugin will not attempt to create the administrative user, group, or authorizations.

The `administratorUserName` is mandatory in all uses of the plugin.  The `administratorPassword`, `administratorFirstName`, `administratorLastName`, and `administratorEmail` properties are only mandatory if the `administratorUserName` does not exist in the user accounts when the engine starts.  THe reason for this is, once the administrative account has been created, you do not need to maintain the password in the xml file or relevant `env` variables.

# Support

This plugin is a proof of concept for automating the initial administrative user for Camunda BPM.  If you have additional use case or have problems using the plugin, please open a issue.

# Installing the Plugin with Docker

The plugin can easily be installed with Docker:

Your dockerfile would look like the following:

```dockerfile
FROM camunda/camunda-bpm-platform:tomcat-7.8.0

# Remove the camunda-inovice app which will result in no users being loaded
RUN rm -r webapps/camunda-invoice

# add bpm-platform.xml file into the Camunda Conf folder.
# The bpm-platform.xml file has the Administrative User Plugin's configuration
COPY bpm-platform.xml /camunda/conf/bpm-platform.xml

# Copy Administrative User Plugin into the Camunda Lib folder
COPY camunda.administrativeuser.plugin-0.1.0-SNAPSHOT.jar /camunda/lib/camunda.administrativeuser.plugin-0.1.0-SNAPSHOT.jar
```

You could have a docker-compose file such as:

```yaml
camunda:
    build: .
    environment:
      - JAVA_OPTS=-Djava.security.egd=file:/dev/./urandom -Duser.timezone=America/Montreal
    ports:
      - "8055:8080"
```

and then run `docker-compose up --build` in the same folder as the docker-compose and dockerfile.

# Build the Plguin

To build the plugin in the root of this repo, then `mvn clean package`

Look in the generated `target` folder for `camunda.administrativeuser.plugin-0.0.0-SNAPSHOT.jar` file. Where 0.0.0 will be the current version.

# Download the plugin:

See the [Releases](https://github.com/DigitalState/camunda-administrative-user-plugin/releases)
