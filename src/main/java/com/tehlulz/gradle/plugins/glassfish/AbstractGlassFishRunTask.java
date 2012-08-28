package com.tehlulz.gradle.plugins.glassfish;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import org.glassfish.embeddable.GlassFishException;
import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tehlulz.gradle.plugins.glassfish.GlassFishServerHandler.GlassFishDeployUndeploy;
import com.tehlulz.gradle.plugins.glassfish.GlassFishServerHandler.GlassFishDeployUndeploy.GlassFishDeploymentException;

/**
 * @author aslag
 */
public abstract class AbstractGlassFishRunTask
  extends ConventionTask
{
  public static final String DEPLOY_EXIT_KEY = "x";

  static final Logger LOG = LoggerFactory.getLogger(AbstractGlassFishRunTask.class);

  protected GlassFishServerHandler glassFishServerHandler = new GlassFishServerHandler();

  protected GlassFishDeployUndeploy glassFishDeployUndeploy;

  private String contextPath;

  private Integer httpPort;

  @TaskAction
  protected void start()
  {
    LOG.info("Configuring GlassFish for " + getProject());
    ProgressLoggerFactory progressLoggerFactory = getServices().get(ProgressLoggerFactory.class);
    ProgressLogger progressLogger = progressLoggerFactory.newOperation(AbstractGlassFishRunTask.class);
    progressLogger.setDescription("Start GlassFish Server");
    progressLogger.setShortDescription("Starting GlassFish");
    progressLogger.started();
    //TODO: add flexible runtimejar loading here

    try {
      //TODO: do this only selectively
      Handler gfLogHandler = new FileHandler(getProject().getProjectDir().getPath() + "/glassfish-embedded.log", true);
      gfLogHandler.setLevel(Level.FINEST);
      gfLogHandler.setFormatter(new SimpleFormatter());

      // remember that "" picks out the root logger
      java.util.logging.Logger.getLogger("").addHandler(gfLogHandler);
    }
    catch (Exception ex) {
      LOG.error("Failed to configure GlassFish logging, logging only errors to console.");
    }
  
    try {
      glassFishServerHandler.startGlassFish(getHttpPort());
      
    } catch (GlassFishException ex) {
      throw new GradleException("Failed to run GlassFish server.", ex);
    } finally {
      progressLogger.completed();
    }
    
    // may block until user signals an exit
    deployUndeployLoop(glassFishServerHandler);
    
    try {
      glassFishServerHandler.stopGlassFish();
    }
    catch (GlassFishException ex) {
      throw new GradleException("Failed to stop GlassFish server.", ex);
    }
  }

  /**
   * Blocks until user elects to exit
   */
  protected void deployUndeployLoop(GlassFishServerHandler glassFishServerHandler)
  {
    while (true) {
      ProgressLogger progressLogger = getServices().get(ProgressLoggerFactory.class).newOperation(
          AbstractGlassFishRunTask.class);
      progressLogger.setDescription(String.format("Deploy archive %s", glassFishDeployUndeploy.getDeploymentName()));
      progressLogger.setShortDescription(String.format("Deploying archive %s",
          glassFishDeployUndeploy.getDeploymentName()));
      progressLogger.started();

      try {
        glassFishServerHandler.deploy(glassFishDeployUndeploy);
        //glassFishDeployUndeploy.deploy(glassFish);
      }
      catch (GlassFishDeploymentException ex) {
        throw new GradleException("Failed to deploy webapp in embedded GlassFish server.", ex);
      }
      finally {
        progressLogger.completed();
      }

      System.out.println(String.format("\nApplication now available at http://localhost:%d/%s/", getHttpPort(),
          getContextPath()));
      System.out.println("Hit ENTER to redeploy, " + DEPLOY_EXIT_KEY + " to exit");
      String str;
      try {
        str = new BufferedReader(new InputStreamReader(System.in)).readLine();
      }
      catch (Exception ex) {
        throw new GradleException(ex.getMessage(), ex);
      }

      try {
        //glassFishDeployUndeploy.undeploy(glassFish);
        glassFishServerHandler.unDeploy(glassFishDeployUndeploy);
      }
      catch (GlassFishDeploymentException ex) {
        throw new GradleException("Failed to undeploy webapp in embedded GlassFish server.", ex);
      }

      if (str.equalsIgnoreCase(DEPLOY_EXIT_KEY))
        break;
    }
  }

  /**
   * Returns the TCP port for GlassFish to listen on for incoming HTTP requests.
   */
  public Integer getHttpPort()
  {
    return httpPort;
  }

  public void setHttpPort(Integer httpPort)
  {
    this.httpPort = httpPort;
  }

  /** 
   * Returns the context path to use to deploy the web application.
   */
  public String getContextPath()
  {
    return contextPath;
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }
}
