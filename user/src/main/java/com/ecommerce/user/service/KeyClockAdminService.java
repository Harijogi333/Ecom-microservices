package com.ecommerce.user.service;

import com.ecommerce.user.dto.UserRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeyClockAdminService {

    @Value("${keycloak.admin.username}")
    private String adminUserName;
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    @Value(("${keycloak.admin.server-url}"))
    private String keyCloakServerUrl;
    @Value("${keycloak.admin.realm}")
    private String realm;
    @Value("${keycloak.admin.clientId}")
    private String clientId;
    @Value("${keycloak.admin.clientUuid}")
    private String clientUuid;

    private final RestTemplate restTemplate=new RestTemplate();

    public String getAccessToken()
    {
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("client_id",clientId);
        params.add("username",adminUserName);
        params.add("password",adminPassword);
        params.add("grant_type","password");

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> httpEntity=new HttpEntity<>(params,httpHeaders);

        ResponseEntity<Map> response=restTemplate.postForEntity(keyCloakServerUrl+"/realms/"+realm+"/protocol/openid-connect/token",httpEntity,Map.class);



        return (String) response.getBody().get("access_token");
    }

    public String createUser(String token, UserRequest userRequest)
    {
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(token);

        Map<String,Object> userPayload=new HashMap<>();
        userPayload.put("username",userRequest.getFirstName());
        userPayload.put("email",userRequest.getEmail());
        userPayload.put("enabled",true);
        userPayload.put("firstName",userRequest.getFirstName());
        userPayload.put("lastName",userRequest.getLastName());


        Map<String,Object> credentials=new HashMap<>();
        credentials.put("type","password");
        credentials.put("value",userRequest.getPassword());
        credentials.put("temporary",false);

        userPayload.put("credentials", List.of(credentials));

        HttpEntity<Map<String,Object>>  httpEntity=new HttpEntity<>(userPayload,httpHeaders);

        String url=keyCloakServerUrl+"/admin/realms/"+realm+"/users";

        ResponseEntity<String> response=restTemplate.postForEntity(url,httpEntity,String.class);

        if(!HttpStatus.CREATED.equals(response.getStatusCode()))
        {
            throw new RuntimeException("Failed to create user in keycloak "+response.getBody());
        }

        URI location= response.getHeaders().getLocation();

        if(location==null)
        {
            throw new RuntimeException("keycloak didn't return the response header "+response.getBody());
        }

        String path=location.getPath();

        return path.substring(path.lastIndexOf("/")+1);

    }


    public Map<String,Object> getRealmRoleRepresentation(String token,String roleName)
    {

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        HttpEntity<Void> httpEntity=new HttpEntity<>(httpHeaders);

        String url=keyCloakServerUrl+"/admin/realms/"+realm+"/clients/"+clientUuid+"/roles/"+roleName;

        ResponseEntity<Map> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,Map.class);

        return response.getBody();
    }

    public void assignRoleToUser(String username,String roleName,String userId)
    {
        String token=getAccessToken();

        Map<String,Object> roleRep=getRealmRoleRepresentation(token,roleName);

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Map<String,Object>>> httpEntity=new HttpEntity<>(List.of(roleRep),httpHeaders);

        String url=keyCloakServerUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/clients/"+clientUuid;

        ResponseEntity<Void> response=restTemplate.postForEntity(url,httpEntity,Void.class);


        if(!response.getStatusCode().is2xxSuccessful())
        {
            throw new RuntimeException("Failed to assign role "+roleName
                            +" to the user "+userId+" :http "+response.getStatusCode());

        }




    }



}
