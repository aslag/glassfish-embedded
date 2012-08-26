package com.tehlulz.gradle.plugins.testsupport;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.testfixtures.internal.ProjectBuilderImpl;

/**
 * <p>This file was copied in its entirety from 
 * org.gradle.testfixtures.ProjectBuilder.</p>  
 * 
 * <p>As of this writing, Gradle testfixtures are not published such that I can 
 * simply add them as a binary dependency.  Instead, I've copied it here.</p>
 * 
 * <p>Creates dummy instances of {@link org.gradle.api.Project} which you can use in testing custom task and plugin
 * implementations.</p>
 *
 * <p>To create a project instance:</p>
 *
 * <ol>
 *
 * <li>Create a {@code ProjectBuilder} instance by calling {@link #builder()}.</li>
 *
 * <li>Optionally, configure the builder.</li>
 *
 * <li>Call {@link #build()} to create the {@code Project} instance.</li>
 *
 * </ol>
 *
 * <p>You can reuse a builder to create multiple {@code Project} instances.</p>
 * 
 * @author aslag
 * 
 */
public class ProjectBuilder
{
  private File projectDir;

  private String name = "test";

  private Project parent;

  private ProjectBuilderImpl impl = new ProjectBuilderImpl();

  /**
   * Creates a project builder.
   *
   * @return The builder
   */
  public static ProjectBuilder builder()
  {
    return new ProjectBuilder();
  }

  /**
   * Specifies the project directory for the project to build.
   *
   * @param dir The project directory
   * @return The builder
   */
  public ProjectBuilder withProjectDir(File dir)
  {
    projectDir = dir;
    return this;
  }

  /**
   * Specifies the name for the project
   *
   * @param name project name
   * @return The builder
   */
  public ProjectBuilder withName(String name)
  {
    this.name = name;
    return this;
  }

  /**
   * Specifies the parent project. Use it to create multi-module projects.
   *
   * @param parent parent project
   * @return The builder
   */
  public ProjectBuilder withParent(Project parent)
  {
    this.parent = parent;
    return this;
  }

  /**
   * Creates the project.
   *
   * @return The project
   */
  public Project build()
  {
    if (parent != null) {
      return impl.createChildProject(name, parent, projectDir);
    }
    return impl.createProject(name, projectDir);
  }

}
