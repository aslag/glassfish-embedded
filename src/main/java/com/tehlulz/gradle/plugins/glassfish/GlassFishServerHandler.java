package com.tehlulz.gradle.plugins.glassfish;

import java.io.File;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.web.ConfigException;
import org.glassfish.embeddable.web.HttpListener;
import org.glassfish.embeddable.web.HttpsListener;
import org.glassfish.embeddable.web.WebContainer;
import org.glassfish.embeddable.web.WebListenerBase;
import org.glassfish.embeddable.web.config.SslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tehlulz.gradle.plugins.glassfish.GlassFishServerHandler.GlassFishDeployUndeploy.GlassFishDeploymentException;

/**
 * <p>A delegate of {@link AbstractGlassFishRunTask}; performs GlassFish 
 * server management tasks, including starting and stopping the server,
 * and deploying and undeploying apps.</p>
 * 
 * @author aslag
 *
 */
public class GlassFishServerHandler
{
  static final Logger LOG = LoggerFactory.getLogger(GlassFishServerHandler.class);
  
  private GlassFish glassFish;
  
  public GlassFishServerHandler()
  {
  }
 
  public void startGlassFish(Integer httpPort)
    throws GlassFishException
  {
    HttpListener listener = new HttpListener("http-listener-gfembed", httpPort);
    listener.setProtocol("http");
    startGlassFish(listener);
  }


  //TODO: this way seems busted; glassfish doesn't seem to care about these settings when it sets up a keystore, it seems to read system properties from javax.net.ssl instead
  public void startGlassFish(Integer httpPort,
                             String keyStore,
                             String keyStorePassword,
                             String keyStoreAlias,
                             String trustStore,
                             String trustStorePassword)
    throws GlassFishException
  {
    HttpsListener listener = new HttpsListener("https-listener-gfembed", httpPort);
    listener.setProtocol("https");
   
    if (keyStore == null || !(new File(keyStore).canRead())) {
      throw new GlassFishException(String.format("Cannot read specified keyStore %s; GlassFish startup aborted.", keyStore));
    }
    
    if (trustStore == null || !(new File(trustStore).canRead())) {
      throw new GlassFishException(String.format("Cannot read specified trustStore %s; GlassFish startup aborted.", trustStore));
    }
    
    SslConfig sslConfig = new SslConfig(keyStore, trustStore);
   
    //TODO: do user a favor here and check the keystore, truststore by opening them with passwords, checking for aliases, etc. before trying to configure glassfish with them and deploy
    
    if (keyStoreAlias != null) {
      sslConfig.setCertNickname(keyStoreAlias);
    }
    
    if (keyStorePassword != null) {
      sslConfig.setKeyPassword(keyStorePassword.toCharArray());
    }
    
    if (trustStorePassword != null) {
      sslConfig.setTrustPassword(trustStorePassword.toCharArray());
    }
    
    listener.setSslConfig(sslConfig);
    startGlassFish(listener);
  }
  
  protected void startGlassFish(WebListenerBase listener) 
    throws GlassFishException
  {
    glassFish = GlassFishRuntime.bootstrap().newGlassFish();
    glassFish.start();
    WebContainer webContainer = glassFish.getService(WebContainer.class);
    
    try {
      webContainer.addWebListener(listener);
    }
    catch (ConfigException ex) {
      throw new GlassFishException(ex);
    }
  }
  
  public void stopGlassFish()
    throws GlassFishException
  {
    glassFish.stop();
  }
  
  public void deploy(GlassFishDeployUndeploy glassFishDU) 
    throws GlassFishDeploymentException
  {
    glassFishDU.deploy(glassFish);
  }
  
  public void unDeploy(GlassFishDeployUndeploy glassFishDU) 
    throws GlassFishDeploymentException
  {
    glassFishDU.undeploy(glassFish);
  }
  
  public static interface GlassFishDeployUndeploy
  {
    void deploy(GlassFish glassFish)
      throws GlassFishDeploymentException;

    void undeploy(GlassFish glassFish)
      throws GlassFishDeploymentException;

    String getDeploymentName();

    public static class GlassFishDeploymentException
      extends Exception
    {
      public GlassFishDeploymentException()
      {
        super();
      }

      public GlassFishDeploymentException(String msg, Throwable th)
      {
        super(msg, th);
      }

      public GlassFishDeploymentException(Throwable th)
      {
        super(th);
      }
    }
  }

}
