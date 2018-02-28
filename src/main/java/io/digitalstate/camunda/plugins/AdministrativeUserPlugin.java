package io.digitalstate.camunda.plugins;

import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;


public class AdministrativeUserPlugin extends AbstractProcessEnginePlugin {

  private Logger LOGGER = LoggerFactory.getLogger(AdministrativeUserPlugin.class);

  protected String administratorUserName;
  protected String administratorPassword;
  protected String administratorFirstName;
  protected String administratorLastName;
  protected String administratorEmail;

  public void postProcessEngineBuild(ProcessEngine engine) {

      final IdentityService identityService = engine.getIdentityService();

      // If the Identity Service is Read Only then do not create a admin user
      if(identityService.isReadOnly()) {
        LOGGER.info("Identity service provider is Read Only, not creating Admin user.");
        return;
      }

      // Ensure Username Param was provided
      if (administratorUserName == null || administratorUserName.length()==0){
        throw new RuntimeException("Administrator User Plugin not configured correctly: Username required");
      }

      // Check if the provided Admin username is currently in the system
      // If it is, then no need to go further.
      User singleResult = identityService.createUserQuery().userId(administratorUserName).singleResult();
      if (singleResult != null) {
        LOGGER.info("Admin user already exists, no need to create a Admin user");
        return;
      } else {
        LOGGER.info("Admin user does not currently exist");
      }

      // If the Admin username does not exist, then continue to check the additional params
      // Check for Password param
      if (administratorPassword == null || administratorPassword.length()==0){
        throw new RuntimeException("Administrator User Plugin not configured correctly: Password required");
      }

      // Check for First Name param
      if (administratorFirstName == null || administratorFirstName.length()==0){
        throw new RuntimeException("Administrator User Plugin not configured correctly: First Name required");
      }

      // Check for Last Name param
      if (administratorLastName == null || administratorLastName.length()==0){
        throw new RuntimeException("Administrator User Plugin not configured correctly: Last Name required");
      }

      // Check for Email param
      if (administratorEmail == null || administratorEmail.length()==0){
        throw new RuntimeException("Administrator User Plugin not configured correctly: Email required");
      }

      LOGGER.info("Generating Admin user and Admin");

      // Generate the Admin user based on the plugin's paramerters in the bpm-platform.xml file
      User user = identityService.newUser(administratorUserName);
      user.setFirstName(administratorFirstName);
      user.setLastName(administratorLastName);
      user.setPassword(administratorPassword);
      user.setEmail(administratorEmail);
      identityService.saveUser(user);


      final AuthorizationService authorizationService = engine.getAuthorizationService();

      LOGGER.info("Creating the camunda admin group");
      // create Administrator Group
      if(identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
        Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
        camundaAdminGroup.setName("camunda BPM Administrators");
        camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
        identityService.saveGroup(camundaAdminGroup);
      }

      LOGGER.info("Creating Admin group authrorizations for all built-in resources");
      // create Admin authorizations on all built-in resources
      for (Resource resource : Resources.values()) {
        if(authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
          AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
          userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
          userAdminAuth.setResource(resource);
          userAdminAuth.setResourceId(ANY);
          userAdminAuth.addPermission(ALL);
          authorizationService.saveAuthorization(userAdminAuth);
        }
      }

      LOGGER.info("Adding Admin user to camunda-admin group");
      identityService.createMembership(administratorUserName, "camunda-admin");
    }

    /**
     * @param username the Admin username
     */
    public void setAdministratorUserName(String username) {
      this.administratorUserName = username;
    }

    /**
     * @param password the Admin password
     */
    public void setAdministratorPassword(String password) {
      this.administratorPassword = password;
    }

    /**
     * @param username the Admin's first name
     */
    public void setAdministratorFirstName(String firstname) {
      this.administratorFirstName = firstname;
    }

    /**
     * @param lastname the Admin's lastname
     */
    public void setAdministratorLastName(String lastname) {
      this.administratorLastName = lastname;
    }

    /**
     * @param email the Admin's email
     */
    public void setAdministratorEmail(String email) {
      this.administratorEmail = email;
    }

  }