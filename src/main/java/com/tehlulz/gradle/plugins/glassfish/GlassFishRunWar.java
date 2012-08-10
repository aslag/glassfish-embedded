package com.tehlulz.gradle.plugins.glassfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFishException;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.InputFile;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aslag
 */
public class GlassFishRunWar
  extends AbstractGlassFishRunTask
{
  private static final Logger LOG = LoggerFactory.getLogger(GlassFishRunWar.class);
  
  private File webApp;
  
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

  /**
   * Blocks until user elects to leave
   */
  @Override
  protected void deployUndeployLoop()
  {
    Deployer deployer;
    while (true) {
      ProgressLoggerFactory progressLoggerFactory = getServices().get(ProgressLoggerFactory.class);
      ProgressLogger progressLogger = progressLoggerFactory.newOperation(AbstractGlassFishRunTask.class);
      progressLogger.setDescription(String.format("Deploy archive %s", getWebApp().getName()));
      progressLogger.setShortDescription(String.format("Deploying archive %s", getWebApp().getName()));
      progressLogger.started();
     
      try {
        deployer = glassFish.getDeployer();
        deployer.deploy(getWebApp().toURI(), "--name="+getContextPath(), "--contextroot="+getContextPath());
      }
      catch (Exception ex) {
        throw new GradleException("Failed to deploy webapp to embedded GlassFish server.", ex);
      } finally {
        progressLogger.completed();
      }
      
      System.out.println(String.format("\nApplication now available at http://localhost:%d/%s/", getHttpPort(), getContextPath()));
      System.out.println("Hit ENTER to redeploy, X to exit\n");
      String str;
      try {
          str = new BufferedReader(new InputStreamReader(System.in)).readLine();
      } catch (Exception ex) {
          throw new GradleException(ex.getMessage(), ex);
      }
      
      try {
        deployer.undeploy(getContextPath());
      }
      catch (GlassFishException ex) {
        throw new GradleException("Failed to undeploy webapp in embedded GlassFish server.", ex);
      }
      
      if (str.equalsIgnoreCase("x")) 
        break;
    }
    
  }

}
