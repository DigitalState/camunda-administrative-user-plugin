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
On camunda engine start, the plugin will search for the `administratorUserName` value.  If the provided username is not found, the plugin will generate a user and relevant group/authorizations.  If the username is found, the plugin will not attempt to create the administrative user, group, or authorizations.

The `administratorUserName` is mandatory is all uses of the plugin.  But the `administratorPassword`, `administratorFirstName`, `administratorLastName`, and `administratorEmail` properties are only mandatory if the `administratorUserName` does not exist in the user accounts when the engine starts.  This means that once the admin account has been created, you do not need to maintain the password in the xml file or relevant `env` variables.

# Support

This plugin is a proof of concept for automating the initial administrative user for Camunda BPM.  If you have additional use case or have problems using the plugin, please open a issue.

