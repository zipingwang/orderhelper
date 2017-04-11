package com.eurotong.orderhelperandroid;
import java.io.*;

import android.util.Log;

public class BusinessInfo {
	
     private String businessName="";
    
     private String nameChinese = "";
     
     private String street = "";

    
     private String number = "";

     
     private String postCode = "";

   
     private String place = "";

   
     private String country = "";

    
     private String currency = "";

    
     private String telephoneNumber = "";

     
     private String faxNumber = "";

  
     private String taxNr = "";

   
     private String emailAddress = "";

    
     private String password = "";

    
     private String greetingLine1 = "";

    
     private String greetingLine2 = "";
     
     private String customerID = "";
     
     private double tax1=-1;
     
     private double tax2=-1;
     
     private double tax3=-1;
     
     private double tax4=-1;

     private double takeAwayPercent=-1;

    private  boolean hasTakeAwayPercent=false;

     /**
 	 * @return the businessName
 	 */
 	public String getBusinessName() {
 		return businessName;
 	}


 	/**
 	 * @param businessName the businessName to set
 	 */
 	public void setBusinessName(String businessName) {
 		this.businessName = businessName;
 	}
 	
	public String getNameChinese() {
		return nameChinese;
	}


	public void setNameChinese(String nameChinese) {
		this.nameChinese = nameChinese;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	private String getPostCode() {
		return postCode;
	}


	private void setPostCode(String postCode) {
		this.postCode = postCode;
	}


	public String getPlace() {
		return place;
	}


	public void setPlace(String place) {
		this.place = place;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getTelephoneNumber() {
		return telephoneNumber;
	}


	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}


	public String getFaxNumber() {
		return faxNumber;
	}


	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}


	public String getTaxNr() {
		return taxNr;
	}


	public void setTaxNr(String taxNr) {
		this.taxNr = taxNr;
	}


	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getGreetingLine1() {
		return greetingLine1;
	}


	public void setGreetingLine1(String greetingLine1) {
		this.greetingLine1 = greetingLine1;
	}


	public String getGreetingLine2() {
		return greetingLine2;
	}


	public void setGreetingLine2(String greetingLine2) {
		this.greetingLine2 = greetingLine2;
	}
	
	public String getCustomerID() {
		return customerID;
	}


	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public double getTax1()
	{
		return this.tax1;
	}
	
	public void setTax1(double value)
	{
		this.tax1=value;
	}

	public double getTax2()
	{
		return this.tax2;
	}
	
	public void setTax2(double value)
	{
		this.tax1=value;
	}
	
	public double getTax3()
	{
		return this.tax3;
	}
	
	public void setTax3(double value)
	{
		this.tax3=value;
	}

    public double getTax4()
    {
        return this.tax4;
    }

    public void setTax4(double value)
    {
        this.tax4=value;
    }
	
	public double getTakeAwayPercent()
	{
		return this.takeAwayPercent;
	}
	
	public void setTakeAwayPercent(double value)
	{
		this.takeAwayPercent=value;
	}

    public boolean getHasTakeProcent()
    {
        if(takeAwayPercent<0)
            return  false;
        else
            return  true;
    }
	
