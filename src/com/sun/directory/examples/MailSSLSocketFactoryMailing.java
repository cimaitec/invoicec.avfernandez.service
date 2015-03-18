package com.sun.directory.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MailSSLSocketFactoryMailing {
	
	  private boolean trustAllHosts;
	  private String[] trustedHosts = null;
	  private SSLContext sslcontext;
	  private KeyManager[] keyManagers;
	  private TrustManager[] trustManagers;
	  private SecureRandom secureRandom;
	  private SSLSocketFactory adapteeFactory = null;

	  public MailSSLSocketFactoryMailing()
	    throws GeneralSecurityException
	  {
	    this("TLS");
	  }

	  public MailSSLSocketFactoryMailing(String protocol)
	    throws GeneralSecurityException
	  {
	    this.trustAllHosts = false;

	    this.sslcontext = SSLContext.getInstance(protocol);

	    this.keyManagers = null;
	    this.trustManagers = new TrustManager[] { new MailTrustManager1(null) };
	    this.secureRandom = null;

	    newAdapteeFactory();
	  }

	  private synchronized void newAdapteeFactory()
	    throws KeyManagementException
	  {
	    this.sslcontext.init(this.keyManagers, this.trustManagers, this.secureRandom);

	    this.adapteeFactory = this.sslcontext.getSocketFactory();
	  }

	  public synchronized KeyManager[] getKeyManagers()
	  {
	    return (KeyManager[])this.keyManagers.clone();
	  }

	  public synchronized void setKeyManagers(KeyManager[] keyManagers)
	    throws GeneralSecurityException
	  {
	    this.keyManagers = ((KeyManager[])keyManagers.clone());
	    newAdapteeFactory();
	  }

	  public synchronized SecureRandom getSecureRandom()
	  {
	    return this.secureRandom;
	  }

	  public synchronized void setSecureRandom(SecureRandom secureRandom)
	    throws GeneralSecurityException
	  {
	    this.secureRandom = secureRandom;
	    newAdapteeFactory();
	  }

	  public synchronized TrustManager[] getTrustManagers()
	  {
	    return this.trustManagers;
	  }

	  public synchronized void setTrustManagers(TrustManager[] trustManagers)
	    throws GeneralSecurityException
	  {
	    this.trustManagers = trustManagers;
	    newAdapteeFactory();
	  }

	  public synchronized boolean isTrustAllHosts()
	  {
	    return this.trustAllHosts;
	  }

	  public synchronized void setTrustAllHosts(boolean trustAllHosts)
	  {
	    this.trustAllHosts = trustAllHosts;
	  }

	  public synchronized String[] getTrustedHosts()
	  {
	    return (String[])this.trustedHosts.clone();
	  }

	  public synchronized void setTrustedHosts(String[] trustedHosts)
	  {
	    this.trustedHosts = ((String[])trustedHosts.clone());
	  }

	  public synchronized boolean isServerTrusted(String server, SSLSocket sslSocket)
	  {
	    if (this.trustAllHosts) {
	      return true;
	    }

	    if (this.trustedHosts != null) {
	      return Arrays.asList(this.trustedHosts).contains(server);
	    }

	    return true;
	  }

	  public synchronized Socket createSocket(Socket socket, String s, int i, boolean flag)
	    throws IOException
	  {
	    return this.adapteeFactory.createSocket(socket, s, i, flag);
	  }

	  public synchronized String[] getDefaultCipherSuites()
	  {
	    return this.adapteeFactory.getDefaultCipherSuites();
	  }

	  public synchronized String[] getSupportedCipherSuites()
	  {
	    return this.adapteeFactory.getSupportedCipherSuites();
	  }

	  public synchronized Socket createSocket()
	    throws IOException
	  {
	    return this.adapteeFactory.createSocket();
	  }

	  public synchronized Socket createSocket(InetAddress inetaddress, int i, InetAddress inetaddress1, int j)
	    throws IOException
	  {
	    return this.adapteeFactory.createSocket(inetaddress, i, inetaddress1, j);
	  }

	  public synchronized Socket createSocket(InetAddress inetaddress, int i)
	    throws IOException
	  {
	    return this.adapteeFactory.createSocket(inetaddress, i);
	  }

	  public synchronized Socket createSocket(String s, int i, InetAddress inetaddress, int j)
	    throws IOException, UnknownHostException
	  {
	    return this.adapteeFactory.createSocket(s, i, inetaddress, j);
	  }

	  public synchronized Socket createSocket(String s, int i)
	    throws IOException, UnknownHostException
	  {
	    return this.adapteeFactory.createSocket(s, i);
	  }

	  private class MailTrustManager1
	    implements X509TrustManager
	  {
	    private X509TrustManager adapteeTrustManager = null;

	    private MailTrustManager1(Object object)
	      throws GeneralSecurityException
	    {
	      TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
	      tmf.init((KeyStore)null);
	      this.adapteeTrustManager = ((X509TrustManager)tmf.getTrustManagers()[0]);
	    }

	    public void checkClientTrusted(X509Certificate[] certs, String authType)
	      throws CertificateException
	    {
	      if ((!MailSSLSocketFactoryMailing.this.trustAllHosts) && (MailSSLSocketFactoryMailing.this.trustedHosts == null))
	        this.adapteeTrustManager.checkClientTrusted(certs, authType);
	    }

	    public void checkServerTrusted(X509Certificate[] certs, String authType)
	      throws CertificateException
	    {
	      if ((!MailSSLSocketFactoryMailing.this.trustAllHosts) && (MailSSLSocketFactoryMailing.this.trustedHosts == null))
	        this.adapteeTrustManager.checkServerTrusted(certs, authType);
	    }

	    public X509Certificate[] getAcceptedIssuers()
	    {
	      return this.adapteeTrustManager.getAcceptedIssuers();
	    }

	    
	  }
	}