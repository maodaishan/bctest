package com.maods.bctest.EOS;

/**
 * Created by MAODS on 2019/2/28.
 */

public class BOSTestNet implements EOSUtils.EosNetParams {
    private static final String[] CANDIDATE_NOTES=new String[]{
            "http://47.254.82.241:80",
            "http://47.254.134.167:80",
            "http://49.129.133.66:80",
            "http://8.208.9.182:80",
            "http://47.91.244.124:80",
            "http://120.197.130.117:8020",
            "http://bos-testnet.meet.one:8888",
            "http://bos-testnet.mytokenpocket.vip:8890",
            "https://bos-testnet.eosphere.io",
            "https://boscore.eosrio.io",
            "https://api.bostest.alohaeos.com"
    };
    @Override
    public String getNetName() {
        return "bos_test_net";
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
        return EOSUtils.TESTNET;
    }
}
