package com.maods.bctest.EOS;

import com.maods.bctest.ChainCommonOperations;

import java.util.List;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSOperations implements ChainCommonOperations {
    private static final String TAG="EOSOperations";

    @Override
    public List<String> getServerNode(){
        return EOSUtils.getAvailableSeeds();
    }
}
