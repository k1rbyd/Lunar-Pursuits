#include <iostream>
#include <mysql.h>
#include <cstring>
#include <cctype>

using namespace std;

const char* HOST = "localhost";
const char* USER = "root";
const char* PASS = "Qwerty#80085";

MYSQL* createConnection() 
{
    MYSQL* obj = mysql_init(NULL);
    if (!obj) 
    {
        cout << "ERROR: MySQL object could not be created." << endl;
        return NULL;
    }

    if (!mysql_real_connect(obj, HOST, USER, PASS, "PROJECT1", 3306, NULL, 0)) 
    {
        cout << "ERROR: Some database info is wrong or does not exist." << endl;
        cout << mysql_error(obj) << endl;
        return NULL;
    }

    return obj;
}

string toUpper(const string& str) 
{
    string result = str;
    for (size_t i = 0; i < result.length(); i++) 
    {
        result[i] = toupper(result[i]);
    }
    return result;
}

int main() 
{
    MYSQL* obj = createConnection();
    if (!obj) 
    {
        return 1;
    }

    string username;
    string password;
    int productid;
    string productname;
    int quantity;
    int price;
    string seller_name;
    bool program_running = true;
    int answer;
    string sentence_aux;
    bool access = false;
    bool access_r = false;

    int response0;
    cout << "Are you ROOT?" << endl;
    cout << "[1] : Yes" << endl;
    cout << "[2] : No" << endl;
    cout << "Your Response:";
    cin >> response0;
    cout << endl;

    if (response0 == 1) 
	{
        string rootPassword;
        cout << "Enter the root password sir: ";
        cin >> rootPassword;
        cout << endl;

        if (strcmp(rootPassword.c_str(), PASS) == 0) 
		{
            access_r = true;
            cout << "Access granted as ROOT." << endl;
            cout << endl << endl;
        }
        else 
		{
            cout << "Access denied. Incorrect root password." << endl;
            return 1;
        }
    }

    if (access == false && access_r == false) 
	{
        cout << "Enter your user ID: ";
        cin >> username;
        cout << "Enter your password: ";
        cin >> password;

        string query = "SELECT userid FROM user WHERE userid = ? AND passcode = ?";

        MYSQL_STMT* stmt = mysql_stmt_init(obj);
        if (!stmt) 
		{
            cout << "ERROR: Statement initialization failed." << endl;
            return 1;
        }

        if (mysql_stmt_prepare(stmt, query.c_str(), query.length())) 
		{
            cout << "ERROR: Statement preparation failed: " << mysql_stmt_error(stmt) << endl;
            return 1;
        }

        MYSQL_BIND bind[2];
        memset(bind, 0, sizeof(bind));

        bind[0].buffer_type = MYSQL_TYPE_STRING;
        bind[0].buffer = (void*)username.c_str();
        bind[0].buffer_length = username.length();

        bind[1].buffer_type = MYSQL_TYPE_STRING;
        bind[1].buffer = (void*)password.c_str();
        bind[1].buffer_length = password.length();

        if (mysql_stmt_bind_param(stmt, bind)) 
		{
            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(stmt) << endl;
            return 1;
        }

        if (mysql_stmt_execute(stmt)) 
		{
            cout << "ERROR: Statement execution failed: " << mysql_stmt_error(stmt) << endl;
            return 1;
        }

        MYSQL_BIND result;
        memset(&result, 0, sizeof(result));
        int result_userid = -1;

        result.buffer_type = MYSQL_TYPE_LONG;
        result.buffer = &result_userid;

        if (mysql_stmt_bind_result(stmt, &result)) 
		{
            cout << "ERROR: Result binding failed: " << mysql_stmt_error(stmt) << endl;
            return 1;
        }

        if (mysql_stmt_fetch(stmt) == 0) 
		{
            access = true;
            cout << "Access granted." << endl;
            cout << endl << endl;
        }
        else {
            access = false;
            cout << "Access denied." << endl;
            return 1;
        }

        mysql_stmt_close(stmt);
    }

    if (access_r) 
	{
        while (program_running) 
		{
        	cout<<endl;
            cout << "What do you want to do Sir:" << endl;
            cout << "[1] : Edit User Database" << endl;
            cout << "[2] : Edit Product Database" << endl;
            cout << "[3] : Exit" << endl;
            cout << "Your choice sir:";
            int choice1_r;
            int choice2_r;
            cin >> choice1_r;
            cin.ignore(100, '\n');
            cout << endl;

            if (choice1_r == 1) 
			{
                bool run_r = true;
                while (run_r) 
				{
					cout<<endl;
                    cout << "What do you want to do in the User Database Sir:" << endl;
                    cout << "[1] : Create a new user" << endl;
                    cout << "[2] : Delete an existing user" << endl;
                    cout << "[3] : Display all the users" << endl;
                    cout << "[4] : Exit" << endl;
                    cout << "Your Choice Sir:";

                    cin >> choice2_r;
                    cin.ignore(100, '\n');
					cout<<endl;
	
                    if (choice2_r == 1) 
					{
                        string newUsername;
                        string newPassword;

                        cout << "Enter the new username: ";
                        cin >> newUsername;
                        cout << "Enter the new password: ";
                        cin >> newPassword;
                        cout<<endl;

                        sentence_aux = "INSERT INTO user(userid, passcode) VALUES (?, ?)";
                        MYSQL_STMT* insert_stmt = mysql_stmt_init(obj);

                        if (!insert_stmt) 
						{
                            cout << "ERROR: Insert statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(insert_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Insert statement preparation failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND insert_bind[2];
                        memset(insert_bind, 0, sizeof(insert_bind));

                        insert_bind[0].buffer_type = MYSQL_TYPE_STRING;
                        insert_bind[0].buffer = (void*)newUsername.c_str();
                        insert_bind[0].buffer_length = newUsername.length();

                        insert_bind[1].buffer_type = MYSQL_TYPE_STRING;
                        insert_bind[1].buffer = (void*)newPassword.c_str();
                        insert_bind[1].buffer_length = newPassword.length();

                        if (mysql_stmt_bind_param(insert_stmt, insert_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(insert_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "New user added successfully." << endl;
                        }

                        mysql_stmt_close(insert_stmt);
                    }

                    if (choice2_r == 2) 
					{
                        string deleteUser;

                        cout << "Enter the username of the user you want to delete: ";
                        cin >> deleteUser;
                        cout<<endl;

                        sentence_aux = "DELETE FROM user WHERE userid = ?";
                        MYSQL_STMT* delete_stmt = mysql_stmt_init(obj);

                        if (!delete_stmt) 
						{
                            cout << "ERROR: Delete statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(delete_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Delete statement preparation failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND delete_bind[1];
                        memset(delete_bind, 0, sizeof(delete_bind));

                        delete_bind[0].buffer_type = MYSQL_TYPE_STRING;
                        delete_bind[0].buffer = (void*)deleteUser.c_str();
                        delete_bind[0].buffer_length = deleteUser.length();

                        if (mysql_stmt_bind_param(delete_stmt, delete_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(delete_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "User deleted successfully." << endl;
                        }

                        mysql_stmt_close(delete_stmt);
                    }

                    if (choice2_r == 3) 
					{
                    	cout<<endl;
                        cout << "List of all users:" << endl;
                        MYSQL_RES* result_set;
                        MYSQL_ROW row;

                        if (mysql_query(obj, "SELECT * FROM user")) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else {
                            result_set = mysql_store_result(obj);
                            if (result_set) 
							{
                                while ((row = mysql_fetch_row(result_set))) 
								{
                                    cout << "UserID: " << row[0] << " | Passcode: " << row[1] << endl;
                                }
                                mysql_free_result(result_set);
                            }
                            else 
							{
                                cout << "No data found." << endl;
                            }
                        }
                    }

                    if (choice2_r == 4) 
					{
                        run_r = false;
                    }
                }
            }

            if (choice1_r == 2) 
			{
                bool run_r = true;
                while (run_r) 
				{
                	cout<<endl;
                    cout << "What do you want to do in the Product Database Sir:" << endl;
                    cout << "[1] : Create a new product" << endl;
                    cout << "[2] : Edit an existing product"<<endl;
                    cout << "[3] : Delete an existing product" << endl;
                    cout << "[4] : Display all the products" << endl;
                    cout << "[5] : Exit" << endl;
                    cout << "Your Choice Sir:";
                    int choice3_r;
                    cin >> choice3_r;
                    cin.ignore(100, '\n');
                    cout<<endl;

                    if (choice3_r == 1) 
					{
                        cout << "Enter the product ID: ";
                        cin >> productid;
                        cout << "Enter the product name: ";
                        cin.ignore();
                        getline(cin,productname);
                        cout << "Enter the quantity: ";
                        cin >> quantity;
                        cout << "Enter the price: ";
                        cin >> price;
                        cout << "Enter the seller name: ";
                        cin.ignore();
                        getline(cin,seller_name);
                        cout<<endl;
                        
						productname = toUpper(productname);
    					seller_name = toUpper(seller_name);

                        sentence_aux = "INSERT INTO products(productid, productname, quantity, price, seller_name) VALUES (?, ?, ?, ?, ?)";
                        MYSQL_STMT* insert_stmt = mysql_stmt_init(obj);

                        if (!insert_stmt) 
						{
                            cout << "ERROR: Insert statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(insert_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Insert statement preparation failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND insert_bind[5];
                        memset(insert_bind, 0, sizeof(insert_bind));

                        insert_bind[0].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[0].buffer = &productid;

                        insert_bind[1].buffer_type = MYSQL_TYPE_STRING;
						insert_bind[1].buffer = (void*)productname.c_str();
						insert_bind[1].buffer_length = productname.length();	

                        insert_bind[2].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[2].buffer = &quantity;

                        insert_bind[3].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[3].buffer = &price;

                        insert_bind[4].buffer_type = MYSQL_TYPE_STRING;
                        insert_bind[4].buffer = (void*)seller_name.c_str();
                        insert_bind[4].buffer_length = seller_name.length();

                        if (mysql_stmt_bind_param(insert_stmt, insert_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(insert_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "New product added successfully." << endl;
                        }

                        mysql_stmt_close(insert_stmt);
                    }


						if (choice3_r == 2) 
						{
						    cout << "Enter the product ID of the product you want to edit: ";
						    cin >> productid;
						    cout << "What do you want to edit for this product:" << endl;
						    cout << "[1] : Quantity" << endl;
						    cout << "[2] : Price" << endl;
						    cout << "Your Choice Sir:";
						    int choice_edit;
						    cin >> choice_edit;
						    cin.ignore(100, '\n');
						
						    if (choice_edit == 1) 
						    {
						        cout << "Enter the new quantity: ";
						        cin >> quantity;
						        cout << endl;
						
						        sentence_aux = "UPDATE products SET quantity = ? WHERE productid = ?";
						    }
						    else if (choice_edit == 2) 
						    {
						        cout << "Enter the new price: ";
						        cin >> price;
						        cout << endl;
						
						        sentence_aux = "UPDATE products SET price = ? WHERE productid = ?";
						    }
						    else 
						    {
						        cout << "Invalid choice." << endl;
						        continue;  // Go back to the product database options
						    }
						
						    MYSQL_STMT* update_stmt = mysql_stmt_init(obj);
						
						    if (!update_stmt) 
						    {
						        cout << "ERROR: Update statement initialization failed." << endl;
						        return 1;
						    }
						
						    if (mysql_stmt_prepare(update_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						    {
						        cout << "ERROR: Update statement preparation failed: " << mysql_stmt_error(update_stmt) << endl;
						        return 1;
						    }
						
						    MYSQL_BIND update_bind[2];
						    memset(update_bind, 0, sizeof(update_bind));
						
						    if (choice_edit == 1) 
						    {
						        update_bind[0].buffer_type = MYSQL_TYPE_LONG;
						        update_bind[0].buffer = &quantity;
						    }
						    else if (choice_edit == 2) 
						    {
						        update_bind[0].buffer_type = MYSQL_TYPE_LONG;
						        update_bind[0].buffer = &price;
						    }
						
						    update_bind[1].buffer_type = MYSQL_TYPE_LONG;
						    update_bind[1].buffer = &productid;
						
						    if (mysql_stmt_bind_param(update_stmt, update_bind)) 
						    {
						        cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(update_stmt) << endl;
						        return 1;
						    }
						
						    if (mysql_stmt_execute(update_stmt)) 
						    {
						        cout << "ERROR: " << mysql_error(obj) << endl;
						    }
						    else 
						    {
						        cout << "Product updated successfully." << endl;
						    }
						
						    mysql_stmt_close(update_stmt);
						}



                    if (choice3_r == 3) 
					{
                        cout << "Enter the product ID of the product you want to delete: ";
                        cin >> productid;
						cout<<endl;
                        sentence_aux = "DELETE FROM products WHERE productid = ?";
                        MYSQL_STMT* delete_stmt = mysql_stmt_init(obj);

                        if (!delete_stmt) 
						{
                            cout << "ERROR: Delete statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(delete_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Delete statement preparation failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND delete_bind[1];
                        memset(delete_bind, 0, sizeof(delete_bind));

                        delete_bind[0].buffer_type = MYSQL_TYPE_LONG;
                        delete_bind[0].buffer = &productid;

                        if (mysql_stmt_bind_param(delete_stmt, delete_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(delete_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "Product deleted successfully." << endl;
                        }

                        mysql_stmt_close(delete_stmt);
                    }

                    if (choice3_r == 4) 
					{
						cout<<endl;
                        cout << "List of all products:" << endl;
                        MYSQL_RES* result_set;
                        MYSQL_ROW row;

                        if (mysql_query(obj, "SELECT * FROM products")) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            result_set = mysql_store_result(obj);
                            if (result_set) 
							{
                                while ((row = mysql_fetch_row(result_set))) 
								{
                                    cout << "ProductID: " << row[0] << " | ProductName: " << row[1] << " | Quantity: " << row[2] << " | Price: " << row[3] << " | SellerName: " << row[4] << endl;
                                }
                                mysql_free_result(result_set);
                            }
                            else 
							{
                                cout << "No data found." << endl;
                            }
                        }
                    }

                    if (choice3_r == 5) 
					{
                        run_r = false;
                    }
                }
            }

            if (choice1_r == 3) 
			{
                program_running = false;
            }
            
        }
    }

    if(access)
    {
    	bool runnn = true;
    	while(runnn)
		{
                	cout<<endl;
                    cout << "What do you want to do in the Product Database:" << endl;
                    cout << "[1] : Create a new product" << endl;
                    cout << "[2] : Edit an existing product"<<endl;
                    cout << "[3] : Delete an existing product" << endl;
                    cout << "[4] : Display all the products" << endl;
                    cout << "[5] : Exit" << endl;
                    cout << "Your Choice :";
                    int choice3_r;
                    cin >> choice3_r;
                    cin.ignore(100, '\n');
                    cout<<endl;

                    if (choice3_r == 1) 
					{
                        cout << "Enter the product ID: ";
                        cin >> productid;
                        cout << "Enter the product name: ";
                        cin.ignore();
                        getline(cin,productname);
                        cout << "Enter the quantity: ";
                        cin >> quantity;
                        cout << "Enter the price: ";
                        cin >> price;
                        cout << "Enter the seller name: ";
                        cin.ignore();
                        getline(cin,seller_name);
                        cout<<endl;

						productname = toUpper(productname);
    					seller_name = toUpper(seller_name);
    					
                        sentence_aux = "INSERT INTO products(productid, productname, quantity, price, seller_name) VALUES (?, ?, ?, ?, ?)";
                        MYSQL_STMT* insert_stmt = mysql_stmt_init(obj);

                        if (!insert_stmt) 
						{
                            cout << "ERROR: Insert statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(insert_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Insert statement preparation failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND insert_bind[5];
                        memset(insert_bind, 0, sizeof(insert_bind));

                        insert_bind[0].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[0].buffer = &productid;

                        insert_bind[1].buffer_type = MYSQL_TYPE_STRING;
                        insert_bind[1].buffer = (void*)productname.c_str();
						insert_bind[1].buffer_length = productname.length();

                        insert_bind[2].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[2].buffer = &quantity;

                        insert_bind[3].buffer_type = MYSQL_TYPE_LONG;
                        insert_bind[3].buffer = &price;

                        insert_bind[4].buffer_type = MYSQL_TYPE_STRING;
                        insert_bind[4].buffer = (void*)seller_name.c_str();
                        insert_bind[4].buffer_length = seller_name.length();

                        if (mysql_stmt_bind_param(insert_stmt, insert_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(insert_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(insert_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "New product added successfully." << endl;
                        }

                        mysql_stmt_close(insert_stmt);
                    }


						if (choice3_r == 2) 
						{
						    cout << "Enter the product ID of the product you want to edit: ";
						    cin >> productid;
						    cout << "What do you want to edit for this product:" << endl;
						    cout << "[1] : Quantity" << endl;
						    cout << "[2] : Price" << endl;
						    cout << "Your Choice :";
						    int choice_edit;
						    cin >> choice_edit;
						    cin.ignore(100, '\n');
						
						    if (choice_edit == 1) 
						    {
						        cout << "Enter the new quantity: ";
						        cin >> quantity;
						        cout << endl;
						
						        sentence_aux = "UPDATE products SET quantity = ? WHERE productid = ?";
						    }
						    else if (choice_edit == 2) 
						    {
						        cout << "Enter the new price: ";
						        cin >> price;
						        cout << endl;
						
						        sentence_aux = "UPDATE products SET price = ? WHERE productid = ?";
						    }
						    else 
						    {
						        cout << "Invalid choice." << endl;
						        continue;  
						    }
						
						    MYSQL_STMT* update_stmt = mysql_stmt_init(obj);
						
						    if (!update_stmt) 
						    {
						        cout << "ERROR: Update statement initialization failed." << endl;
						        return 1;
						    }
						
						    if (mysql_stmt_prepare(update_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						    {
						        cout << "ERROR: Update statement preparation failed: " << mysql_stmt_error(update_stmt) << endl;
						        return 1;
						    }
						
						    MYSQL_BIND update_bind[2];
						    memset(update_bind, 0, sizeof(update_bind));
						
						    if (choice_edit == 1) 
						    {
						        update_bind[0].buffer_type = MYSQL_TYPE_LONG;
						        update_bind[0].buffer = &quantity;
						    }
						    else if (choice_edit == 2) 
						    {
						        update_bind[0].buffer_type = MYSQL_TYPE_LONG;
						        update_bind[0].buffer = &price;
						    }
						
						    update_bind[1].buffer_type = MYSQL_TYPE_LONG;
						    update_bind[1].buffer = &productid;
						
						    if (mysql_stmt_bind_param(update_stmt, update_bind)) 
						    {
						        cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(update_stmt) << endl;
						        return 1;
						    }
						
						    if (mysql_stmt_execute(update_stmt)) 
						    {
						        cout << "ERROR: " << mysql_error(obj) << endl;
						    }
						    else 
						    {
						        cout << "Product updated successfully." << endl;
						    }
						
						    mysql_stmt_close(update_stmt);
						}



                    if (choice3_r == 3) 
					{
                        cout << "Enter the product ID of the product you want to delete: ";
                        cin >> productid;
						cout<<endl;
                        sentence_aux = "DELETE FROM products WHERE productid = ?";
                        MYSQL_STMT* delete_stmt = mysql_stmt_init(obj);

                        if (!delete_stmt) 
						{
                            cout << "ERROR: Delete statement initialization failed." << endl;
                            return 1;
                        }

                        if (mysql_stmt_prepare(delete_stmt, sentence_aux.c_str(), sentence_aux.length())) 
						{
                            cout << "ERROR: Delete statement preparation failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        MYSQL_BIND delete_bind[1];
                        memset(delete_bind, 0, sizeof(delete_bind));

                        delete_bind[0].buffer_type = MYSQL_TYPE_LONG;
                        delete_bind[0].buffer = &productid;

                        if (mysql_stmt_bind_param(delete_stmt, delete_bind)) 
						{
                            cout << "ERROR: Parameter binding failed: " << mysql_stmt_error(delete_stmt) << endl;
                            return 1;
                        }

                        if (mysql_stmt_execute(delete_stmt)) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            cout << "Product deleted successfully." << endl;
                        }

                        mysql_stmt_close(delete_stmt);
                    }

                    if (choice3_r == 4) 
					{
						cout<<endl;
                        cout << "List of all products:" << endl;
                        MYSQL_RES* result_set;
                        MYSQL_ROW row;

                        if (mysql_query(obj, "SELECT * FROM products")) 
						{
                            cout << "ERROR: " << mysql_error(obj) << endl;
                        }
                        else 
						{
                            result_set = mysql_store_result(obj);
                            if (result_set) 
							{
                                while ((row = mysql_fetch_row(result_set))) 
								{
                                    cout << "ProductID: " << row[0] << " | ProductName: " << row[1] << " | Quantity: " << row[2] << " | Price: " << row[3] << " | SellerName: " << row[4] << endl;
                                }
                                mysql_free_result(result_set);
                            }
                            else 
							{
                                cout << "No data found." << endl;
                            }
                        }
                    }

                    if (choice3_r == 5) 
					{
                        runnn = false;
                    }
                }
	}

    mysql_close(obj);
    return 0;
}

