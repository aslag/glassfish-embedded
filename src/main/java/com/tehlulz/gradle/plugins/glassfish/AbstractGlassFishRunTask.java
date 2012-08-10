package com.tehlulz.gradle.plugins.glassfish;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aslag
 */
public abstract class AbstractGlassFishRunTask
  extends ConventionTask
{
  protected GlassFish glassFish;
  
  private static final Logger LOG = LoggerFactory.getLogger(AbstractGlassFishRunTask.class);

  private String contextPath;

  private Integer httpPort;
 
  @TaskAction
  protected void start()
  {
    LOG.info("Starting GlassFish from "+this.getClass().getName());
   
    //TODO: add flexible runtimejar loading here
    
    startStopGlassFish();
  }
  
  public void startStopGlassFish() {
    LOG.info("Configuring GlassFish for " + getProject());
    
    ProgressLoggerFactory progressLoggerFactory = getServices().get(ProgressLoggerFactory.class);
    ProgressLogger progressLogger = progressLoggerFactory.newOperation(AbstractGlassFishRunTask.class);
    progressLogger.setDescription("Start GlassFish Server");
    progressLogger.setShortDescription("Starting GlassFish");
    progressLogger.started();

    
    GlassFishProperties gfProps = new GlassFishProperties();
    
    
    //TODO: make port user-configurable
    gfProps.setPort("http-listener", getHttpPort()); // refer to JavaDocs for the details of this API.
    
    try {
      //TODO: do this only selectively
      Handler gfLogHandler = new FileHandler(getProject().getProjectDir().getPath()+"/glassfish-embedded.log", true);
      gfLogHandler.setLevel(Level.FINEST);
      gfLogHandler.setFormatter(new SimpleFormatter());
      
      // remember that "" picks out the root logger
      java.util.logging.Logger.getLogger("").addHandler(gfLogHandler);

    }
    catch (Exception ex) {
      LOG.error("Failed to configure GlassFish logging, logging only errors to console.");
    }
    
    try {
      glassFish = GlassFishRuntime.bootstrap().newGlassFish(gfProps);
      glassFish.start();
    }
    catch (Exception ex) {
      throw new GradleException("Failed to start GlassFish server.", ex);
    } finally {
      progressLogger.completed();
    }
    
    // may block until user signals an exit
    deployUndeployLoop();
    try {
      glassFish.stop();
    }
    catch (Exception ex) {
      throw new GradleException("Failed to stop GlassFish server.", ex);
    }
  }
 
  protected abstract void deployUndeployLoop();
  
  /**
   * Returns the TCP port for GlassFish to listen on for incoming HTTP requests.
   */
  public Integer getHttpPort() {
      return httpPort;
  }

  public void setHttpPort(Integer httpPort) {
      this.httpPort = httpPort;
  }

  /** 
   * Returns the context path to use to deploy the web application.
   */
  public String getContextPath() {
      return contextPath;
  }   

  public void setContextPath(String contextPath) {
      this.contextPath = contextPath;
  }   
}
