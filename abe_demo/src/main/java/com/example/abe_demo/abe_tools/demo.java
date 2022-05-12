package com.example.abe_demo.abe_tools;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class demo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Pairing bp = PairingFactory.getPairing("a.properties");
        System.out.println(bp.getZr().getClass());
        Element test = bp.getGT().newElement(new BigInteger("123456789098765432112345"));
        System.out.println(test);
    }
}
