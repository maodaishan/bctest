package com.maods.bctest.EOS;

/**
 * Created by MAODS on 2019/2/28.
 */

public class MEETONEMainNet implements EOSUtils.EosNetParams {
    private static final String[] CANDIDATE_NOTES=new String[]{
            "https://fullnode.meet.one",
            "https://api.meetone.eostribe.io",
            "https://meetseed.ikuwara.com:8889",
            "https://api.meetone.alohaeos.com",
            "https://meetone.eossweden.eu",
            "http://api-meetone.eossf.net:8888",
            "https://meetone.eosphere.io",
            "https://meetone.eosn.io",
            "https://meetone.eosargentina.io"
    };
    @Override
    public String getNetName() {
        return "meetone_main_net";
    }

    @Override
    public String[] getCandidateNodes() {
        return CANDIDATE_NOTES;
    }

    @Override
    public String getSysTokenName() {
        return "MEETONE";
    }

    @Override
    public double getDoubleFromAsset(String asset) {
        if(asset.endsWith("RAM")){
            return Double.parseDouble(asset.substring(0,asset.length()-4));
        }else {
            return Double.parseDouble(asset.substring(0, asset.length() - 8));
        }
    }

    @Override
    public int getNetType(){
        return EOSUtils.MAINNET;
    }
}
