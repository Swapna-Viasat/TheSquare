/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package com.hellobaytree.graftrs.shared.phone;

public interface OnSmsReceivedListener {
    void onSmsReceived(String code);
}
