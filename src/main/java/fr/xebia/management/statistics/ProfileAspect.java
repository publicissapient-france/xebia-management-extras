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
package fr.xebia.management.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.util.StringUtils;

@ManagedResource
@Aspect
public class ProfileAspect implements InitializingBean, DisposableBean, BeanNameAware, SelfNaming {

    public enum ClassNameStyle {
        COMPACT_FULLY_QUALIFIED_NAME, FULLY_QUALIFIED_NAME, SHORT_NAME
    };

    protected static class RootObject {

        private final ProceedingJoinPoint pjp;

        private RootObject(ProceedingJoinPoint pjp) {
            super();
            this.pjp = pjp;
        }

        public Object[] getArgs() {
            return pjp.getArgs();
        }

        public Object getInvokedObject() {
            return pjp.getThis();
        }
    }

    /**
     * <p>
     * Formats the given <code>fullyQualifiedName</code> according to the given
     * <code>classNameStyle</code>.
     * </p>
     * <p>
     * Samples with <code>java.lang.String</code>:
     * <ul>
     * <li>{@link ClassNameStyle#FULLY_QUALIFIED_NAME} :
     * <code>java.lang.String</code></li>
     * <li>{@link ClassNameStyle#COMPACT_FULLY_QUALIFIED_NAME} :
     * <code>j.l.String</code></li>
     * <li>{@link ClassNameStyle#SHORT_NAME} : <code>String</code></li>
     * </ul>
     * </p>
     */
    protected static String getClassName(String fullyQualifiedName, ClassNameStyle classNameStyle) {
        String className;
        switch (classNameStyle) {
        case FULLY_QUALIFIED_NAME:
            className = fullyQualifiedName;
            break;
        case COMPACT_FULLY_QUALIFIED_NAME:
            String[] splittedFullyQualifiedName = StringUtils.delimitedListToStringArray(fullyQualifiedName, ".");
            StringBuilder sb = new StringBuilder(fullyQualifiedName.length());
            for (int i = 0; i < splittedFullyQualifiedName.length - 1; i++) {
                sb.append(splittedFullyQualifiedName[i].charAt(0)).append(".");
            }
            sb.append(splittedFullyQualifiedName[splittedFullyQualifiedName.length - 1]);
            className = sb.toString();
            break;
        case SHORT_NAME:
            className = StringUtils.unqualify(fullyQualifiedName);
            break;
        default:
            // should not occur
            className = fullyQualifiedName;
            break;
        }
        return className;
    }

    private ClassNameStyle classNameStyle = ClassNameStyle.COMPACT_FULLY_QUALIFIED_NAME;

    private Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();

    private ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * @see ObjectName#getDomain()
     */
    private String jmxDomain = "fr.xebia";

    private MBeanExporter mbeanExporter;

    private MBeanServer server;

    private String name;

    private ObjectName objectName;

    private ParserContext parserContext = new TemplateParserContext();

    protected ConcurrentMap<String, ServiceStatistics> serviceStatisticsByName = new ConcurrentHashMap<String, ServiceStatistics>();

    public void afterPropertiesSet() throws Exception {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }

        this.mbeanExporter = new AnnotationMBeanExporter();
        this.mbeanExporter.setEnsureUniqueRuntimeObjectNames(false);
        this.mbeanExporter.setServer(this.server);
        this.mbeanExporter.setAutodetectMode(MBeanExporter.AUTODETECT_NONE);
        this.mbeanExporter.afterPropertiesSet();
    }

    public void destroy() throws Exception {
        this.mbeanExporter.destroy();
    }

    public MBeanExporter getMbeanExporter() {
        return mbeanExporter;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            String objectNameAsString = jmxDomain + ":type=ProfileAspect";
            if (StringUtils.hasLength(name)) {
                objectNameAsString += ",name=" + ObjectName.quote(name);
            }
            objectName = new ObjectName(objectNameAsString);
        }
        return objectName;
    }

    @ManagedAttribute
    public int getRegisteredServiceStatisticsCount() {
        return this.serviceStatisticsByName.size();
    }

    @Around(value = "execution(* *(..)) && @annotation(profiled)", argNames = "pjp,profiled")
    public Object profileInvocation(ProceedingJoinPoint pjp, Profiled profiled) throws Throwable {

        Signature jointPointSignature = pjp.getStaticPart().getSignature();

        // COMPUTE SERVICE STATISTICS NAME
        String name;
        if (StringUtils.hasLength(profiled.name())) {
            String template = profiled.name();
            Expression expression = expressionCache.get(template);
            if (expression == null) {
                expression = expressionParser.parseExpression(template, parserContext);
                expressionCache.put(template, expression);
            }

            if (expression instanceof LiteralExpression) {
                // Optimization : prevent useless objects instantiations
                name = expression.getExpressionString();
            } else {
                name = expression.getValue(new RootObject(pjp), String.class);
            }
        } else {
            name = jointPointSignature.getDeclaringTypeName() + "." + jointPointSignature.getName();
        }

        //
        ServiceStatistics serviceStatistics = serviceStatisticsByName.get(name);
        if (serviceStatistics == null) {
            // INSTIANCIATE NEW SERVICE STATISTICS
            ServiceStatistics newServiceStatistics = new ServiceStatistics(name, profiled.businessExceptionsTypes(),
                    profiled.communicationExceptionsTypes());
            newServiceStatistics.setSlowInvocationThresholdInMillis(profiled.slowInvocationThresholdInMillis());
            newServiceStatistics.setVerySlowInvocationThresholdInMillis(profiled.verySlowInvocationThresholdInMillis());
            String nameAttribute;
            if (StringUtils.hasLength(profiled.name())) {
                nameAttribute = name;
            } else {
                nameAttribute = ProfileAspect.getClassName(jointPointSignature.getDeclaringTypeName(), classNameStyle) + "."
                        + jointPointSignature.getName();

            }
            newServiceStatistics.setObjectName(new ObjectName(this.jmxDomain + ":type=ServiceStatistics,name=" + nameAttribute));

            ServiceStatistics previousServiceStatistics = serviceStatisticsByName.putIfAbsent(name, newServiceStatistics);
            if (previousServiceStatistics == null) {
                serviceStatistics = newServiceStatistics;
                mbeanExporter.registerManagedResource(serviceStatistics);
            } else {
                serviceStatistics = previousServiceStatistics;
            }
        }

        // INVOKE AND PROFILE
        long nanosBefore = System.nanoTime();
        serviceStatistics.incrementCurrentActiveCount();
        try {
            Object returned = pjp.proceed();
            return returned;
        } catch (Throwable t) {
            serviceStatistics.incrementExceptionCount(t);
            throw t;
        } finally {
            serviceStatistics.decrementCurrentActiveCount();
            serviceStatistics.incrementInvocationCounterAndTotalDurationWithNanos(System.nanoTime() - nanosBefore);
        }
    }

    public void setBeanName(String name) {
        this.name = name;
    }

    public void setClassNameStyle(ClassNameStyle classNameStyle) {
        this.classNameStyle = classNameStyle;
    }

    /**
     * 
     * @param classNameStyle
     *            one of COMPACT_FULLY_QUALIFIED_NAME, FULLY_QUALIFIED_NAME and
     *            SHORT_NAME
     */
    public void setClassNameStyle(String classNameStyle) {
        this.classNameStyle = ClassNameStyle.valueOf(classNameStyle);
    }

    public void setJmxDomain(String jmxDomain) {
        this.jmxDomain = jmxDomain;
    }

    public void setServer(MBeanServer server) {
        this.server = server;
    }
}
