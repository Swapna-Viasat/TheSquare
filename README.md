# graftrs-android

Developers! Please cover all network calls with the custom DialogBuilder.showCustomDialog(context). At the end of the call    it must be dismissed by calling DialogBuilder.cancelDialog(dialog) with the Dialog instance received from the previous call.  
