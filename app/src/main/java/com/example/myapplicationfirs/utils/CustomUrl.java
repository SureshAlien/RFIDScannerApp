package com.example.myapplicationfirs.utils;

/**
 * Created by pragnya on 9/3/18.
 */


public class CustomUrl {
    CustomUrl(){
        //do nothing here, default constructor
    }

    private static String SERVER_ADDRESS ;

    public static final String URL_SCHEME = "http";
    public static final String API = "api";
    public static final String API_RESOURCE = "resource";
    public static final String API_METHOD = "method";

    //added on 27Oct 2017 to reflect changes made in the app name (new app name is motoinventory_tracker, old one renfield)
    //begin change
    private static final String ERPNEXT_APPNAME = "nhance";
    private static final String DOT_STRING_IN_PATH_NAME = ".";
    private static final String CUSTOM_API = "packing_items_api";
    private static final String API_PATH_NAME = ERPNEXT_APPNAME+DOT_STRING_IN_PATH_NAME+CUSTOM_API+DOT_STRING_IN_PATH_NAME;
    public static final String COMPANY_NAME = "HSR Services";
    public static final String COMPANY_ABBR = "HSR";

    //end change


    public static final String LOGIN_URL = "login";
    public static final String LOGOUT_URL = "logout";

    //all the tables I am accessing in the backend
    public static final String WAREHOUSE_TABLE="Warehouse";
    public static final String SERIAL_NO = "Serial No";
    public static final String ITEM_TABLE = "Item";
    public static final String CUSTOMER_TABLE ="Customer" ;
    public static final String RFID_TAG_HISTORY_TABLE ="RFID Tag Info" ;

    //added on 25th Oct to accommodate the requirement to tie the user to a particulr warehouse
    public static final String USER_TABLE="User";
    public static final String CONTROL_DOCUMENT_TABLE = "ControlDocument";
    public static final String CONTROL_DOCUMENT_ITEM_TABLE ="ControlDocument Item" ;


    //all the api method calls to login/reset password, login stuff
    public static final String GET_LOGGED_USER = "frappe.auth.get_logged_user";
    public static final String RESEND_THE_PASS_URL = "frappe.core.doctype.user.user.reset_password";

    //all api method calls related to receive vehicle from RE

    //fetch_tag_packing_details


    //transform items apis
    private static final String TRANSFORM_API = "mprp_txfm_api";

    private static final String ALTERNATE_API_PATH_NAME = ERPNEXT_APPNAME+DOT_STRING_IN_PATH_NAME+TRANSFORM_API+DOT_STRING_IN_PATH_NAME;

    private static final String ISSUE_FROM_STOCK_API = "mprp_issue_items_from_stock";


    //rfid_App custom apis path
    public static final String GET_PERMITTED_DOCTYPE_DATA   =API_PATH_NAME+"get_permitted_doctype_data" ;
    public static final String ASSOCIATE_DOCTYPE_RFID_TAGS   =API_PATH_NAME+"associate_doctype_rfid_tags" ;
    public static final String SAMPLE_UPDATE   =API_PATH_NAME+"sample_update" ;
    public static final String UPDATE_RFID_TAG_DETAILS_CHILD_DOC   =API_PATH_NAME+"update_rfid_tag_details_child_doc" ;
    public static final String CREATE_RFID_TAG_DETAILS_DOC   =API_PATH_NAME+"create_rfid_tag_details_doc" ;

    //packig details api

    private static final String ERPNEXT_APPNAME_TEMP = "rfid";
    private static final String DOT_STRING_IN_PATH_NAME_TEMP = ".";
    private static final String CUSTOM_API_TEMP = "api";
    private static final String API_PATH_NAME_TEMP = ERPNEXT_APPNAME_TEMP+DOT_STRING_IN_PATH_NAME_TEMP+CUSTOM_API_TEMP+DOT_STRING_IN_PATH_NAME_TEMP;


    public static final String FETCH_TAG_PACKING_DETAILS   =API_PATH_NAME+"fetch_tag_packing_details" ;
    public static final String TEST_PD_FROM_ANDROID   =API_PATH_NAME+"test_pd_from_android" ;
    public static final String FETCH_SI_PIPB_DETAILS   =API_PATH_NAME+"fetch_si_pipb_details" ;
    public static final String FETCH_SI_PIPB_DETAILS_SISCREEN  =API_PATH_NAME+"fetch_si_pb_details_siscreen" ;
    public static final String MAKE_DELIVERY_NOTE = API_PATH_NAME+"create_delivery_note";

    //new version with planned design
    public static final String CREATE_PI_DOC = API_PATH_NAME+"create_pi_doc";
    public static final String GET_PITEMS_AC_TO_BOX_NAME = API_PATH_NAME+"get_pitems_ac_to_box_name";
    public static final String CREATE_PB_DOC = API_PATH_NAME+"create_pb_doc";
    public static final String GET_PACKING_ITEM_DETAILS_AC_TO_RFID = API_PATH_NAME+"get_packing_item_details_ac_to_rfid";
    public static final String GET_PACKING_BOX_DETAILS_AC_TO_RFID = API_PATH_NAME+"get_packing_box_details_ac_to_rfid";
    public static final String CREATE_STOCK_ENTRY_TRANSFER = API_PATH_NAME+"craete_stock_entry_transfer";
    public static final String MOVE_TO_BOX = API_PATH_NAME+"move_to_box";


    public static String getServerAddress() {
        return SERVER_ADDRESS;
    }

    public static void setServerAddress(String serverAddress) {
        SERVER_ADDRESS = serverAddress;
        System.out.println(" ************From Custo url set server address ************************ : ");
    }


}
