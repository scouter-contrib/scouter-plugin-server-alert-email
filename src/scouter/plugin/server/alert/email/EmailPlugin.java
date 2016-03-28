/*
 *  Copyright 2016 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author Sang-Cheon Park
 */
package scouter.plugin.server.alert.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import scouter.lang.AlertLevel;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.server.Configure;
import scouter.server.Logger;
import scouter.server.core.AgentManager;

/**
 * Scouter server plugin to send alert via email
 * 
 * @author Sang-Cheon Park(nices96@gmail.com) on 2016. 3. 28.
 */
public class EmailPlugin {
	
	// Get singleton Configure instance from server
    final Configure conf = Configure.getInstance();

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
    public void alert(final AlertPack pack) {
        if (conf.getBoolean("ext_plugin_email_send_alert", false)) {
        	
        	// Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
        	int level = conf.getInt("ext_plugin_email_level", 0);
        	
        	if (level <= pack.level) {
        		new Thread() {
        			public void run() {
                        try {
                        	// Get server configurations for email
                            String hostname = conf.getValue("ext_plugin_email_smtp_hostname", "smtp.gmail.com");
                            int port = conf.getInt("ext_plugin_email_smtp_port", 587);
                            String username = conf.getValue("ext_plugin_email_username");
                            String password = conf.getValue("ext_plugin_email_password");
                            boolean tlsEnabled = conf.getBoolean("ext_plugin_email_tls_enabled", true);
                            String from = conf.getValue("ext_plugin_email_from_address");
                            String to = conf.getValue("ext_plugin_email_to_address");
                            String cc = conf.getValue("ext_plugin_email_cc_address");
                        	
                        	assert hostname != null;
                        	assert port > 0;
                        	assert username != null;
                        	assert password != null;
                        	assert from != null;
                        	assert to != null;
                        	
                        	// Get agent Name
                        	String name = AgentManager.getAgentName(pack.objHash) == null ? "N/A" : AgentManager.getAgentName(pack.objHash);
                        	
                        	if (name.equals("N/A") && pack.message.endsWith("connected.")) {
                    			int idx = pack.message.indexOf("connected");
                        		if (pack.message.indexOf("(re)") > -1) {
                        			name = pack.message.substring(0, idx - 8);
                        		} else {
                        			name = pack.message.substring(0, idx - 7);
                        		}
                        	}
                        	
                        	// Make email subject
                            String subject = "[" + AlertLevel.getName(pack.level) + "] " + pack.objType.toUpperCase() + 
                                          	 "(" + name + ") : " + pack.title;

                            // Make email message
                            String message = "[TYPE] : " + pack.objType.toUpperCase() + "\n" + 
                                          	 "[NAME] : " + name + "\n" + 
                                          	 "[LEVEL] : " + AlertLevel.getName(pack.level) + "\n" +
                                          	 "[TITLE] : " + pack.title + "\n" + 
                                          	 "[MESSAGE] : " + pack.message;
                                          
                            // Create an Email instance
                            Email email = new SimpleEmail();
                            
                            email.setHostName(hostname);
                            email.setSmtpPort(port);
                            email.setAuthenticator(new DefaultAuthenticator(username, password));
                            email.setStartTLSEnabled(tlsEnabled);
                            email.setFrom(from);
                            email.setSubject(subject);
                            email.setMsg(message);
                            
                            for (String addr : to.split(",")) {
                            	email.addTo(addr);
                            }
                            
                            if (cc != null) {
                            	for (String addr : cc.split(",")) {
                            		email.addCc(addr);
                            	}
                            }
                            
                            // Send the email
                            email.send();
                            
                            println("Email sent to [" + to + "] successfully.");
                        } catch (Exception e) {
                        	println("[Error] : " + e.getMessage());
                        	
                        	if (conf._trace) {
                                e.printStackTrace();
                            }
                        }
        			}
        		}.start();
            }
        }
    }
    
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
	public void object(ObjectPack pack) {
		if (pack.version != null && pack.version.length() > 0) {
			AlertPack p = null;
			if (pack.wakeup == 0L) {
				// in case of agent (re)connected
				p = new AlertPack();
		        p.level = AlertLevel.WARN;
		        p.objHash = pack.objHash;
		        p.title = "An object has been activated.";
		        p.message = pack.objName + " is (re)connected.";
		        p.time = System.currentTimeMillis();
		        p.objType = "scouter";
				
		        alert(p);
			} else if (pack.alive == false) {
				// in case of agent disconnected
				p = new AlertPack();
		        p.level = AlertLevel.WARN;
		        p.objHash = pack.objHash;
		        p.title = "An object has been inactivated.";
		        p.message = pack.objName + " is disconnected.";
		        p.time = System.currentTimeMillis();
		        p.objType = "scouter";
				
		        alert(p);
			}
		}
	}

    private void println(Object o) {
        if (conf.getBoolean("ext_plugin_email_debug", false)) {
            Logger.println(o);
        }
    }
}