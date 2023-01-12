package com.rafaeldeluca.catalogo.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.rafaeldeluca.catalogo.entities.User;
import com.rafaeldeluca.catalogo.repositories.UserRepository;

@Component
public class JwtTokenEnhancer implements TokenEnhancer{

	@Autowired
	private UserRepository UserRepository;
	
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		User user = UserRepository.findByEmail(authentication.getName());
		Map<String, Object> myMap = new HashMap<String,Object>();
		myMap.put("userId", user.getId());
		myMap.put("userFirstName", user.getFirstName());
		myMap.put("userLastName", user.getLastName());
		
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
		token.setAdditionalInformation(myMap);
		
		// return token;
		return accessToken;
	}

}
