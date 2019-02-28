package com.maods.bctest.EOS;

/**
 * Created by MAODS on 2019/2/28.
 */

public class BOSMainNet implements EOSUtils.EosNetParams{
    private static final String[] CANDIDATE_NOTES=new String[]{
            "https://api.bos.eosrio.io",
            "https://api.bossweden.org",
            "http://bosapi-one.eosstore.co:8888",
            "http://bosapi-two.eosstore.co:8888",
            "https://api.hellobos.one",
            "http://api.eoshexagon.com:20888",
            "https://bosmatrix.blockmatrix.network",
            "https://api.bos42.io",
            "https://api-bos.oraclechain.io",
            "http://bosafrique.eosnairobi.io:9588",
            "https://bos-api.eoseoul.io"
    };
    @Override
    public String getNetName() {
        return "bos_main_net";
    }

    @Override
    public String[] getCandidateNodes() {
        return CANDIDATE_NOTES;
    }

    @Override
    public String getSysTokenName() {
        return "BOS";
    }

    @Override
    public double getDoubleFromAsset(String asset) {
        return Double.parseDouble(asset.substring(0,asset.length()-4));
    }

    @Override
    public int getNetType(){
        return EOSUtils.MAINNET;
    }
}
