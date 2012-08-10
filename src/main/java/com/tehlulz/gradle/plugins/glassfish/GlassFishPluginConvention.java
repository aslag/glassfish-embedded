package com.tehlulz.gradle.plugins.glassfish;

public class GlassFishPluginConvention
{

  private Integer httpPort = 8080;

  /**     
   * Returns the TCP port for Jetty to listen on for incoming HTTP requests.
   */ 
  public Integer getHttpPort() {
      return httpPort;
  }       
      
  public void setHttpPort(Integer httpPort) {
      this.httpPort = httpPort;
  }   
}
