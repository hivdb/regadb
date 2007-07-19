package net.sf.wts.client.cli;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.WtsClient;
import net.sf.wts.client.meta.WtsMetaClient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

public class WtsClientCli {
	private static Options options;
	
	public static void main(String[] args) {
		options = new Options();
		
		Option helpOption = new Option("h", "help", false, "print this message");
		options.addOption(helpOption);
		
		Option versionOption = new Option("v", "version", false, "print the version information");
		options.addOption(versionOption);
		
		Option urlOption = new Option("u", "url", true, "set the url");
		urlOption.setArgs(1);
		urlOption.setArgName("url");
		options.addOption(urlOption);
		
		Option proxyOption = new Option("p", "proxy", true, "set the proxy settings");
		proxyOption.setArgs(2);
		proxyOption.setArgName("proxy host:proxy port");
		proxyOption.setValueSeparator(':');
		options.addOption(proxyOption);
		
		OptionGroup wtsOptionGroup = new OptionGroup();
		
		Option servicesOption = new Option(null, "services", false, "list available services");
		wtsOptionGroup.addOption(servicesOption);
		
		Option serviceDescriptionOption = new Option(null, "servicedescription", true, "get a service's description");
		serviceDescriptionOption.setArgs(1);
		serviceDescriptionOption.setArgName("servicename");
		wtsOptionGroup.addOption(serviceDescriptionOption);
		
		Option loginOption = new Option(null, "login", true, "login to the service and receive a session ticket");
		loginOption.setArgs(3);
		loginOption.setArgName("servicename username password");
		loginOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(loginOption);
		
		Option closeSessionOption = new Option(null, "close", true, "close session");
		closeSessionOption.setArgs(2);
		closeSessionOption.setArgName("servicename sessionticket");
		closeSessionOption.setValueSeparator(',');
		wtsOptionGroup.addOption(closeSessionOption);
		
		Option startOption = new Option(null, "start", true, "start the service execution");
		startOption.setArgs(2);
		startOption.setArgName("servicename sessionticket");
		startOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(startOption);
		
		Option stopOption = new Option(null, "stop", true, "stop the service execution");
		stopOption.setArgs(2);
		stopOption.setArgName("servicename sessionticket");
		stopOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(stopOption);
		
		Option uploadOption = new Option(null, "upload", true, "upload a file to the service");
		uploadOption.setArgs(4);
		uploadOption.setArgName("servicename sessionticket filename file");
		uploadOption.setValueSeparator(',');
		wtsOptionGroup.addOption(uploadOption);
		
		Option downloadOption = new Option(null, "download", true, "download a result file from the service");
		downloadOption.setArgs(4);
		downloadOption.setArgName("servicename sessionticket filename file");
		downloadOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(downloadOption);
		
		Option monitorStatusOption = new Option(null, "status", true, "get the status of the running service");
		monitorStatusOption.setArgs(2);
		monitorStatusOption.setArgName("servicename sessionticket");
		monitorStatusOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(monitorStatusOption);
		
		Option monitorLogOption = new Option(null, "log", true, "get the entire log file of the running service");
		monitorLogOption.setArgs(3);
		monitorLogOption.setArgName("servicename sessionticket file");
		monitorLogOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(monitorLogOption);
		
		Option monitorTailOption = new Option(null, "logtail", true, "get the log tail of the running service");
		monitorTailOption.setArgs(3);
		monitorTailOption.setArgName("servicename sessionticket lines");
		monitorTailOption.setValueSeparator(' ');
		wtsOptionGroup.addOption(monitorTailOption);
		
		options.addOptionGroup(wtsOptionGroup);
		
		CommandLineParser parser = new PosixParser();
		
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.getOptions().length == 0) {
				usage();
			}
			else if (cmd.hasOption(helpOption.getLongOpt())) {
				usage();
			}
			else if (cmd.hasOption(versionOption.getLongOpt())) {
				System.out.println("Wts CLI 1.0 Beta");
			}
			else if (cmd.hasOption(urlOption.getLongOpt())) {
				String url = cmd.getOptionValue(urlOption.getLongOpt());
				
				if (cmd.hasOption(proxyOption.getLongOpt())) {
					if (cmd.getOptionValues(proxyOption.getLongOpt()).length == 2) {
						String proxyHost = cmd.getOptionValues(proxyOption.getLongOpt())[0];
						String proxyPort = cmd.getOptionValues(proxyOption.getLongOpt())[1];
						
						System.setProperty("http.proxyHost", proxyHost);
						System.setProperty("http.proxyPort", proxyPort);
					}
					else {
						usage();
					}
				}
				
				WtsClient wtsClient = new WtsClient(url);
				WtsMetaClient wtsMetaClient = new WtsMetaClient(url);
				
				if (cmd.hasOption(servicesOption.getLongOpt())) {
					try {
						String services = wtsMetaClient.listServices();
						
						System.out.println(services);
					}
					catch (RemoteException e) {
						remoteException(e);
					}
					catch (MalformedURLException e) {
						malformedURLException();
					}
				}
				else if (cmd.hasOption(serviceDescriptionOption.getLongOpt())) {
					String serviceName = cmd.getOptionValue(serviceDescriptionOption.getLongOpt());
					
					try {
						String serviceDescription = new String(wtsMetaClient.getServiceDescription(serviceName));
						
						System.out.println(serviceDescription);
					}
					catch (RemoteException e) {
						remoteException(e);
					}
					catch (MalformedURLException e) {
						malformedURLException();
					}
				}
				else if (cmd.hasOption(loginOption.getLongOpt())) {
					if (cmd.getOptionValues(loginOption.getLongOpt()).length == 3) {
						String serviceName = cmd.getOptionValues(loginOption.getLongOpt())[0];
						String userName = cmd.getOptionValues(loginOption.getLongOpt())[1];
						String password = cmd.getOptionValues(loginOption.getLongOpt())[2];
						
						try {
							String challenge = wtsClient.getChallenge(userName);
							
							String sessionTicket = wtsClient.login(userName, challenge, password, serviceName);
							
							System.out.println("Session Ticket: " + sessionTicket);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(closeSessionOption.getLongOpt())) {
					if (cmd.getOptionValues(closeSessionOption.getLongOpt()).length == 2) {
						String serviceName = cmd.getOptionValues(closeSessionOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(closeSessionOption.getLongOpt())[1];
						
						try {
							wtsClient.closeSession(sessionTicket, serviceName);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(startOption.getLongOpt())) {
					if (cmd.getOptionValues(startOption.getLongOpt()).length == 2) {
						String serviceName = cmd.getOptionValues(startOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(startOption.getLongOpt())[1];
						
						try {
							wtsClient.start(sessionTicket, serviceName);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(stopOption.getLongOpt())) {
					if (cmd.getOptionValues(stopOption.getLongOpt()).length == 2) {
						String serviceName = cmd.getOptionValues(stopOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(stopOption.getLongOpt())[1];
						
						try {
							wtsClient.stop(sessionTicket, serviceName);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(uploadOption.getLongOpt())) {
					if (cmd.getOptionValues(uploadOption.getLongOpt()).length == 4) {
						String serviceName = cmd.getOptionValues(uploadOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(uploadOption.getLongOpt())[1];
						String fileName = cmd.getOptionValues(uploadOption.getLongOpt())[2];
						String file = cmd.getOptionValues(uploadOption.getLongOpt())[3];
						
						File localLocation = new File(file);
						
						try {
							wtsClient.upload(sessionTicket, serviceName, fileName, localLocation);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(downloadOption.getLongOpt())) {
					if (cmd.getOptionValues(downloadOption.getLongOpt()).length == 4) {
						String serviceName = cmd.getOptionValues(downloadOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(downloadOption.getLongOpt())[1];
						String fileName = cmd.getOptionValues(downloadOption.getLongOpt())[2];
						String file = cmd.getOptionValues(downloadOption.getLongOpt())[3];
						
						File toWrite = new File(file);
						
						try {
							wtsClient.download(sessionTicket, serviceName, fileName, toWrite);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(monitorStatusOption.getLongOpt())) {
					if (cmd.getOptionValues(monitorStatusOption.getLongOpt()).length == 2) {
						String serviceName = cmd.getOptionValues(monitorStatusOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(monitorStatusOption.getLongOpt())[1];
						
						try {
							System.out.println(wtsClient.monitorStatus(sessionTicket, serviceName));
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(monitorLogOption.getLongOpt())) {
					if (cmd.getOptionValues(monitorLogOption.getLongOpt()).length == 3) {
						String serviceName = cmd.getOptionValues(monitorLogOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(monitorLogOption.getLongOpt())[1];
						String file = cmd.getOptionValues(monitorLogOption.getLongOpt())[2];
						
						File toWrite = new File(file);
						
						try {
							FileUtils.writeByteArrayToFile(toWrite, wtsClient.monitorLogFile(sessionTicket, serviceName));
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						usage();
					}
				}
				else if (cmd.hasOption(monitorTailOption.getLongOpt())) {
					if (cmd.getOptionValues(monitorTailOption.getLongOpt()).length == 3) {
						String serviceName = cmd.getOptionValues(monitorTailOption.getLongOpt())[0];
						String sessionTicket = cmd.getOptionValues(monitorTailOption.getLongOpt())[1];
						int numberOfLines = Integer.parseInt(cmd.getOptionValues(monitorTailOption.getLongOpt())[1]);
						
						try {
							String tail = new String(wtsClient.monitorLogTail(sessionTicket, serviceName, numberOfLines));
							System.out.println(tail);
						}
						catch (RemoteException e) {
							remoteException(e);
						}
						catch (MalformedURLException e) {
							malformedURLException();
						}
					}
					else {
						usage();
					}
				}
				else {
					usage();
				}
			}
			else {
				usage();
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void usage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("wts", options, true);
		
		System.exit(0);
	}
	
	public static void remoteException(Exception e) {
		System.out.println("Remote Exception");
		e.printStackTrace();
		
		System.exit(0);
	}
	
	public static void malformedURLException() {
		System.out.println("Malformed URL Exception");
		
		System.exit(0);
	}
}
