# graftrs-android

Developers! Please cover all network calls with the custom DialogBuilder.getInstance().showCustomDialog(context). At the end of the call    it must be dismissed by calling DialogBuilder.getInstance().cancelDialog(dialog) with the Dialog instance received from the previous call.  
