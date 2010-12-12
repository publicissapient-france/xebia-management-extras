/*
 * Copyright 2008-2010 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.management.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * Maven project information (groupId, artifactId, version) exposed with JMX.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
@ManagedResource
public class WebApplicationMavenInformation implements ServletContextAware, InitializingBean, SelfNaming, BeanNameAware {

    private static class DirectoryFileFilter implements FileFilter {

        public boolean accept(File file) {
            return file.isDirectory();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationMavenInformation.class);

    private static final String POM_PROPERTY_ARTIFACT_ID = "artifactId";
    private static final String POM_PROPERTY_GROUP_ID = "groupId";
    private static final String POM_PROPERTY_VERSION = "version";

    private String artifactId = "#UNKNOWN#";

    private String beanName;

    private String groupId = "#UNKNOWN#";

    private String jmxDomain = "fr.xebia";

    private ObjectName objectName;

    private ServletContext servletContext;

    private String version = "#UNKNOWN#";

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.servletContext, "'servletConfig' can not be null");

        loadInformationFromPomProperties();
    }

    @ManagedAttribute(description = "Maven ${project.artifactId} property extracted from ${webapp-home}/META-INF/${groupId}/${artifactId}/pom.properties file")
    public String getArtifactId() {
        return artifactId;
    }

    @ManagedAttribute(description = "Maven ${project.groupId}:${project.artifactId}:${project.version} properties extracted from ${webapp-home}/META-INF/${groupId}/${artifactId}/pom.properties file")
    public String getFullyQualifiedArtifactIdentifier() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @ManagedAttribute(description = "Maven ${project.groupId} property extracted from ${webapp-home}/META-INF/${groupId}/${artifactId}/pom.properties file")
    public String getGroupId() {
        return groupId;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            String objectNameString = jmxDomain + ":type=WebApplicationMavenInformation";
            if (StringUtils.hasText(this.beanName)) {
                objectNameString += ",name=" + ObjectName.quote(this.beanName);
            }
            objectName = ObjectName.getInstance(objectNameString);
        }
        return objectName;
    }

    @ManagedAttribute(description = "Maven ${project.version} property extracted from ${webapp-home}/META-INF/${groupId}/${artifactId}/pom.properties file")
    public String getVersion() {
        return version;
    }

    private void loadInformationFromPomProperties() throws URISyntaxException, IOException {
        String mavenMetaInfFolderPath = servletContext.getRealPath("/META-INF/maven/");
        if (mavenMetaInfFolderPath == null) {
            logger.warn("Folder " + servletContext.getRealPath("/") + "/META-INF/maven/"
                    + " not found. Application probably in development mode, could not determine application maven information");
            return;
        }
        File mavenMetaInfFolder = new File(mavenMetaInfFolderPath);
        if (!mavenMetaInfFolder.exists()) {
            logger.warn("Folder " + mavenMetaInfFolder.getAbsolutePath()
                    + " not found. Application probably in development mode, could not determine application maven information");
            return;
        }

        File[] groupIdFolderAsArray = mavenMetaInfFolder.listFiles(new DirectoryFileFilter());
        if (groupIdFolderAsArray.length == 0) {
            logger.warn("Folder /META-INF/maven/${project.groupId} not found under " + mavenMetaInfFolder.getAbsolutePath()
                    + ". Application probably in development mode, could not determine application maven information");
            return;
        } else if (groupIdFolderAsArray.length > 1) {
            logger.warn("More than one folder found under " + mavenMetaInfFolder.getAbsolutePath() + " : "
                    + Arrays.asList(groupIdFolderAsArray) + ". Could not determine application maven information");
            return;
        }
        File groupIdFolder = groupIdFolderAsArray[0];

        File[] artifactIdFolderAsArray = groupIdFolder.listFiles(new DirectoryFileFilter());
        if (artifactIdFolderAsArray.length == 0) {
            logger.warn("Folder /META-INF/maven/" + groupIdFolder.getName() + "/${project.artifactId} not found under "
                    + mavenMetaInfFolder.getAbsolutePath()
                    + ". Application probably in development mode, could not determine application maven information");
            return;
        } else if (artifactIdFolderAsArray.length > 1) {
            logger.warn("More than one folder found under " + groupIdFolder.getAbsolutePath() + " : "
                    + Arrays.asList(artifactIdFolderAsArray) + ". Could not determine application maven information");
            return;
        }
        File artifactIdFolder = artifactIdFolderAsArray[0];

        File pomPropertiesFile = new File(artifactIdFolder, "pom.properties");
        if (!pomPropertiesFile.exists()) {
            logger.warn("File " + pomPropertiesFile.getAbsolutePath()
                    + " not found. Application probably in development mode, could not determine application maven information");
            return;
        }

        Properties pomProperties = new Properties();
        FileInputStream in = new FileInputStream(pomPropertiesFile);
        try {
            pomProperties.load(in);
        } finally {
            in.close();
        }

        this.groupId = pomProperties.getProperty(POM_PROPERTY_GROUP_ID, this.groupId);
        this.artifactId = pomProperties.getProperty(POM_PROPERTY_ARTIFACT_ID, this.artifactId);
        this.version = pomProperties.getProperty(POM_PROPERTY_VERSION, this.version);
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setJmxDomain(String jmxDomain) {
        this.jmxDomain = jmxDomain;
    }

    public void setObjectName(String objectName) throws MalformedObjectNameException {
        this.objectName = objectName == null ? null : ObjectName.getInstance(objectName);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
