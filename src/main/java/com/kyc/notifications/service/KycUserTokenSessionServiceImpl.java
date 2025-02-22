package com.kyc.notifications.service;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.MessageData;
import com.kyc.core.model.jwt.JwtData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.core.rest.feign.common.KycUserClient;
import com.kyc.core.security.jwt.KycUserTokenSessionService;
import com.kyc.core.util.TokenUtil;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class KycUserTokenSessionServiceImpl implements KycUserTokenSessionService {

    @Autowired
    private KycUserClient kycUserClient;
    @Autowired
    private KycMessages kycMessages;

    @Override
    public Jwt retrieveInfoUserToken(String token) {

        try{
            ResponseData<JwtData> response = kycUserClient.sessionChecking(token);
            return TokenUtil.transform(token,response.getData());
        }
        catch(FeignException e){

            MessageData messageData = kycMessages.getMessage("");
            throw KycRestException.builderRestException()
                    .exception(e)
                    .inputData(token)
                    .status(HttpStatus.FORBIDDEN)
                    .errorData(messageData)
                    .build();

        }
    }
}
