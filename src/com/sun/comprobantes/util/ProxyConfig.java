package com.sun.comprobantes.util;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyConfig
{
  private static String host;
  private static int port;
  private static Proxy proxy;

  public static void init(String uri)
  {
    System.setProperty("java.net.useSystemProxies", "true");
    System.setProperty("javax.net.ssl.trustStore", "resources/jssecacerts");
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

    proxy = getProxy(uri);
    if ((proxy != null) && (!proxy.type().toString().equals("DIRECT"))) {
      InetSocketAddress addr = (InetSocketAddress)proxy.address();
      host = addr.getHostName();
      port = addr.getPort();
    }
    else
    {
      System.setProperty("java.net.useSystemProxies", "true");
    }
  }

  private static Proxy getProxy(String uri)
  {
    List l = null;
    try {
      ProxySelector def = ProxySelector.getDefault();

      l = def.select(new URI("http://proxy/test"));
      ProxySelector.setDefault(ProxySelector.getDefault());
    } catch (Exception e) {
      Logger.getLogger(ProxyConfig.class.getName()).log(Level.SEVERE, null, e);
    }
    if (l != null) {
      Iterator iter = l.iterator(); if (iter.hasNext()) {
        Proxy proxy = (Proxy)iter.next();
        return proxy;
      }
    }
    return null;
  }
  public static class ProxyAuthenticator extends Authenticator {
    private String userName;
    private String password;

    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.userName, this.password.toCharArray());
    }

    public ProxyAuthenticator(String userName, String password) {
      this.userName = userName;
      this.password = password;
    }
  }
}