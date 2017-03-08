package com.sickoorange.superflashlight;

/**
 * Created by SickoOrange
 * on 2017/3/8
 * Email: heylbly@gmail.com
 */

public class MessageEvent {
    public static class stateChanged {
        private boolean status;

        public stateChanged(boolean status) {
            this.status = status;
        }

        public boolean getStatus() {
            return status;
        }
    }

    public static class cameraError {
        private String errorMessage;

        public cameraError(String message) {
            errorMessage = message;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class storboScopeChanged{
        private boolean status;
        public storboScopeChanged(boolean status){
            this.status=status;
        }

        public boolean getStatus() {
            return status;
        }
    }


}
