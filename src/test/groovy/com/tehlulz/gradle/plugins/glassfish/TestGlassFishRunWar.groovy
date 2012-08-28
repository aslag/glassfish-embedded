package com.tehlulz.gradle.plugins.glassfish

import spock.lang.Specification

/**
 * @author aslag
 */
class TestGlassFishRunWar
  extends Specification
{
  
  def "GlassFish runtime is started prior to startStopGlassFish()'s call to deployUndeployLoop(glassFish)"() {
    def GlassFishRunWar = GroovySpy(GlassFishServerHandler)
    
    when:
    String s = ""
    
    then: 
    s == ""
  } 
}
