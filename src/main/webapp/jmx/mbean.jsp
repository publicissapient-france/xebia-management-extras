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
<%@page import="java.net.InetAddress"%><html>
<head>
<title>MBean</title>
</head>
<body>
<h1>MBean</h1>
<%
    try {
        String name = request.getParameter("name");
        out.write("Server: " + InetAddress.getLocalHost() + ", date: "
                  + new java.sql.Timestamp(System.currentTimeMillis()).toString() + "<br>");
        
        if (name == null || name.length() == 0) {
            out.print("<form>ObjectName<input name='name' /><input type='submit'/></form>");
        } else {
            
            List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
            for (MBeanServer mbeanServer : mbeanServers) {
                
                Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName(name), null);
                
                for (ObjectInstance objectInstance : objectInstances) {
                    ObjectName objectName = objectInstance.getObjectName();
                    MBeanInfo mbeanInfo = mbeanServer.getMBeanInfo(objectName);
                    MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
                    Arrays.sort(attributeInfos, new Comparator<MBeanAttributeInfo>() {
                        public int compare(MBeanAttributeInfo o1, MBeanAttributeInfo o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    out.println("<h1>" + objectName + "</h1>");
                    out.println("<table border='1'>");
                    
                    out.println("<tr><th>Name</th><td>Value</td><td>Type</td><td>Description</td></tr>");
                    for (MBeanAttributeInfo attributeInfo : attributeInfos) {
                        out.println("<tr>");
                        out.println("<th>" + attributeInfo.getName() + "</th>");
                        out.println("<td>"
                                    + (attributeInfo.isReadable()
                                        ? mbeanServer.getAttribute(objectName, attributeInfo.getName()) : "#NOT_READABLE#")
                                    + "</td>");
                        out.println("<td>" + attributeInfo.getType() + "</td>");
                        out.println("<td>" + attributeInfo.getDescription() + "</td>");
                        out.println("</tr>");
                    }
                    out.flush();
                    out.println("</table>");
                }
                
                out.println("Total mbeans count <b>" + objectInstances.size() + "</b>");
            }
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