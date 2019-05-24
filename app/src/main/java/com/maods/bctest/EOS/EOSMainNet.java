package com.maods.bctest.EOS;
/**
 * Created by MAODS on 2019/2/28.
 */

public class EOSMainNet implements EOSUtils.EosNetParams {
    private String[] CANDIDATE_NODES=new String[]{
            "http://mainnet.meet.one",
            "http://api-mainnet.starteos.io",
            "http://api.eosn.io",
            "http://api1.eosasia.one"
    };
    @Override
    public String getNetName() {
        return "eos_main_net";
    }

    @Override
    public String[] getCandidateNodes() {
        return CANDIDATE_NODES;
    }

    @Override
    public String getSysTokenName() {
        return "EOS";
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
