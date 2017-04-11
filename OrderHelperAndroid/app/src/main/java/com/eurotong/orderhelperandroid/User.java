package com.eurotong.orderhelperandroid;

public class User
{
    //private String _userName;
    //private String _password;
    private static User _current;

    private User()
    {
    	UserName = "";
    	Password = "";
    }

    public static User Current()
    {
        
        if (_current == null)
        {
            _current = new User();
        }
        return _current;
        
    }

    public String UserName;
    

    public String Password;
  

    public boolean IsAdmin()
    {
        //return true;
        
    	boolean isadimn = false;
            if (UserName.equals("admin") && Password.equals("87708800"))
            {
                isadimn = true;
            }
            return isadimn;
            
        
    }

    public boolean IsOwner()
    {
       
    	boolean isOwner = false;
    	//  if (Password.equals(Setting.Current().get_ownerPassword()))
        if (Password.equals("1234"))
        {
            isOwner = true;
        }
        return isOwner;
        
    }
    
    public boolean IsInstaller()
    {
       
    	boolean isInstaller = false;
            if (Password.equals("7890"))
            {
                isInstaller = true;
            }
            return isInstaller;
        
    }

    public boolean IsOperator()
    {       
    	boolean isOperator = true;
            //if (Password.Equals("7890"))
            //{
            //    isOperator = true;
            //}
       return isOperator;
    }
    
    public Boolean HasRight(int operation)
    {
        Boolean hasRight = false;
        if (operation == Define.UR_ADVANCED_OPERATION)
        {
            if (IsAdmin() || IsInstaller() || IsOwner())
            {
                hasRight= true;
            }
        }
        else
            if (operation == Define.UR_CONFIGURE_PARAMETERS)
            {
                if (IsAdmin())
                {
                    hasRight = true;
                }
            }
        else
            if (operation == Define.UR_DEBUG_PRINT_LAYOUT)
            {
                if (IsAdmin() || IsInstaller())
                {
                    hasRight = true;
                }
            }
        else
            if (operation == Define.UR_DELETE_ALL_TABLES)
            {                   
                    hasRight = true;
            }
        else
            if (operation == Define.UR_PRINT)
            {
                hasRight = true;
            }
        else
            if (operation == Define.UR_PRINT_BAR)
            {
                hasRight = true;
            }
        else
            if (operation == Define.UR_PRINT_KITCHEN)
            {
                hasRight = true;
            }
        return hasRight;
    }
}