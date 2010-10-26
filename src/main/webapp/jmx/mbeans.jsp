<%--                                                                           --%>
<%--  Copyright 2008-2010 Xebia and the original author or authors.            --%>
<%--                                                                           --%>
<%--  Licensed under the Apache License, Version 2.0 (the "License");          --%>
<%--  you may not use this file except in compliance with the License.         --%>
<%--  You may obtain a copy of the License at                                  --%>
<%--                                                                           --%>
<%--       http://www.apache.org/licenses/LICENSE-2.0                          --%>
<%--                                                                           --%>
<%--  Unless required by applicable law or agreed to in writing, software      --%>
<%--  distributed under the License is distributed on an "AS IS" BASIS,        --%>
<%--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. --%>
<%--  See the License for the specific language governing permissions and      --%>
<%--  limitations under the License.                                           --%>
<%--                                                                           --%>
<%@ page import="java.lang.management.ManagementFactory"%>
<%@ page import="javax.management.*"%>
<%@ page import="java.io.*,java.util.*"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.net.URLEncoder"%><html>
<head>
<title>MBeanServers</title>
</head>
<body>
<h1>MBeanServers</h1>
<%
    try {
        out.println("Server: " + InetAddress.getLocalHost() + ", date: "
                    + new java.sql.Timestamp(System.currentTimeMillis()).toString() + "<br>");
        
        List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
        for (MBeanServer mbeanServer : mbeanServers) {
            
            out.println("<h1>MbeanServer " + mbeanServer + "</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>Object Name</th><th>Description</th></tr>");
            List<ObjectInstance> objectInstances = new ArrayList<ObjectInstance>(mbeanServer.queryMBeans(ObjectName.WILDCARD, null));
            Collections.sort(objectInstances, new Comparator<ObjectInstance>() {
                public int compare(ObjectInstance o1, ObjectInstance o2) {
                    return o1.getObjectName().compareTo(o2.getObjectName());
                }
                
            });
            for (ObjectInstance objectInstance : objectInstances) {
                ObjectName objectName = objectInstance.getObjectName();
                out.println("<tr>");
                out.println("<td><a href='mbean.jsp?name=" + URLEncoder.encode(objectName.getCanonicalName(), "UTF-8") + "'>"
                            + objectName + "</a></td>");
                out.println("<td><em>" + mbeanServer.getMBeanInfo(objectName).getDescription() + "</em></td>");
                out.println("</tr>");
                out.flush();
            }
            
            out.println("</table>");
            out.println("Total mbeans count <b>" + objectInstances.size() + "</b>");
        }
    } catch (Throwable e) {
        out.println("<pre>");
        PrintWriter printWriter = new PrintWriter(out);
        e.printStackTrace(printWriter);
        out.println("</pre>");
        printWriter.flush();
    }
%>
</body>
</html>