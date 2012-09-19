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
  
  private boolean ssl;
  
  private String keyStore;
  
  private String keyStorePassword;
  
  private String keyStoreAlias;
  
  private String trustStore;
  
  private String trustStorePassword;

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
      //TODO: do this logging mischief only selectively
      java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
      
      // remove existing handlers
      Handler[] handlers = rootLogger.getHandlers();
      for (int i = 0; i < handlers.length; i++) {
          rootLogger.removeHandler(handlers[i]);
      }
      
      // register logging to file
      Handler gfLogHandler = new FileHandler(getProject().getProjectDir().getPath() + "/glassfish-embedded.log", true);
      gfLogHandler.setLevel(Level.FINEST);
      gfLogHandler.setFormatter(new SimpleFormatter());

      // remember that "" picks out the root logger
      rootLogger.addHandler(gfLogHandler);
    }
    catch (Exception ex) {
      LOG.error("Failed to configure GlassFish logging, logging only errors to console.");
    }
  
    try {
      //TODO: rework this part once a better scheme for configuring listeners is figured out
      if (!ssl) {
        LOG.info(String.format("Configuring GlassFish listener; using port %d", getHttpPort()));
        glassFishServerHandler.startGlassFish(getHttpPort());
      } else {
        LOG.info(String.format("Configuring SSL GlassFish listener; using port %d", getHttpPort()));
        glassFishServerHandler.startGlassFish(getHttpPort(), getKeyStore(), getKeyStorePassword(), getKeyStoreAlias(), getTrustStore(), getTrustStorePassword());
      }
      
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
  
  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public String getKeyStore()
  {
    return keyStore;
  }

  public void setKeyStore(String keyStore)
  {
    this.keyStore = keyStore;
  }

  public String getKeyStorePassword()
  {
    return keyStorePassword;
  }

  public void setKeyStorePassword(String keyStorePassword)
  {
    this.keyStorePassword = keyStorePassword;
  }

  public String getKeyStoreAlias()
  {
    return keyStoreAlias;
  }

  public void setKeyStoreAlias(String keyStoreAlias)
  {
    this.keyStoreAlias = keyStoreAlias;
  }

  public String getTrustStore()
  {
    return trustStore;
  }

  public void setTrustStore(String trustStore)
  {
    this.trustStore = trustStore;
  }

  public String getTrustStorePassword()
  {
    return trustStorePassword;
  }

  public void setTrustStorePassword(String trustStorePassword)
  {
    this.trustStorePassword = trustStorePassword;
  }
}
