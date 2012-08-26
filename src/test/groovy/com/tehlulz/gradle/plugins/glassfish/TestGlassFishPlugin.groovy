package com.tehlulz.gradle.plugins.glassfish

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin

import spock.lang.Specification

import com.tehlulz.gradle.plugins.testsupport.HelperUtil

/**
 * <p>Borrows heavily from org.gradle.api.plugins.jetty.JettyPluginTest.groovy.</p>
 * 
 * @author aslag
 *
 */
class TestGlassFishPlugin
extends Specification {

  def "tehlulz-glassfish-embedded applies war plugin and adds convention to project"() {
    Project project = HelperUtil.createRootProject()

    when:
    new GlassFishPlugin().apply(project)

    then:
    project.getPlugins().hasPlugin(WarPlugin)
    project.convention.plugins.get('glassfish-embed') instanceof GlassFishPluginConvention
  }

  def "tehlulz-glassfish-embedded applies tasks to project and that those tasks have appropriate configuration options"() {
    Project project = HelperUtil.createRootProject()

    when:
    new GlassFishPlugin().apply(project)

    then:
    def task = project.tasks[GlassFishPlugin.GLASSFISH_RUN_WAR]
    task instanceof GlassFishRunWar
    hasTaskDep(task, WarPlugin.WAR_TASK_NAME)
    hasTaskDep(task, JavaPlugin.CLASSES_TASK_NAME)
    task.httpPort == project.httpPort
  }
 
  def "tehlulz-glassfish-embedded adds mappings to new GlassFish tasks"() {
    Project project = HelperUtil.createRootProject()
    
    when:
    new GlassFishPlugin().apply(project)
    
    then:
    def task = project.tasks.add('customWar', GlassFishRunWar)
    hasTaskDep(task, JavaPlugin.CLASSES_TASK_NAME)
    task.httpPort == project.httpPort
  }
   
  boolean hasTaskDep(Task task, String expectedDepName) 
  { 
    Set<? extends Task> tasks = task.taskDependencies.getDependencies(task)
    
    if (tasks.find { it.name.contains(expectedDepName) } != null) 
     return true
    
    // if we get this far, we need to look at deps of tasks in Set 'tasks'
    for (Task t : tasks) {
      return hasTaskDep(t, expectedDepName)  
    }
  }
}
