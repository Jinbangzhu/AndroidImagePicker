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
         * extra key for position
         */
        public static final String POSITION = "position";

        public static final String IMAGEDISPLAY = "imageDisplay";
        /**
         * extra is show camera
         */
        public static final String SHOWCAMERA = "showCamera";


        public static final String RESULT_ITEMS = "result_items";

        public static final String PHOTO_PATH = "mCurrentPhotoPath";



        public static final int RESULT_CODE_DONE = 0x1;
        public static final int RESULT_CODE_REFRESH = 0x2;
        public static final int RESULT_CODE_CANCEL = 0x4;
        public static final int REQUEST_CODE_PREVIEW = 0x3;
        public static final int REQUEST_CODE_PICKUP = 0x5;
        public static final int REQUEST_CODE_TAKEPHOTO = 0x6;


    }
}
