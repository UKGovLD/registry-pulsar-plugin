# Registry Pulsar Plugin
Pulsar notification plugin for the [Linked Data Registry](https://github.com/UKGovLD/registry-core).

# Installation
To build the project, run `mvn clean package`.
This will produce a set of JAR files in the `target` directory.

To install the plugin **without dependencies**, add the `registry-pulsar-plugin-{version}.jar` JAR
to your LD registry deployment.
In Tomcat, you can do this by adding the JAR and any missing dependencies to the `WEB-INF/lib` directory of the registry webapp.
The full set of dependencies can be found in the `target/lib` directory after packaging. 

To install the plugin **with dependencies**, add the `registry-pulsar-plugin-{version}-jar-with-dependencies.jar` JAR
to the `WEB-INF/lib` directory of the registry webapp.

Alternatively, you can create a new web app which extends the registry with the plugin functionality.
You should build the new WAR using a Maven project which has the `registry-core` WAR, classes
and `registry-pulsar-plugin` artifacts as dependencies.
See [here](https://github.com/epimorphics/registry-pulsar-ext) for an example of such a project.
 
#### Example
```
<dependency>
    <groupId>com.github.ukgovld</groupId>
    <artifactId>registry-core</artifactId>
    <version>2.3.15</version>
    <type>war</type>
</dependency>
<dependency>
    <groupId>com.github.ukgovld</groupId>
    <artifactId>registry-core</artifactId>
    <version>2.3.15</version>
    <classifier>classes</classifier>
</dependency>
<dependency>
    <groupId>com.github.ukgovld</groupId>
    <artifactId>registry-pulsar-plugin</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

The Pulsar plugin is configured by the registry's `app.conf` configuration file.
The [PulsarNotificationAgent](https://github.com/UKGovLD/registry-pulsar-plugin/blob/master/src/main/java/com/epimorphics/registry/notification/PulsarNotificationAgent.java)
class implements the [NotificationAgent](https://github.com/UKGovLD/registry-core/blob/master/src/main/java/com/epimorphics/registry/notification/NotificationAgent.java)
interface,
and can be supplied to the `agent` property on the standard [RegistryMonitor](https://github.com/UKGovLD/registry-core/blob/master/src/main/java/com/epimorphics/registry/notification/RegistryMonitor.java)
instance defined in the configuration file,
in the same way as the registry's built-in notification agents.

See [here](https://github.com/UKGovLD/registry-core/wiki/Notification) for documentation on how to configure
the standard monitoring and notification components.

### Pulsar Configuration

You can configure the Pulsar connection by defining a [ClientConfigurationData](http://pulsar.apache.org/api/client/2.2.0/index.html?org/apache/pulsar/client/impl/conf/ClientConfigurationData.html)
instance in your `app.conf`.
This must specify the `serviceUrl` at least, but all of the properties listed in the Javadoc are supported.

To authenticate the registry client with your Pulsar server,
use the `authPluginClassName` and `authParams` properties.
The values of these properties will depend on your choice of authentication system.
Use these [Java examples]( https://pulsar.apache.org/docs/en/client-libraries-java/#authentication) as a guide.

#### Example
```
pulsarConfig = org.apache.pulsar.client.impl.conf.ClientConfigurationData
pulsarConfig.serviceUrl = pulsar://localhost:6650

pulsar = com.epimorphics.registry.notification.PulsarNotificationAgent
pulsar.config = $pulsarConfig

monitorConfig = com.epimorphics.registry.notification.MonitorRegister
monitorConfig.defaultTopic = ldregistry

monitor = com.epimorphics.registry.notification.RegistryMonitor
monitor.agent = $pulsar
monitor.config = $monitorConfig
```