package com.maods.bctest.EOS;

import java.util.List;
import java.util.Map;

/**
 * Created by MAODS on 2018/8/14.
 */

public class ContextFreeAction extends Action {
    public ContextFreeAction(String account, String name, String data, String hexData, List<Map<String, String>> authorization) {
        super(account, name, /*data, */hexData, authorization);
    }
}
