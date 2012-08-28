package com.tehlulz.gradle.plugins.glassfish;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
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
    GlassFishProperties gfProps = new GlassFishProperties();

    gfProps.setPort("http-listener", httpPort); // refer to JavaDocs for the details of this API.
    glassFish = GlassFishRuntime.bootstrap().newGlassFish(gfProps);
    glassFish.start();
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
