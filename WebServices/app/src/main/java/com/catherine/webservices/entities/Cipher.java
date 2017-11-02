package com.catherine.webservices.entities;

public abstract class Cipher {

    public abstract String encrypt(String message);

    public abstract String decrypt(String message) throws Exception;
}