	static BusinessInfo _bisInfo;
    public static BusinessInfo Current()
    {
       
            if (_bisInfo == null)
            {
            	 if (Common.ExistsInStorage(Define.SETTING_FILE_NAME))
                 //load from isolated storage
                 {                   
                     try {
                    	 _bisInfo = BusinessInfo.Parse(Common.GetFileInputStreamFromStorage(Define.BUSINESS_INFO_FILE_NAME));
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
                 }
                 else
                 {
                	 _bisInfo = new BusinessInfo();
                 }
              
            }
            return _bisInfo;        
    }
    
    public static BusinessInfo Parse(FileInputStream inputStream) throws Exception
    {    	
    	BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode"));       
    	BusinessInfo bi = new BusinessInfo();       
        String line = sr.readLine();
        while (line != null)
        {
        	
            if (Common.IsDataLine(line)==true)
            {
                String[] items = Common.SplitStringInTwo(line, Define.SEPERATOR);
                
	            String item=items[0].toLowerCase();
	            	
                if(item.equals("businessname")){
                    bi.setBusinessName(items[1].trim());
                }else
                if(item.equals("namechinese")){
                    bi.setNameChinese (items[1].trim());
                    }else
                if(item.equals("street")){
                    bi.setStreet(items[1].trim());
                    }else
                if(item.equals("number")){
                    bi.setNumber(items[1].trim());
                    }else
                if(item.equals("postcode")){
                    bi.setPostCode(items[1].trim());
                    }else
                if(item.equals("place")){
                    bi.setPlace(items[1].trim());
                    }else
                if(item.equals("telephonenumber")){
                    bi.setTelephoneNumber(items[1].trim());
                    }else
                if(item.equals("faxnumber")){
                    bi.setFaxNumber(items[1].trim());
                    }else
                if(item.equals("taxnr")){
                    bi.setTaxNr(items[1].trim());
                    }else
                if(item.equals("emailaddress")){
                    bi.setEmailAddress(items[1].trim());
                    }else
                if(item.equals("password")){
                    bi.setPassword(items[1].trim());
                    }else
                if(item.equals("greetingline1")){
                    bi.setGreetingLine1(items[1].trim());
                    }else
                if(item.equals("greetingline2")){
                    bi.setGreetingLine2(items[1].trim());
                    }else
                if(item.equals("currency")){
                    bi.setCurrency(items[1].trim());
                    }else
                if(item.equals("country")){
                    bi.setCountry(items[1].trim());
                    }
                else
                    if(item.equals("customerid")){
                        bi.setCustomerID(items[1].trim());
                        }
                else
                    if(item.equals("tax1")){
                      bi.setTax1(Common.GetDoubleResult(items[1].trim(), -1));
                        }
                else
                    if(item.equals("tax2")){
                      bi.setTax2(Common.GetDoubleResult(items[1].trim(), -1));
                        }
                else
                    if(item.equals("tax3")){
                      bi.setTax3(Common.GetDoubleResult(items[1].trim(), -1));
                        }
                else
                    if(item.equals("tax4")){
                      bi.setTax2(Common.GetDoubleResult(items[1].trim(), -1));
                        }
                else
                    if(item.equals("takeawaypercent")){
                        bi.setTakeAwayPercent(Common.GetDoubleResult(items[1].trim(), -1));
                    }
                   else
                   {
                	Log.e(Define.APP_CATALOG, "item not found in parse setting:" + items[0].trim().toLowerCase() + line);
                   }                    
                }            
            
            line = sr.readLine();
        }
        sr.close();
        return bi;
    }
    
    /// <summary>
    /// get value for printing. not use reflection. easy to reuse to android
    /// </summary>
    /// <param name="propertyName"></param>
    /// <returns></returns>
    public String GetPropertyStringValue(String propertyName)
    {
        String value = "Not Found";
        String item=propertyName.trim().toLowerCase();
        //http://stackoverflow.com/questions/3576413/why-is-my-string-to-string-comparison-failing
        //In Java, one of the most common mistakes newcomers meet is using == to compare Strings. You have to remember, == compares the object references, not the content.
        if(item.equals("businessname")){
                value=getBusinessName();
                }
            else if (item.equals("namechinese")){
                value=getNameChinese();
                }
            else if (item.equals("street")){
                value=getStreet();
                }
            else if (item.equals("number")){
                value=getNumber();
                }
            else if (item.equals("postcode")){
                value=getPostCode();
                }
            else if (item.equals("place")){
                value=getPlace();
                }
            else if (item.equals("telephonenumber")){
                value=getTelephoneNumber();
                }
            else if (item.equals("faxnumber")){
                value=getFaxNumber();
                }
            else if (item.equals("taxnr")){
                value=getTaxNr();
                }
            else if (item.equals("emailaddress")){
                value=getEmailAddress();
                }
            else if (item.equals("password")){
                value=getPassword();
                }
            else if (item.equals("greetingline1")){
                value=getGreetingLine1();
                }
            else if (item.equals("greetingline2")){
                value=getGreetingLine2();
                }
            else if (item.equals("currency")){
                value=getCurrency();
                }
            else if (item.equals("country")){
                value=getCountry();
                }
            else if (item.equals("customerid")){
                value=getCountry();
                }
            else if (item.equals("tax1")){
                value=Common.FormatDouble(this.tax1);
                }
            else if (item.equals("tax2")){
                value=Common.FormatDouble(this.tax2);
                }
            else if (item.equals("tax3")){
                value=Common.FormatDouble(this.tax3);
                }
            else if (item.equals("tax4")){
                value=Common.FormatDouble(this.tax4);
                }        
            else
            {
            	Common.GetToastLong("item not found in GetPropertyStringValue:" + item).show();
            }
       
        return value;
    }

    public static void Reload()
    {
    	_bisInfo=null;       
    }
}
