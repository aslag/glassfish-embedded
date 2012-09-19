package com.tehlulz.gradle.plugins.glassfish;

/**
 * Configuration:
 * <pre>
 *   glassFishRunWar {
 *     httpPort = 8081
 *     ssl = false
 *     keyStore = '/path/to/keystore.jks'
 *     keyStorePassword = 'password'
 *     keyStoreAlias = 'alias'
 *     trustStore = '/path/to/truststore.jks'
 *     trustStorePassword = 'truststorepassword'
 *   }
 * </pre>
 * 
 * TODO: Rework configuration so that it specification of multiple listeners is possible (perhaps a listener {} block)
 * TODO: Figure out some sensible rules for determining keyStoreAlias if none is provided and more than one cert is in keyStore
 * 
 * 
 * @author mdye
 *
 */
public class GlassFishPluginConvention
{

  private Integer httpPort = 8080;
  
  private boolean ssl = false;
  
  private String keyStore;
  
  private String keyStorePassword;
  
  private String keyStoreAlias;
  
  private String trustStore;
  
  private String trustStorePassword;

  /**     
   * Returns the TCP port for Jetty to listen on for incoming HTTP requests.
   */ 
  public Integer getHttpPort() {
    return httpPort;
  }       
      
  public void setHttpPort(Integer httpPort) {
      this.httpPort = httpPort;
  }
  
  public boolean getSsl() {
    return ssl;
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
