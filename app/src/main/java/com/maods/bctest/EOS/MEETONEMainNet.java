package com.maods.bctest.EOS;

/**
 * Created by MAODS on 2019/2/28.
 */

public class MEETONEMainNet implements EOSUtils.EosNetParams {
    private static final String[] CANDIDATE_NOTES=new String[]{
            "http://mainnet.eosio.sg",
            "http://mars.fn.eosbixin.com",
            "http://api1.acroeos.one",
            "https://telosseed.ikuwara.com:8889",
            "http://bp.cryptolions.io",
            "http://api.eosn.io",
            "http://api-mainnet.starteos.io",
            "http://mainnet.eosnairobi.io",
            "http://api-meetone.eossv.org",
            "http://api.nytelos.com",
            "http://api-eos.blckchnd.com",
            "http://meetone.eossweden.eu",
            "http://api.eostribe.io",
            "http://api-meetone.eosbeijing.one",
            "http://node1.eosphere.io",
            "http://mainnet.genereos.io",
            "http://api.eosvenezuela.io:8888"
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
