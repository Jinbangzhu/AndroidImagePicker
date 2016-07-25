package com.cndroid.pickimagelib;

/**
 * Created by jinbangzhu on 7/25/16.
 */

public class Intents {
    private Intents() {
    }

    public static final class ImagePicker {
        /**
         * Title for actionbar
         */
        public static final String TITLE = "title";

        /**
         * Title for actionbar using resource
         */
        public static final String TITLERES = "titleRes";

        /**
         * Max size for chosen
         */
        public static final String LIMIT = "limit";

        /**
         * if defaultChosen is true, all the images will be selected
         */
        public static final String DEFAULTCHOSEN = "defaultChosen";

        /**
         * only scan DCIM folder
         */
        public static final String DCIMONLY = "dcimOnly";

        /**
         * will be chosen for #SELECTEDIMAGES#
         */
        public static final String SELECTEDIMAGES = "selectedImages";

        /**
         * ImageTime  after #STARTTIME#
         */
        public static final String STARTTIME = "startTime";


        /**
         * extra key for pickupImageHolder
         */
        public static final String PICKUPIMAGEHOLDER = "pickupImageHolder";
        /**
         * extra key for imageItem
         */
        public static final String IMAGEITEM = "imageItem";

        /**
         *  extra key for position
         */
        public static final String POSITION = "position";


    }
}
