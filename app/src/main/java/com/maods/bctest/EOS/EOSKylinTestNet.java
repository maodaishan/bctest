package com.maods.bctest.EOS;


/**
 * Created by MAODS on 2019/2/28.
 */

public class EOSKylinTestNet implements EOSUtils.EosNetParams {
    private static final String[] CANDIDATE_NOTES=new String[]{
            "http://39.108.231.157:30065",
            "http://api.jeda.one",
            "http://eosapi.nodepacific.com:8888",
            "http://api.eostribe.io",
            "http://fn001.eossv.org:80",
            "http://api-mainnet.starteos.io",
            "http://api.eosn.io",
            "http://api.tokenika.io",
            "http://mainnet.eoscanada.com"
    };
    @Override
    public String getNetName() {
        return "kylin_test_net";
    }

    @Override
    public String[] getCandidateNodes() {
        return CANDIDATE_NOTES;
    }

    @Override
    public String getSysTokenName() {
        return "SYS";
    }

    @Override
    public double getDoubleFromAsset(String asset) {
        return Double.parseDouble(asset.substring(0,asset.length()-4));
    }

    @Override
    public int getNetType(){
        return EOSUtils.TESTNET;
    }
}
