package com.tehlulz.gradle.plugins.glassfish;

import java.util.concurrent.Callable;

import org.glassfish.embeddable.GlassFishRuntime;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.bundling.War;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Deploys applications (particularly web applications packaged 
 * as WAR archives) in the GlassFish JEE 6 application server.</p>
 *
 * <p>Much of this was adapted from the maven-embedded-glassfish-plugin (http://download.java.net/maven/glassfish/org/glassfish/maven-embedded-glassfish-plugin/), and the Gradle-bundled Jetty plugin.</p>
 *
 * @author aslag
 *
 */
public class GlassFishPlugin implements Plugin<Project>
{
  static final Logger LOG = LoggerFactory.getLogger(GlassFishPlugin.class);
  
  protected static final String GLASSFISH_RUN_WAR = "glassFishRunWar";
  
  static GlassFishRuntime glassFishRuntime;
  
  @Override
  public void apply(Project project)
  {
    LOG.info("Applying glassfish-embed");
    
    project.getPlugins().apply(WarPlugin.class);
    GlassFishPluginConvention glassFishPluginConvention = new GlassFishPluginConvention();
    Convention convention = project.getConvention();
    convention.getPlugins().put("glassfish-embed", glassFishPluginConvention);
    
    configureMappingRules(project, glassFishPluginConvention);
    configureGlassFishRunWar(project);
  }
  
  private void configureMappingRules(final Project project, final GlassFishPluginConvention glassFishPluginConvention) {
	 project.getTasks().withType(AbstractGlassFishRunTask.class, new Action<AbstractGlassFishRunTask>() {
         public void execute(AbstractGlassFishRunTask abstractGlassFishRunTask) {
             configureAbstractGlassFishTask(project, glassFishPluginConvention, abstractGlassFishRunTask);
         }   
     }); 
	 }

  private void configureGlassFishRunWar(final Project project)
  {
    project.getTasks().withType(GlassFishRunWar.class, new Action<GlassFishRunWar>() {

      @Override
      public void execute(GlassFishRunWar glassFishRunWar)
      {
        glassFishRunWar.dependsOn(WarPlugin.WAR_TASK_NAME);
        glassFishRunWar.getConventionMapping().map("webApp", new Callable<Object>() {
          public Object call() throws Exception {
            return ((War) project.getTasks().getByName(WarPlugin.WAR_TASK_NAME)).getArchivePath();
          }
        });
      }
    });
    
    GlassFishRunWar glassFishRunWar = project.getTasks().add(GLASSFISH_RUN_WAR, GlassFishRunWar.class);
    glassFishRunWar.setDescription("Assembles the webapp into a war and deploys it using GlassFish.");
    glassFishRunWar.setGroup(WarPlugin.WEB_APP_GROUP);
    
  }
  
	protected void configureAbstractGlassFishTask(final Project project,
			final GlassFishPluginConvention glassFishPluginConvention,
			final AbstractGlassFishRunTask abstractGlassFishRunTask) {

	  //TODO: set bootstrap properties on glassfish server here
	  
	  //TODO: map convention variables (like glassFishRun's httpPort) here
    abstractGlassFishRunTask.getConventionMapping().map("contextPath", new Callable<Object>() {
      public Object call() throws Exception {
          return ((War) project.getTasks().getByName(WarPlugin.WAR_TASK_NAME)).getBaseName();
      }
    });
    abstractGlassFishRunTask.getConventionMapping().map("httpPort", new Callable<Object>() {
      public Object call() throws Exception {
          return glassFishPluginConvention.getHttpPort();
      }
    });

	}

  public JavaPluginConvention getJavaConvention(Project project)
  {
    return project.getConvention().getPlugin(JavaPluginConvention.class);
  }

  public WarPluginConvention getWarConvention(Project project)
  {
    return project.getConvention().getPlugin(WarPluginConvention.class);
  }

}
