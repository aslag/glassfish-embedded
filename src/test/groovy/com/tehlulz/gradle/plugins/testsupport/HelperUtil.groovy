package com.tehlulz.gradle.plugins.testsupport

import org.gradle.api.internal.project.DefaultProject
import org.junit.rules.TemporaryFolder

/**
 * <p>This file was copied in its entirety from 
 * org.gradle.util.HelperUtil.</p>  
 * 
 * <p>As of this writing, Gradle testfixtures are not published such that I can 
 * simply add them as a binary dependency.  Instead, I've copied it here.</p>
 * 
 * @author aslag
 */
class HelperUtil {
  static DefaultProject createRootProject() {
    createRootProject(new TemporaryFolder().newFolder())
  }

  static DefaultProject createRootProject(File rootDir) {
    return ProjectBuilder
    .builder()
    .withProjectDir(rootDir)
    .build()
  }
}
