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
    private static final String CUSTOM_API = "mprp_android_api";
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
    //added on 25th Oct to accommodate the requirement to tie the user to a particulr warehouse
    public static final String USER_TABLE="User";
    public static final String CONTROL_DOCUMENT_TABLE = "ControlDocument";
    public static final String CONTROL_DOCUMENT_ITEM_TABLE ="ControlDocument Item" ;


    //all the api method calls to login/reset password, login stuff
    public static final String GET_LOGGED_USER = "frappe.auth.get_logged_user";
    public static final String RESEND_THE_PASS_URL = "frappe.core.doctype.user.user.reset_password";

    //all api method calls related to receive vehicle from RE

    public static final String VALIDATE_SERIAL_NO = API_PATH_NAME+"validate_serial_no";
    public static final String SUBMIT_STOCK_ENTRY = API_PATH_NAME+"submit_stock_entry";
    public static final String MAKE_STOCK_ENTRY = API_PATH_NAME+"make_stock_entry";
    public static final String SEND_MAIL = API_PATH_NAME+"send_IBNR_mail";
    public static final String MAKE_NEW_SERIAL_NO_ENTRY = API_PATH_NAME+"make_new_serial_no_entry";







    //default WH string..using this in ReceiveFromRE createStockEntry method, have to change this hard coded value, replace with a dummy string, let server throw an error
    public static final String DEFAULT_WH = "Finished Goods - "+COMPANY_ABBR;

    //to get requested items table
    public static final String GET_REQUESTED_ITEM_DETAILS =API_PATH_NAME+"get_requested_items_details" ;
    //to input the requested Items
    public static final String SEND_REQUESTED_ITEMS_LIST = API_PATH_NAME+"set_requested_items_details";

    //transform items apis
    private static final String TRANSFORM_API = "mprp_txfm_api";

    private static final String ALTERNATE_API_PATH_NAME = ERPNEXT_APPNAME+DOT_STRING_IN_PATH_NAME+TRANSFORM_API+DOT_STRING_IN_PATH_NAME;
    public static final String GET_TRANSFORMED_ITEM_DETAILS = ALTERNATE_API_PATH_NAME+"get_transformed_items_details";
    public static final String SEND_TRANSFORMED_ITEMS_LIST = ALTERNATE_API_PATH_NAME+"set_transformed_items_details";

    private static final String ISSUE_FROM_STOCK_API = "mprp_issue_items_from_stock";
    private static final String ISSUE_API_PATH = ERPNEXT_APPNAME+DOT_STRING_IN_PATH_NAME+ISSUE_FROM_STOCK_API+DOT_STRING_IN_PATH_NAME;
    public static final String GET_ISSUE_MATERIAL_FROM_STOCK_ITEM_DETAILS = ALTERNATE_API_PATH_NAME+"get_issue_material_from_stock_details";
    public static final String SEND_ISSUE_ITEMS_FROM_STOCK_LIST = ALTERNATE_API_PATH_NAME+"set_issued_items_from_stock_details";



    public static String getServerAddress() {
        return SERVER_ADDRESS;
    }

    public static void setServerAddress(String serverAddress) {
        SERVER_ADDRESS = serverAddress;
        System.out.println(" ************From Custo url set server address ************************ : ");


    }
}
