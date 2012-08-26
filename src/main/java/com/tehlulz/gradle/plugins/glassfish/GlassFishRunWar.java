package com.tehlulz.gradle.plugins.glassfish;

import java.io.File;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.gradle.api.tasks.InputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aslag
 */
public class GlassFishRunWar
  extends AbstractGlassFishRunTask
{
  static final Logger LOG = LoggerFactory.getLogger(GlassFishRunWar.class);
  
  private File webApp;
  
  public GlassFishRunWar() {
    glassFishDeployUndeploy = new GlassFishWarDeployUndeploy();
  }
  
  /**
   * Returns the web application to deploy.
   */
  @InputFile
  public File getWebApp() {
      return webApp;
  }

  public void setWebApp(File webApp) {
      this.webApp = webApp;
  }

  protected class GlassFishWarDeployUndeploy implements GlassFishDeployUndeploy {
    @Override
    public void deploy(GlassFish glassFish)
      throws GlassFishDeploymentException
    {
      if (glassFish == null)
        throw new RuntimeException("Reference to GlassFish runtime is null and must not be; cannot deploy.");
      
      try {
        glassFish.getDeployer().deploy(getWebApp().toURI(), "--name="+getContextPath(), "--contextroot="+getContextPath());
      }
      catch (GlassFishException ex) {
        throw new GlassFishDeploymentException("Deployment to GlassFish failed.", ex);
      }
    }
    
    @Override
    public void undeploy(GlassFish glassFish)
      throws GlassFishDeploymentException
    {
      if (glassFish == null)
        throw new RuntimeException("Reference to GlassFish runtime is null and must not be; cannot deploy.");
      
      try {
        glassFish.getDeployer().undeploy(getContextPath());
      }
      catch (GlassFishException ex) {
        throw new GlassFishDeploymentException("Undeployment from GlassFish failed.", ex);
      }
    }
    
    @Override
    public String getDeploymentName() {
      return getWebApp().getName();
    }
    
  }
}
