package com.ether.web3test.service.wallet;

import com.ether.web3test.model.wallet.CredentialsWallet;
import com.ether.web3test.service.util.Web3jMetadataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service
@Slf4j
public class Web3JWalletServiceImpl implements Web3jWalletService {

    @Value("${key.storage.url}")
    private String keyStorage;

    @Autowired
    private Web3jMetadataProvider web3jMetadataProvider;

    /**
     * Method generates new wallet and return private key
     *
     * @param password custom`s password
     * @return privet key of wallet
     */
    //todo create init value of balance from main1
    public CredentialsWallet createWallet(String password) {
        CredentialsWallet result = null;
        try {
            String newWallet = WalletUtils.generateFullNewWalletFile(password, new File(keyStorage));
            Credentials credentials = WalletUtils.loadCredentials(password, keyStorage + File.separator + newWallet);
            String publicKey = credentials.getEcKeyPair().getPublicKey().toString();
            String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);

            result = CredentialsWallet.builder()
                    .accountNumber(credentials.getAddress())
                    .publicKey(publicKey)
                    .privateKey(privateKey)
                    .build();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CipherException | IOException e) {
            log.info("Create web3 waller ERROR (type error: {}), message: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return result;
    }

    public CredentialsWallet getCredentialsWallet(String filename, String pwd) {
        CredentialsWallet credentialsWallet = null;
        try {
            Credentials credentials = WalletUtils.loadCredentials(pwd, keyStorage + File.separator + filename);
            String publicKey = credentials.getEcKeyPair().getPublicKey().toString();
            String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            credentialsWallet = CredentialsWallet.builder()
                    .accountNumber(credentials.getAddress())
                    .publicKey(publicKey)
                    .privateKey(privateKey)
                    .build();
        } catch (Exception e) {
            log.error("Getting credentials wallet by filename({}) and password ({}) ERROR, message: {}", filename, pwd, e.getMessage());
        }
        return credentialsWallet;
    }

    /**
     * Getting credentials by private keys
     *
     * @param privateKey private key of value
     * @return credentials entity
     */
    public Credentials getCredentialByPrivateKey(String privateKey) {
        Credentials credentials = null;
        try {
            credentials = Credentials.create(privateKey);
        } catch (Exception e) {
            log.error("Getting credentials by private key ERROR, message: {}", e.getMessage());
        }
        return credentials;
    }

    public BigInteger getBalanceByAccountNumber(String accountNumber) {
        BigInteger result = null;
        try {
            Web3j web3j = web3jMetadataProvider.getWeb3j();
            EthGetBalance ethGetBalance = web3j
                    .ethGetBalance(accountNumber, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            //in wei
            result = ethGetBalance.getBalance();
        } catch (Exception e) {
            log.error("Getting balance error, message: {}", e.getMessage());
        }
        return result;
    }
}
